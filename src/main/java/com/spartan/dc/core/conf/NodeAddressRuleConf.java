package com.spartan.dc.core.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wxq
 * @create 2022/8/21 10:26
 * @description node address configuration
 */
@Data
@Component
@ConfigurationProperties(prefix = "chainaddresscheck")
public class NodeAddressRuleConf {
    private List<NodeAddressRule> list;
}