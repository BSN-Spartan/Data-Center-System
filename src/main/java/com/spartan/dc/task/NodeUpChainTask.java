package com.spartan.dc.task;

import com.alibaba.fastjson.JSONObject;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.wuhanchain.ReqJsonWithOfflineHashBean;
import com.spartan.dc.core.enums.NodeStateEnum;
import com.spartan.dc.dao.write.DcNodeMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.DcNode;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.reddate.spartan.service.BaseService.nonceManagerUtils;

/**
 *  Node registration
 * @author linzijun
 * @version V1.0
 * @date 2022/8/18 19:07
 */
@Configuration
@ConditionalOnProperty(prefix = "task", name = "enabled", havingValue = "true")
public class NodeUpChainTask {

    private final static Logger LOG = LoggerFactory.getLogger(NodeUpChainTask.class);

    @Resource
    private SpartanSdkClient spartanSdkClient;

    @Resource
    private DcNodeMapper dcNodeMapper;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Autowired
    private WalletService walletService;

    @Scheduled(cron = "${task.nodeUpChain}")
    private void configureTasks() throws Exception {

        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            LOG.info("Task: Exception of node registration: {}", "the basic information of the data center has not been configured yet");
            return;
        }

        if (!walletService.checkWalletExists()) {
            LOG.info("Task: Exception of node registration: {}", "the key store information has not been configured yet");
            return;
        }

        List<DcNode> dcNodes = dcNodeMapper.getStayNodeUpChainList();
        LOG.info("Task: Node registration: {}", JSONObject.toJSONString(dcNodes));
        if (dcNodes == null || dcNodes.size() == 0) {
            return;
        }

        for (DcNode dcNode : dcNodes) {

            try {
                dcNode.setTxHash(enterNetByDC(sysDataCenter.getNttAccountAddress(), sysDataCenter.getDcCode(), dcNode.getNodeCode(), sysDataCenter.getNttAccountAddress(), dcNode.getChainId().intValue(),dcNode.getNodeAddress(),dcNode.getApplySign()));
            } catch (Exception e) {
                LOG.error("Task: Exception of node registration: {}", e.getMessage());
                dcNode.setReason(e.getMessage());
                dcNode.setTxTime(new Date());
                dcNode.setState(NodeStateEnum.NETWORK_FAIL.getCode());
                dcNodeMapper.updateByPrimaryKeySelective(dcNode);

                // reset nonce
                nonceManagerUtils.resetNonce();
                continue;
            }

            dcNode.setState(NodeStateEnum.IN_THE_SUBMISSION.getCode());
            dcNode.setTxTime(new Date());
            dcNodeMapper.updateByPrimaryKeySelective(dcNode);

        }
    }

    public String enterNetByDC(String sender, String dcId, String nodeId, String dcNttAddr, int chainId, String nodeAddr, String nodeSign) throws Exception {
        ReqJsonWithOfflineHashBean reqJsonWithOfflineHashBean = spartanSdkClient.nodeService.enterNetByDC(sender, dcId, nodeId, dcNttAddr, chainId, nodeAddr, nodeSign);
        String offLineHash = reqJsonWithOfflineHashBean.getOffLineHash();
        String txHash = spartanSdkClient.nodeService.send(reqJsonWithOfflineHashBean.getReqJsonRpcBean());
        return txHash;
    }

}
