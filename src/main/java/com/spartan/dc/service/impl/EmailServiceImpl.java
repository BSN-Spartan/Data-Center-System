package com.spartan.dc.service.impl;

import com.alibaba.fastjson.JSON;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.model.DcMailConf;
import com.spartan.dc.service.EmailService;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @Author syt
 * @Created by 2019-10-18 11:29
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Override
    public String fetchKey() {
        return DcMailConfTypeEnum.MAIL.getName();
    }

    @Override
    public boolean sendHtmlEmail(String from, String[] to, String[] cc, String subject, String content, Map<String, String> inputStreamFileList, DcMailConf dcMailConf) {
        return sendEmail(from, to, cc, subject, content, true, inputStreamFileList, dcMailConf);
    }


    private boolean sendEmail(String from, String[] to, String[] cc, String subject, String content, boolean isHtml, Map<String, String> fileBase64StrMap, DcMailConf dcMailConf) {
        JavaMailSenderImpl javaMailSender = javaMailSender(dcMailConf);

        if (from == null || from.length() == 0) {
            return false;
        }
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            if (cc != null && cc.length > 0) {
                helper.setCc(cc);
            }
            helper.setSubject(subject);
            helper.setText(content, isHtml);
            helper.setFrom(new InternetAddress(from, "", "UTF-8"));

            if (Objects.nonNull(fileBase64StrMap) && fileBase64StrMap.size() > 0) {
                for (String fileName : fileBase64StrMap.keySet()) {
                    helper.addAttachment(fileName, new ByteArrayResource(Base64Utils.decodeFromString(fileBase64StrMap.get(fileName))));
                }
            }

            javaMailSender.send(mimeMessage);
            return true;
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private JavaMailSenderImpl javaMailSender(DcMailConf dcMailConf) {
        MailProperties properties = new MailProperties();
        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        if (Objects.nonNull(dcMailConf)) {
            properties.setHost(dcMailConf.getMailHost());
            properties.setUsername(dcMailConf.getMailUserName());
            properties.setPassword(dcMailConf.getMailPassWord());
            if (!"".equals(dcMailConf.getMailPort())) {
                properties.setPort(Integer.parseInt(dcMailConf.getMailPort().trim()));
            }
            this.applyProperties(properties, sender,dcMailConf);
        }
        return sender;
    }

    private void applyProperties(MailProperties properties, JavaMailSenderImpl sender,DcMailConf dcMailConf) {
        sender.setHost(properties.getHost());
        if (properties.getPort() != null) {
            sender.setPort(properties.getPort());
        }

        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());
        if (properties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(properties.getDefaultEncoding().name());
        }

        if (!dcMailConf.getProperties().isEmpty()) {
            sender.setJavaMailProperties(this.asProperties(dcMailConf.getProperties()));
        }

    }

    private Properties asProperties(String conf) {
        Map propertiesMap = (Map)JSON.parse(conf);
        Properties properties = new Properties();
        properties.putAll(propertiesMap);
        return properties;
    }

}
