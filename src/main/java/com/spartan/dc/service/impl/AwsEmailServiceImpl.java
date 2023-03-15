package com.spartan.dc.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.spartan.dc.core.enums.DcMailConfTypeEnum;
import com.spartan.dc.model.DcMailConf;
import com.spartan.dc.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author syt
 * @Created by 2019-10-18 11:29
 */
@Service
public class AwsEmailServiceImpl implements EmailService {

    @Override
    public String fetchKey() {
        return DcMailConfTypeEnum.AWSMAIL.getName();
    }

    @Override
    public boolean sendHtmlEmail(String from, String[] to, String[] cc, String subject, String content, Map<String, String> inputStreamFileList, DcMailConf dcMailConf) {
        return sendEmail(from, to, cc, subject, content, true, inputStreamFileList, dcMailConf);
    }


    private boolean sendEmail(String from, String[] to, String[] cc, String subject, String content, boolean isHtml, Map<String, String> fileBase64StrMap, DcMailConf dcMailConf) {
        if (from == null || from.length() == 0) {
            return false;
        }
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            if (cc != null && cc.length > 0) {
                simpleMailMessage.setCc(cc);
            }
            simpleMailMessage.setFrom(from);
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(content);

            Destination destination = new Destination();
            List<String> toAddresses = new ArrayList<>();
            String[] receivers = simpleMailMessage.getTo();
            Collections.addAll(toAddresses, Objects.requireNonNull(receivers));
            destination.setToAddresses(toAddresses);

            if (cc != null && cc.length > 0) {
                List<String> ccAddresses = new ArrayList<>();
                String[] ccs = simpleMailMessage.getCc();
                Collections.addAll(ccAddresses, Objects.requireNonNull(ccs));
                destination.setCcAddresses(ccAddresses);
            }

            SendEmailRequest sendEmailRequest = new SendEmailRequest();
            sendEmailRequest.setDestination(destination);
            sendEmailRequest.withSource(simpleMailMessage.getFrom());

            Content contentData = new Content();
            contentData.setData(simpleMailMessage.getSubject());
            contentData.setCharset("utf-8");

            Content bodyContent = new Content(simpleMailMessage.getText());
            Body body = new Body();
            body.setHtml(bodyContent);

            Message message = new Message();
            message.setSubject(contentData);
            message.setBody(body);

            sendEmailRequest.setMessage(message);


            AmazonSimpleEmailService amazonSimpleEmailService = amazonSimpleEmailService(dcMailConf);
            amazonSimpleEmailService.sendEmail(sendEmailRequest);
            return true;
        } catch (AmazonSimpleEmailServiceException e) {
            e.printStackTrace();
            return false;
        }
    }


    public AmazonSimpleEmailService amazonSimpleEmailService(DcMailConf dcMailConf) {
        if (Objects.nonNull(dcMailConf)) {
            BasicAWSCredentials credentials = new BasicAWSCredentials(dcMailConf.getAccessKey(), dcMailConf.getSecretKey());
            return AmazonSimpleEmailServiceClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(dcMailConf.getRegionStatic())
                    .withRequestHandlers()
                    .build();
        }
        return null;
    }
}
