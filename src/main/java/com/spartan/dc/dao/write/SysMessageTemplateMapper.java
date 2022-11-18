package com.spartan.dc.dao.write;

import com.spartan.dc.model.SysMessageTemplate;

import java.util.List;

public interface SysMessageTemplateMapper {
    int deleteByPrimaryKey(Long templateId);

    int insert(SysMessageTemplate record);

    int insertSelective(SysMessageTemplate record);

    SysMessageTemplate selectByPrimaryKey(Long templateId);

    int updateByPrimaryKeySelective(SysMessageTemplate record);

    int updateByPrimaryKey(SysMessageTemplate record);

    List<SysMessageTemplate> listByCode(String msgCode);
}