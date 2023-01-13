package com.spartan.dc.task;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.wuhanchain.ReqJsonWithOfflineHashBean;
import com.reddate.spartan.dto.wuhanchain.TransactionsBean;
import com.spartan.dc.core.enums.MsgCodeEnum;
import com.spartan.dc.core.enums.NodeStateEnum;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.dao.write.DcNodeMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.DcNode;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.service.WalletService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.*;

import static com.reddate.spartan.service.BaseService.nonceManagerUtils;

/**
 * Node registration
 *
 * @author linzijun
 * @version V1.0
 * @date 2022/8/18 19:07
 */
@Configuration
@ConditionalOnProperty(prefix = "task", name = "enabled", havingValue = "true")
public class NodeUpChainTask extends BaseTask {

    private final static Logger LOG = LoggerFactory.getLogger(NodeUpChainTask.class);

    @Resource
    private SpartanSdkClient spartanSdkClient;

    @Resource
    private DcNodeMapper dcNodeMapper;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Autowired
    private WalletService walletService;

    private static int TX_RETRY_COUNT = 5;

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
                if (StringUtils.isNotEmpty(dcNode.getTxHash())) {
                    TransactionsBean tx = spartanSdkClient.baseService.getTransactionByHash(dcNode.getTxHash());
                    if (Objects.nonNull(tx)) {
                        dcNode.setState(NodeStateEnum.IN_THE_SUBMISSION.getCode());
                        dcNodeMapper.updateByPrimaryKeySelective(dcNode);
                        continue;
                    }
                }

                String txHash = enterNetByDC(sysDataCenter, dcNode);
                dcNode.setTxHash(txHash);
            } catch (Exception e) {
                LOG.error("Task: Exception of node registration: {}", e.getMessage());

                // reset nonce
                nonceManagerUtils.resetNonce();
                // balance reminder
                if (e.getMessage().contains("insufficient funds for gas")) {
                    if (GAS_BALANCE_REMINDER_TIME == null || DateUtil.between(GAS_BALANCE_REMINDER_TIME, new Date(), DateUnit.MINUTE) >= REMINDER_TIME) {
                        LOG.info("Task: Exception of node registration:Send balance reminder email......");

                        // send email
                        emailReminder(sysDataCenter, MsgCodeEnum.GAS_BALANCE_REMINDER_CONFIG, null);
                        GAS_BALANCE_REMINDER_TIME = new Date();
                    }
                } else if (e.getMessage().contains("nonce too low")) {
                   // retry
                } else {
                    dcNode.setReason(e.getMessage());
                    dcNode.setTxTime(new Date());
                    dcNode.setState(NodeStateEnum.NETWORK_FAIL.getCode());
                    dcNodeMapper.updateByPrimaryKeySelective(dcNode);
                }
                continue;
            }

            dcNode.setState(NodeStateEnum.IN_THE_SUBMISSION.getCode());
            dcNode.setTxTime(new Date());
            dcNodeMapper.updateByPrimaryKeySelective(dcNode);
        }
    }

    public String enterNetByDC(SysDataCenter sysDataCenter, DcNode dcNode) throws Exception {
        ReqJsonWithOfflineHashBean reqJsonWithOfflineHashBean = spartanSdkClient.nodeService.enterNetByDC(sysDataCenter.getNttAccountAddress(), sysDataCenter.getDcCode(), dcNode.getNodeCode(), sysDataCenter.getNttAccountAddress(), dcNode.getChainId().intValue(), dcNode.getNodeAddress(), dcNode.getApplySign());
        String offLineHash = reqJsonWithOfflineHashBean.getOffLineHash();
        if (StringUtils.isEmpty(offLineHash)) {
            throw new GlobalException("EnterNet Simulate offLineHash failed");
        }
        dcNode.setTxHash(offLineHash);
        return spartanSdkClient.nodeService.send(reqJsonWithOfflineHashBean.getReqJsonRpcBean());
    }
}
