package com.spartan.dc.service;

import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.model.DcSystemConf;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/12/19 10:48
 */
public interface DcSystemConfService {

    int insertSelective(DcSystemConf record);

    int deleteByPrimaryKey(Long confId);

    DcSystemConf querySystemConfByCode(String confCode);

    int updateByPrimaryKey(DcSystemConf record);

}
