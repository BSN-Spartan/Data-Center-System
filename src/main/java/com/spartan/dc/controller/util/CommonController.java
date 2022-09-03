package com.spartan.dc.controller.util;

import com.spartan.dc.core.conf.SystemConf;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dell
 */
@RestController
@RequestMapping("/sys/")
public class CommonController {

    @Autowired
    private SystemConf systemConf;

    @GetMapping("getSysConf")
    @ResponseBody
    public ResultInfo getSysConf() {
        if (systemConf.getName() == null || systemConf.getName().replaceAll(" ","").length() ==0) {
            systemConf.setName("BSN Spartan Data Center System");
        }
        systemConf.setDefaultPassword(null);
        return ResultInfoUtil.successResult(systemConf);
    }

}
