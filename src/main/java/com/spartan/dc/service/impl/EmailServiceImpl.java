package com.spartan.dc.service.impl;

import com.spartan.dc.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 *
 *
 * @Author syt
 * @Created by 2019-10-18 11:29
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${sys-name}")
    private String sysName;

    @Autowired
    private JavaMailSender mailSender;


    @Override
    public boolean sendHtmlEmail(String from, String[] to, String[] cc, String subject, String content,Map<String, String> inputStreamFileList) {
        return sendEmail(from, to, cc, subject, content, true,inputStreamFileList);
    }


    private boolean sendEmail(String from, String[] to, String[] cc, String subject, String content, boolean isHtml,  Map<String, String> fileBase64StrMap) {
        if (from == null || from.length() == 0) {
            return false;
        }
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            if (cc != null && cc.length > 0) {
                helper.setCc(cc);
            }
            helper.setSubject(subject);
            helper.setText(content, isHtml);
            helper.setFrom(new InternetAddress(from, sysName, "UTF-8"));

            if (Objects.nonNull(fileBase64StrMap)&& fileBase64StrMap.size() > 0) {
                for (String fileName : fileBase64StrMap.keySet()) {
                    helper.addAttachment(fileName, new ByteArrayResource(Base64Utils.decodeFromString(fileBase64StrMap.get(fileName))));
                }
            }

            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
