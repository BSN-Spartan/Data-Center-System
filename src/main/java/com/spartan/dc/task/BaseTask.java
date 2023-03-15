package com.spartan.dc.task;

import com.google.common.collect.Lists;
import com.reddate.spartan.SpartanSdkClient;
import com.spartan.dc.core.dto.portal.SendMessageReqVO;
import com.spartan.dc.core.enums.BalanceCheckStateEnum;
import com.spartan.dc.core.enums.DcSystemConfTypeEnum;
import com.spartan.dc.core.enums.MsgCodeEnum;
import com.spartan.dc.core.enums.SystemConfCodeEnum;
import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.service.SendMessageService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author wxq
 * @create 2022/12/28 14:33
 * @description base task
 */
public class BaseTask {

    private final static Logger LOG = LoggerFactory.getLogger(BaseTask.class);


    public static Date GAS_BALANCE_REMINDER_TIME = null;
    public static Date NTT_BALANCE_REMINDER_TIME = null;
    /**
     * Reminding time for insufficient ntt and gas
     * Unit:minute
     */
    public static int REMINDER_TIME = 30;

    @Autowired
    private SendMessageService sendMessageService;

    @Resource
    private SpartanSdkClient spartanSdkClient;

    @Resource
    private DcSystemConfMapper dcSystemConfMapper;

    /**
     * Send email
     **/
    public void sendEmail(String msgCode, Map<String, Object> replaceTitleMap,Map<String, Object> replaceContentMap, List<String> receivers) {
        SendMessageReqVO sendMessageReqVO = new SendMessageReqVO();
        sendMessageReqVO.setMsgCode(msgCode);
        sendMessageReqVO.setReplaceTitleMap(replaceTitleMap);
        sendMessageReqVO.setReplaceContentMap(replaceContentMap);
        sendMessageReqVO.setReceivers(receivers);

        // Send email
        sendMessageService.sendMessage(sendMessageReqVO);
    }

    public boolean emailReminder(SysDataCenter sysDataCenter, MsgCodeEnum msgCodeEnum, Map<String, Object> replaceContentMap) {
        if (StringUtils.isBlank(sysDataCenter.getContactsEmail())) {
            LOG.error(String.format("Send email error,dc_code:%s,is empty.", sysDataCenter.getDcCode()));
            return true;
        }

        // Recipient
        List<String> receivers = Lists.newArrayList();
        receivers.add(sysDataCenter.getContactsEmail());
        Map<String, Object> replaceTitleMap = new HashMap<>();

        // Send email
        sendEmail(msgCodeEnum.getCode(), replaceTitleMap, replaceContentMap, receivers);
        return false;
    }

}
