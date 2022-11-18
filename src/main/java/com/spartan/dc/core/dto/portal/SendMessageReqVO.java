package com.spartan.dc.core.dto.portal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author syt
 * @Description
 * @Date 2022/7/22/22:24
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageReqVO implements Serializable {

    private static final long serialVersionUID = -6475371696668876094L;

    private String msgCode;

    private Map<String, Object> replaceTitleMap;

    private Map<String, Object> replaceContentMap;

    private List<String> receivers;

    private List<String> cc;

    /**
     * User collection
     */
    private List<Long> userIds;

    private String linkUrl = "javascript:;";

    Map<String, String> fileBase64StrMap;


}
