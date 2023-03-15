package com.spartan.dc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.dto.portal.SendMessageReqVO;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.AesUtil;
import com.spartan.dc.core.util.common.CacheManager;
import com.spartan.dc.dao.write.DcMailConfMapper;
import com.spartan.dc.model.DcMailConf;
import com.spartan.dc.model.vo.req.DcMailConfReqVO;
import com.spartan.dc.service.MailConfigurationService;
import com.spartan.dc.service.SendMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName MailConfigurationServicelmpl
 * @Author wjx
 * @Date 2023/3/7 10:38
 * @Version 1.0
 */
@Service
public class MailConfigurationServicelmpl implements MailConfigurationService {
    @Autowired
    private SendMessageService sendMessageService;

    @Resource
    private DcMailConfMapper dcMailConfMapper;

    @Override
    public ResultInfo sendTest(DcMailConfReqVO dcMailConfReqVO) {
        SendMessageReqVO sendMessageReqVO = new SendMessageReqVO();
        DcMailConf dcMailConf = DcMailConf.builder()
                .mailHost(dcMailConfReqVO.getMailHost().trim())
                .mailPort(dcMailConfReqVO.getMailPort().trim())
                .mailUserName(dcMailConfReqVO.getMailUserName().trim())
                .mailPassWord(dcMailConfReqVO.getMailPassWord().trim())
                .properties(dcMailConfReqVO.getProperties().trim())
                .type(dcMailConfReqVO.getType())
                .accessKey(dcMailConfReqVO.getAccessKey().trim())
                .secretKey(dcMailConfReqVO.getSecretKey().trim())
                .regionStatic(dcMailConfReqVO.getRegionStatic().trim())
                .build();

        List<String> receivers = Lists.newArrayList();
        receivers.add(dcMailConfReqVO.getReceiver());

        List<String> cc = Lists.newArrayList();
        cc.add(dcMailConfReqVO.getCc());

        sendMessageReqVO.setMsgCode(MsgCodeEnum.EMAIL_SEND_TEST_.getCode());
        sendMessageReqVO.setReceivers(receivers);
        if (!cc.get(0).equals("")) {
            sendMessageReqVO.setCc(cc);
        }
        sendMessageService.sendMessageTest(sendMessageReqVO, dcMailConf);
        return ResultInfoUtil.successResult("Success");
    }

    @Override
    @Transactional
    public ResultInfo updateSendMode(DcMailConfReqVO dcMailConfReqVO) throws Exception {
        if (Objects.isNull(dcMailConfReqVO.getType())) {
            throw new GlobalException("Email type is not selected");
        }
        String springMailKey = null;
        String awsAccessKey = null;
        String awsSecretKey = null;

        if (dcMailConfReqVO.getType().equals(DcMailConfTypeEnum.AWSMAIL.getCode())) {
            if (!dcMailConfReqVO.getAccessKey().isEmpty()) {
                awsAccessKey = AesUtil.encrypt(dcMailConfReqVO.getAccessKey().trim(), MailConfSecretKeyEnum.AWS_ACCESS_KEY.getCode());
            }

            if (!dcMailConfReqVO.getSecretKey().isEmpty()) {
                awsSecretKey = AesUtil.encrypt(dcMailConfReqVO.getSecretKey().trim(), MailConfSecretKeyEnum.AWS_SECRET_KEY.getCode());
            }
        } else {
            if (!dcMailConfReqVO.getMailPassWord().isEmpty()) {
                springMailKey = AesUtil.encrypt(dcMailConfReqVO.getMailPassWord().trim(), MailConfSecretKeyEnum.SPRING_MAIL_KEY.getCode());
            }
        }

        DcMailConf dcMailConf = DcMailConf.builder()
                .mailHost(dcMailConfReqVO.getMailHost().trim())
                .mailPort(dcMailConfReqVO.getMailPort().trim())
                .mailUserName(dcMailConfReqVO.getMailUserName().trim())
                .mailPassWord(springMailKey)
                .properties(dcMailConfReqVO.getProperties().trim())
                .type(dcMailConfReqVO.getType())
                .accessKey(awsAccessKey)
                .secretKey(awsSecretKey)
                .regionStatic(dcMailConfReqVO.getRegionStatic().trim())
                .state(DcMailConfStateEnum.ENABLE.getCode())
                .updateTime(new Date())
                .build();


        DcMailConf conf = dcMailConfMapper.queryMailConfType(dcMailConf.getType());
        if (Objects.isNull(conf)) {
            dcMailConf.setCreateTime(new Date());
            dcMailConfMapper.insertSelective(dcMailConf);
            CacheManager.put("dcMailConf", JSONObject.toJSONString(dcMailConf));
        } else {
            dcMailConf.setMailConfId(conf.getMailConfId());
            dcMailConfMapper.updateByPrimaryKeySelective(dcMailConf);
            CacheManager.put("dcMailConf", JSONObject.toJSONString(dcMailConf));
        }
        for (DcMailConfTypeEnum e : DcMailConfTypeEnum.values()) {
            if (!dcMailConfReqVO.getType().equals(e.getCode())) {
                DcMailConf selectMailType = dcMailConfMapper.queryMailConfType(e.getCode());
                if (Objects.isNull(selectMailType)) {
                    continue;
                }
                DcMailConf mailConf = new DcMailConf();
                mailConf.setMailConfId(selectMailType.getMailConfId());
                mailConf.setState(DcMailConfStateEnum.DISABLE.getCode());
                dcMailConfMapper.updateByPrimaryKeySelective(mailConf);
            }
        }
        return ResultInfoUtil.successResult("Success");
    }

    @Override
    public ResultInfo<List<DcMailConf>> queryConf() throws Exception {
        String springMailKey = null;
        String awsAccessKey = null;
        String awsSecretKey = null;
        List<DcMailConf> conf = dcMailConfMapper.selectMailConf();
        for (DcMailConf dcMailConf : conf) {
            if (dcMailConf.getType().equals(DcMailConfTypeEnum.AWSMAIL.getCode())) {
                if (!dcMailConf.getAccessKey().isEmpty()) {
                    awsAccessKey = AesUtil.decrypt(dcMailConf.getAccessKey().trim(), MailConfSecretKeyEnum.AWS_ACCESS_KEY.getCode());
                    dcMailConf.setAccessKey(awsAccessKey);
                }

                if (!dcMailConf.getSecretKey().isEmpty()) {
                    awsSecretKey = AesUtil.decrypt(dcMailConf.getSecretKey().trim(), MailConfSecretKeyEnum.AWS_SECRET_KEY.getCode());
                    dcMailConf.setSecretKey(awsSecretKey);
                }
            } else {
                if (!dcMailConf.getMailPassWord().isEmpty()) {
                    springMailKey = AesUtil.decrypt(dcMailConf.getMailPassWord().trim(), MailConfSecretKeyEnum.SPRING_MAIL_KEY.getCode());
                    dcMailConf.setMailPassWord(springMailKey);
                }
            }
        }

        boolean match = conf.stream()
                .anyMatch(dcMailConf -> Objects.equals(DcMailConfTypeEnum.MAIL.getCode(), dcMailConf.getType()));
        if (!match) {
            DcMailConf dcMailConf = DcMailConf.builder()
                    .type(DcMailConfTypeEnum.MAIL.getCode())
                    .properties("{\"mail.smtp.auth\":true,\"mail.smtp.starttls.enable\":true}")
                    .build();
            conf.add(dcMailConf);
        }

        return ResultInfoUtil.successResult(conf);
    }
}
