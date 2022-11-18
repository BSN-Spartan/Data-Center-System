package com.spartan.dc.service;

import java.util.Map;

/**
 *
 *
 * @Author syt
 * @Created by 2019-10-18 11:07
 */
public interface EmailService {


    boolean sendHtmlEmail(String from, String[] to, String[] cc, String subject, String content,  Map<String, String> fileBase64StrMap);

}
