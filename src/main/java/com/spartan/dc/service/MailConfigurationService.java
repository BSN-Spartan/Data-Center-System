package com.spartan.dc.service;

import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.model.DcMailConf;
import com.spartan.dc.model.vo.req.DcMailConfReqVO;

import java.util.List;

/**
 * @ClassName MailConfigurationService
 * @Author wjx
 * @Date 2023/3/7 10:36
 * @Version 1.0
 */
public interface MailConfigurationService {
    ResultInfo sendTest(DcMailConfReqVO dcMailConfReqVO);

    ResultInfo updateSendMode(DcMailConfReqVO dcMailConfReqVO) throws Exception;

    ResultInfo<List<DcMailConf>> queryConf() throws Exception;
}
