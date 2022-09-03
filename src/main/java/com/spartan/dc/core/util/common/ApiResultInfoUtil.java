package com.spartan.dc.core.util.common;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.spartan.dc.core.dto.dc.RespQueryChainVO;
import com.spartan.dc.core.dto.dc.RespSaveDcVO;
import org.jsoup.Connection;

import java.util.List;

/**
 * Desc：
 *
 * @Created by 2022-07-21 18:04
 */
public class ApiResultInfoUtil {

    private static String getResponseBody(Connection.Response response) {
        if (response == null) {
            return null;
        }
        String res = response.body();
        if (res == null || res.length() == 0) {
            return null;
        }
        return res;
    }

    public static ApiResultInfo initApiResultInfo(Connection.Response response) {
        return JSONObject.parseObject(getResponseBody(response), ApiResultInfo.class);
    }

    public static ApiResultInfo<List<RespQueryChainVO>> convertQueryChainInfo(Connection.Response response) {
        return JSONObject.parseObject(getResponseBody(response), new TypeReference<ApiResultInfo<List<RespQueryChainVO>>>(){});
    }

    public static ApiResultInfo<RespSaveDcVO> convertSaveDcInfo(Connection.Response response) {
        return JSONObject.parseObject(getResponseBody(response), new TypeReference<ApiResultInfo<RespSaveDcVO>>(){});
    }

}
