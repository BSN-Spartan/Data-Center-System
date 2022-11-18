package com.spartan.dc.core.util.message;

import com.spartan.dc.model.SysMessageTemplate;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Desc：
 *
 * @Created by 2022-07-27 09:49
 */
public class MessageTemplateUtil {

    public static SysMessageTemplate getMsgByCode(List<SysMessageTemplate> messageTemplateList, short msgType) {
        for (SysMessageTemplate sysMessageTemplate : messageTemplateList) {
            if (sysMessageTemplate.getMsgType().equals(msgType)) {
                return sysMessageTemplate;
            }
        }
        return null;
    }

    public static SysMessageTemplate replaceMsg(SysMessageTemplate messageTemplate, Map<String, Object> replaceTitleMap, Map<String, Object> replaceContentMap) {
        if (!CollectionUtils.isEmpty(replaceTitleMap)) {
            messageTemplate.setMsgTitle(sendMsgWrap(replaceTitleMap, messageTemplate.getMsgTitle()));
        }
        if (!CollectionUtils.isEmpty(replaceContentMap)) {
            messageTemplate.setMsgContent(sendMsgWrap(replaceContentMap, messageTemplate.getMsgContent()));
        }
        return messageTemplate;
    }

    public static String initEmailContent(String defaultTemplate, String emailText,String emailContactTemplate) {
        if (Objects.isNull(defaultTemplate)) {
            return emailText;
        }
        Map<String, Object> replaceParam = new HashMap<>();
        replaceParam.put("text", emailText);
        replaceParam.put("contact", emailContactTemplate);

        return sendMsgWrap(replaceParam, defaultTemplate);
    }


    protected static String sendMsgWrap(Map<String, Object> params, String temp) {
        StringSubstitutor stringSubstitutor = new StringSubstitutor(params);
        String wrapResult = stringSubstitutor.replace(temp);
        return wrapResult;

    }
}
