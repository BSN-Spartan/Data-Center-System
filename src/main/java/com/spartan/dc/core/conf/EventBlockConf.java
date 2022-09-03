package com.spartan.dc.core.conf;

import com.spartan.dc.dao.write.EventBlockMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicLong;

/**
 * EventBlock
 * @author linzijun
 * @version V1.0
 * @date 2022/8/9 14:31
 */
public class EventBlockConf {

    public static AtomicLong eventBlock = null;

}
