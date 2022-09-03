package com.spartan.dc.dao.write;

import com.spartan.dc.model.EventBlock;

public interface EventBlockMapper {
    int deleteByPrimaryKey(Long blockHeight);

    int insert(EventBlock record);

    int insertSelective(EventBlock record);

    EventBlock getOne();

    void increment();

    void updateEventBlock(EventBlock eventBlock);
}