package com.spartan.dc.service;


import com.spartan.dc.core.dto.portal.SendMessageReqVO;

/**
 * Desc：
 *
 * @Created by 2022-07-26 19:06
 */
public interface SendMessageService {

    boolean sendMessage(SendMessageReqVO sendMessageReqVO);

    boolean sendMessageByContent(String title,String messageContent,String to);
}
