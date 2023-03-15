package com.spartan.dc.task;

import com.reddate.spartan.SpartanSdkClient;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.dao.write.DcAccountBalanceConfMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.handler.ChainGasRechargeHandle;
import com.spartan.dc.model.DcAccountBalanceConf;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.model.vo.resp.BalanceReminderRespVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Node registration
 *
 * @author linzijun
 * @version V1.0
 * @date 2022/8/18 19:07
 */
@Configuration
@ConditionalOnProperty(prefix = "task", name = "enabled", havingValue = "true")
public class BalanceCheckTask extends BaseTask {

    private final static Logger LOG = LoggerFactory.getLogger(BalanceCheckTask.class);

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Resource
    private DcAccountBalanceConfMapper dcAccountBalanceConfMapper;

    @Resource
    private SpartanSdkClient spartanSdkClient;

    protected Long chainId = Long.valueOf(ChainTypeEnum.ETH.getCode());

    @Scheduled(cron = "${task.balanceCheck}")
    private void balanceCheck() throws Exception {
        // check data center
        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            LOG.error("Task: Exception of balance check: {}", "the basic information of the data center has not been configured yet");
            return;
        }

        // get the reminder account
        List<BalanceReminderRespVo> reminderAccountList = dcAccountBalanceConfMapper.getReminderAccountList();

        if (reminderAccountList == null || reminderAccountList.size() == 0) {
            LOG.info("Task:There is no account information to be checked temporarily");
            return;
        }

        for (BalanceReminderRespVo reminderAccount:reminderAccountList) {

            BigDecimal nttBalance;
            String msgCode = "";

            if(reminderAccount.getMonitorType().equals(BalanceCheckTypeEnum.MONITOR_NTT.getType())){
                try {
                    nttBalance = spartanSdkClient.nTTService.balanceOf(reminderAccount.getChainAddress());
                } catch (Exception e) {
                    LOG.error("Failed to get NTT balance:", e);
                    continue;
                }
                msgCode = MsgCodeEnum.NTT_BALANCE_REMINDER.getCode();
            }else{
                try {
                    nttBalance = new BigDecimal(spartanSdkClient.nTTService.balanceOfGas(reminderAccount.getChainAddress()));

                    nttBalance = ChainGasRechargeHandle.initGasDivide(chainId,nttBalance);
                } catch (Exception e) {
                    LOG.error("Failed to get Gas balance:", e);
                    continue;
                }
                msgCode = MsgCodeEnum.GAS_BALANCE_REMINDER.getCode();
            }

            if (nttBalance.compareTo(reminderAccount.getBalanceLimit()) <= 0) {

                // get email
                List<String> nttReceivers = Arrays.asList(reminderAccount.getReminderEmail().split(";"));

                // email replace content
                Map<String, Object> replaceContentMap = new HashMap<>();
                replaceContentMap.put("accountBalance_", nttBalance);
                replaceContentMap.put("chainName_", reminderAccount.getChainName());
                replaceContentMap.put("accountAddress_", reminderAccount.getChainAddress());
                replaceContentMap.put("accountLimit_", reminderAccount.getBalanceLimit());
                Map<String, Object> replaceTitleMap =  new HashMap<>();
                replaceTitleMap.put("chainName_", reminderAccount.getChainName());

                // send ntt balance email
                sendEmail(msgCode, replaceTitleMap,replaceContentMap, nttReceivers);
            }

        }


    }


}
