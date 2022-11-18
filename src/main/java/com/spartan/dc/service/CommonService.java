package com.spartan.dc.service;

import java.util.Map;

/**
 * Desc：
 *
 * @Created by 2022-08-11 09:40
 */
public interface CommonService {

    Boolean sendEmail(String emailMsgCode, Map<String, Object> replaceTitleMap, Map<String, Object> replaceContentMap, Map<String, String> fileBase64StrMap, String... receivers);

}
