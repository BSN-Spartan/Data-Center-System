package com.spartan.dc.controller;

import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.util.common.DateUtils;
import com.spartan.dc.core.vo.resp.HealthRespVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author : rjx
 * @Date : 2023/02/16 13:40
 **/
@ApiIgnore
@RestController
@RequestMapping("/v1/health/")
public class HealthController {

    @GetMapping("check")
    public ResultInfo<HealthRespVO> checkHealth() {
        String serviceName = "spartan-dc";

        String currentTime = DateUtils.getTime();

        HealthRespVO healthRespVO = new HealthRespVO();
        healthRespVO.setServiceName(serviceName);
        healthRespVO.setCurrentTime(currentTime);

        return ResultInfoUtil.definitionResult(0, "success", healthRespVO);
    }

}
