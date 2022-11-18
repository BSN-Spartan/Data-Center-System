package com.spartan.dc.core.dto.portal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 
 * @author liqiuyue
 * @version V1.0
 * @date 2022 11-04 16:45.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinUrlTempVO {

	private String accessKey;

	private String accessDemo;

	private String chainCode;

	private String rpcUrl;

	private String wsURl;

	private String evmRpcUrl;

	private String evmWsUrl;

	private String grpcUrl;

}
