package com.spartan.dc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.spartan.dc.core.dto.portal.SendMessageReqVO;
import com.spartan.dc.core.enums.DcSystemConfTypeEnum;
import com.spartan.dc.core.enums.MessageTypeEnum;
import com.spartan.dc.core.enums.MsgCodeEnum;
import com.spartan.dc.core.enums.SystemConfCodeEnum;
import com.spartan.dc.core.util.message.MessageTemplateUtil;
import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.dao.write.SysMailRecordMapper;
import com.spartan.dc.dao.write.SysMessageTemplateMapper;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.model.SysMailRecord;
import com.spartan.dc.model.SysMessageTemplate;
import com.spartan.dc.service.EmailService;
import com.spartan.dc.service.SendMessageService;
import com.spartan.dc.tread.DefaultThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Desc：
 *
 * @Created by 2022-07-26 19:07
 */
@Service
@Slf4j
public class SendMessageServiceImpl implements SendMessageService {


    @Autowired
    private SysMessageTemplateMapper sysMessageTemplateMapper;

    @Autowired
    private SysMailRecordMapper sysMailRecordMapper;

    @Resource
    private DcSystemConfMapper dcSystemConfMapper;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;


    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${system.iconBase64}")
    private String iconBase64;

    public static final int MAIL_CONTENT_MAX_LENGTH = 1000;
    public static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("sysMessage-%d").build();
    public static  ExecutorService executorService = new ThreadPoolExecutor(5, 10, 1L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), namedThreadFactory);

    @Override
    public boolean sendMessage(SendMessageReqVO sendMessageReqVO) {
        executorService.submit(() -> {
            List<SysMessageTemplate> messageTemplateList = sysMessageTemplateMapper.listByCode(sendMessageReqVO.getMsgCode());
            if (CollectionUtils.isEmpty(messageTemplateList)) {
                log.error("Send email error message template is empty.");
                return;
            }
            // Modify title and content
            log.info("Send email------{}", JSONArray.toJSONString(messageTemplateList));

            // Send email
            List<String> receivers = sendMessageReqVO.getReceivers();
            if (!CollectionUtils.isEmpty(receivers)) {
                SysMessageTemplate messageTemplate = MessageTemplateUtil.getMsgByCode(messageTemplateList, MessageTypeEnum.EMAIL.getCode());
                if (!Objects.isNull(messageTemplate)) {

                    // Get the initial template of the email
                    String emailTemplate = getEmailContent(MsgCodeEnum.EMAIL_TEMPLATE.getCode());
                    log.info("Get the initial template of the email【{}】", emailTemplate);
                    // Email Contact Template
                    String emailContactTemplate = getContactsTemplate();
                    log.info("Email Contact Template【{}】", emailContactTemplate);

                    // Email content
                    messageTemplate = MessageTemplateUtil.replaceMsg(messageTemplate, sendMessageReqVO.getReplaceTitleMap(), sendMessageReqVO.getReplaceContentMap());
                    String[] cc = null;
                    if (!CollectionUtils.isEmpty(sendMessageReqVO.getCc())) {
                        cc = sendMessageReqVO.getCc().toArray(new String[0]);
                    }
                    //
                    String msgContent = MessageTemplateUtil.initEmailContent(emailTemplate, messageTemplate.getMsgContent(), emailContactTemplate);

                    // replace message content
                    String dcCenterName = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.PORTAL_INFORMATION.getCode(), SystemConfCodeEnum.HEADLINE.getCode());
                    String msgTitle = messageTemplate.getMsgTitle();
                    if(StringUtils.isNotBlank(dcCenterName)){
                        msgTitle = dcCenterName + ": " + msgTitle;
                        //msgTitle = msgTitle.replace("BSN Spartan Data Center",dcCenterName);
                    }
                    SysDataCenter sysDataCenter= sysDataCenterMapper.getSysDataCenter();
                    if (StringUtils.isNotBlank(sysDataCenter.getLogo())) {
                        msgContent = msgContent.replace("${logo}",sysDataCenter.getLogo());
                    }else {
                        msgContent = msgContent.replace("${logo}", iconBase64);
                    }

                    log.info("Sending email content......【{}】", msgContent);

                    String mailContent = messageTemplate.getMsgContent();
                    if (mailContent.length() > MAIL_CONTENT_MAX_LENGTH) {
                        mailContent = mailContent.substring(0, MAIL_CONTENT_MAX_LENGTH);
                    }

                    SysMailRecord mailRecord = SysMailRecord.builder()
                            .templateId(messageTemplate.getTemplateId())
                            .mailTitle(msgTitle)
                            .mailContent(mailContent)
                            .sender(from)
                            .receiver(JSONArray.toJSONString(receivers))
                            .ccPeople(cc == null ? "" : JSONArray.toJSONString(sendMessageReqVO.getCc()))
                            .sendTime(new Date())
                            .state((short) 0)
                            .build();
                    // save
                    sysMailRecordMapper.insertSelective(mailRecord);

                    // Sending
                    boolean sendState = false;
                    try {
                        sendState = emailService.sendHtmlEmail(from, sendMessageReqVO.getReceivers().toArray(new String[0]), cc, msgTitle, msgContent, sendMessageReqVO.getFileBase64StrMap());
                    } catch (Exception e) {
                        log.info("Failed to send the email:【{}】", JSONObject.toJSONString(sendMessageReqVO), e);
                    }


                    if (sendState) {
                        // Update email status
                        SysMailRecord updateMailRecord = SysMailRecord.builder().state((short) 1).recordId(mailRecord.getRecordId()).build();
                        sysMailRecordMapper.updateByPrimaryKeySelective(updateMailRecord);
                    }

                }
            }});
        return true;
    }


    private String getEmailContent(String messageCode) {
        String emailContent = null;//redisUtils.get(messageCode);
        if (emailContent == null || emailContent.length() == 0) {
            List<SysMessageTemplate> emailTemplateList = sysMessageTemplateMapper.listByCode(messageCode);
            if (!CollectionUtils.isEmpty(emailTemplateList)) {
                emailContent = emailTemplateList.get(0).getMsgContent();
//                redisUtils.set(messageCode, emailContent);
            }
        }

//        String dcCenterName = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.PORTAL_INFORMATION.getCode(), SystemConfCodeEnum.TITLE.getCode());
//        if (StringUtils.isNotBlank(dcCenterName)) {
//            emailContent = emailContent.replace("BSN Spartan Network", dcCenterName);
//        }
        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (StringUtils.isNotBlank(sysDataCenter.getLogo())) {
            emailContent = emailContent.replace("${logo}", sysDataCenter.getLogo());
        }else {
            emailContent = emailContent.replace("${logo}", iconBase64);
        }
        return emailContent;
    }


    @Override
    public boolean sendMessageByContent(String title, String messageContent, String to) {

        DefaultThreadPool.asyncTask(() -> {

            // Send email
            List<String> receivers = new ArrayList<>();
            receivers.add(to);
            if (!CollectionUtils.isEmpty(receivers)) {

                // Get the initial template of the email
                String emailTemplate = getEmailContent(MsgCodeEnum.EMAIL_TEMPLATE.getCode());
                log.info("Get the initial template of the email【{}】", emailTemplate);
                // Email Contact Template
                String emailContactTemplate = getContactsTemplate();
                log.info("Email Contact Template【{}】", emailContactTemplate);

                String[] cc = null;

                //
                String msgContent = MessageTemplateUtil.initEmailContent(emailTemplate, messageContent, emailContactTemplate);
                log.info("Sending email content......【{}】", msgContent);

                String mailContent = messageContent;
                if (mailContent.length() > MAIL_CONTENT_MAX_LENGTH) {
                    mailContent = mailContent.substring(0, MAIL_CONTENT_MAX_LENGTH);
                }

                SysMailRecord mailRecord = SysMailRecord.builder()
                        .templateId(Long.valueOf(00))
                        .mailTitle(title)
                        .mailContent(mailContent)
                        .sender(from)
                        .receiver(JSONArray.toJSONString(receivers))
                        .ccPeople(String.valueOf(cc))
                        .sendTime(new Date())
                        .state((short) 0)
                        .build();
                // save
                sysMailRecordMapper.insertSelective(mailRecord);

                // Sending
                boolean sendState = false;
                try {
                    sendState = emailService.sendHtmlEmail(from, receivers.toArray(new String[0]), cc, title, msgContent, null);
                } catch (Exception e) {
                    log.info("Failed to send the email:【{}】", JSONObject.toJSONString(null), e);
                }


                if (sendState) {
                    // Update email status
                    SysMailRecord updateMailRecord = SysMailRecord.builder().state((short) 1).recordId(mailRecord.getRecordId()).build();
                    sysMailRecordMapper.updateByPrimaryKeySelective(updateMailRecord);
                }
            }
            return true;
        });

        return true;
    }


    private String getContactsTemplate() {

        // Email Contact Template
        String emailContactTemplate = "<tr><td style=\"font-size: 14px; color: #000000;padding-top: 15px;padding-bottom: 15px;\">\n" +
                "<span>Thank you for using BSN Spartan Network!</span></td></tr><tr><td style=\"font-size: 14px; color: #000000;\">\n" +
                "<span>If you have any questions, please contact us by the following ways:</span></td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">";

        String contactBody = "";
        // Email Ender
        String Telegram = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.TECHNICAL_SUPPORT.getCode(), SystemConfCodeEnum.TELEGRAM.getCode());
        String Whatsapp = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.TECHNICAL_SUPPORT.getCode(), SystemConfCodeEnum.WHATSAPP.getCode());
        String Reddit = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.TECHNICAL_SUPPORT.getCode(), SystemConfCodeEnum.REDDIT.getCode());
        String Discord = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.TECHNICAL_SUPPORT.getCode(), SystemConfCodeEnum.DISCORD.getCode());
        String Twitter = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.TECHNICAL_SUPPORT.getCode(), SystemConfCodeEnum.TWITTER.getCode());
        String Email = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.CONTACT_US.getCode(), SystemConfCodeEnum.EMAIL.getCode());
        String dcCenterName = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.PORTAL_INFORMATION.getCode(), SystemConfCodeEnum.TITLE.getCode());


        if (StringUtils.isNotBlank(Telegram)) {
            contactBody = contactBody + "-Telegram:&nbsp;<a target=\"_blank\" href=\"${Telegram}\">${telegram}</a></td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">".replace("${telegram}", Telegram);
        }

        if (StringUtils.isNotBlank(Whatsapp)) {
            contactBody = contactBody + "-Whatsapp:&nbsp;<a target=\"_blank\" href=\"${Whatsapp}\">${Whatsapp}</td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">".replace("${Whatsapp}", Whatsapp);
        }
        if (StringUtils.isNotBlank(Reddit)) {
            contactBody = contactBody + "-Reddit:&nbsp;<a target=\"_blank\" href=\"${Reddit}\">${Reddit}</a></td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">".replace("${Reddit}", Reddit);
        }
        if (StringUtils.isNotBlank(Discord)) {
            contactBody = contactBody + "-Discord:&nbsp;<a target=\"_blank\" href=\"${Discord}\">${Discord}</a></td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">".replace("${Discord}", Discord);
        }
        if (StringUtils.isNotBlank(Twitter)) {
            contactBody = contactBody + "-Twitter:&nbsp;<a target=\"_blank\" href=\"${Twitter}\">${Twitter}</a></td></tr><tr><td style=\"font-size: 14px; padding-top: 5px;\">".replace("${Twitter}", Twitter);
        }
        if (StringUtils.isNotBlank(Email)) {
            contactBody = contactBody + "-Email:&nbsp;<a href=\"${Email}\">${Email}</td></tr><tr><td>&nbsp;</td></tr>".replace("${Email}", Email);
        }
//        if (StringUtils.isNotBlank(dcCenterName)) {
//            emailContactTemplate = emailContactTemplate.replace("BSN Spartan Network", dcCenterName);
//        }

        if (contactBody.length() == 0) {
            emailContactTemplate = getEmailContent(MsgCodeEnum.EMAIL_CONTACT_TEMPLATE_.getCode());
        } else {
            emailContactTemplate = emailContactTemplate + contactBody;
        }
        return emailContactTemplate;
    }
}
