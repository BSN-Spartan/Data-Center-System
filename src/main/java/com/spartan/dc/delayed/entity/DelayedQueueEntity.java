package com.spartan.dc.delayed.entity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Delay queue base class
 *
 * @Author : rjx
 * @Date : 2022/10/11 10:21
 **/
@Data
@SuperBuilder
public class DelayedQueueEntity {

    /**
     * Number of executions
     */
    private Integer number;
}
