package com.spartan.dc.core.conf;

import lombok.Data;

/**
 * @author wxq
 * @create 2022/8/21 11:08
 * @description NodeAddressRule
 */
@Data
public class NodeAddressRule {
    private int chainId;
    private String regularFormat;
    private boolean containsNodeId;
}
