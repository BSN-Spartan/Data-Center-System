package com.spartan.dc.controller.util;

import com.spartan.dc.core.conf.SystemConf;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Dell
 */
@RestController
@RequestMapping("/sys/")
@ApiIgnore
public class CommonController {

    @Autowired
    private SystemConf systemConf;

    @GetMapping("getSysConf")
    @ResponseBody
    public ResultInfo getSysConf() {
        if (StringUtils.isBlank(systemConf.getName())){
            systemConf.setName("BSN Spartan Data Center System");
        }
        systemConf.setDefaultPassword(null);
        return ResultInfoUtil.successResult(systemConf);
    }

}
