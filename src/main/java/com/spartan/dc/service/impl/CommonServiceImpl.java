package com.spartan.dc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.spartan.dc.core.dto.portal.SendMessageReqVO;
import com.spartan.dc.service.CommonService;
import com.spartan.dc.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Desc：
 *
 * @Created by 2022-08-11 09:42
 */
@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    @Autowired
    private SendMessageService sendMessageService;

    @Override
    public Boolean sendEmail(String emailMsgCode, Map<String, Object> replaceTitleMap, Map<String, Object> replaceContentMap, Map<String, String> fileBase64StrMap, String... receivers) {
        log.info("Send email to ......");

        SendMessageReqVO sendMessageReqVO = new SendMessageReqVO();
        sendMessageReqVO.setMsgCode(emailMsgCode);
        sendMessageReqVO.setReplaceTitleMap(replaceTitleMap);
        sendMessageReqVO.setReplaceContentMap(replaceContentMap);
        List<String> receiverList = new ArrayList<>();
        for (int i = 0; i < receivers.length; i++) {
            receiverList.add(receivers[i]);
        }
        sendMessageReqVO.setReceivers(receiverList);
        log.info("Sending email to ...... Send content ...... [{}]", JSONObject.toJSONString(sendMessageReqVO));
        sendMessageReqVO.setFileBase64StrMap(fileBase64StrMap);
        return sendMessageService.sendMessage(sendMessageReqVO);
    }
}
