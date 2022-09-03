package com.spartan.dc.core.runner;

import com.spartan.dc.core.conf.EventBlockConf;
import com.spartan.dc.core.conf.SystemConf;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.Md5Utils;
import com.spartan.dc.core.util.common.UUIDUtil;
import com.spartan.dc.dao.write.DcUserMapper;
import com.spartan.dc.dao.write.EventBlockMapper;
import com.spartan.dc.dao.write.NttTxSumMapper;
import com.spartan.dc.model.DcUser;
import com.spartan.dc.model.EventBlock;
import com.spartan.dc.model.NttTxSum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class LoadOneRunner implements CommandLineRunner {

    @Resource
    private SystemConf systemConf;

    @Resource
    private DcUserMapper dcUserMapper;

    @Resource
    private EventBlockMapper eventBlockMapper;

    @Resource
    private NttTxSumMapper nttTxSumMapper;

    @Value("${chain.blockHeight}")
    private Long blockHeight;

    @Override
    public void run(String... args) throws Exception {

        String adminName = systemConf.getAdminName();
        String adminEmail = systemConf.getAdminEmail();
        String defaultPassword = systemConf.getDefaultPassword();


        DcUser dcUser = dcUserMapper.selectByEmail(adminEmail);
        if (Objects.isNull(dcUser)) {
            dcUser = new DcUser();
            dcUser.setContactsEmail(adminEmail);
            dcUser.setContactsName(adminName);
            dcUser.setContactsPhone("123456");

            String salt = UUIDUtil.generate().toLowerCase();
            dcUser.setSalt(salt);
            try {
                defaultPassword = Md5Utils.encodeUtf8(salt + defaultPassword);
            } catch (Exception e) {
                throw new GlobalException("Failed to encrypt the password");
            }
            dcUser.setPassword(defaultPassword);
            dcUser.setState(Short.valueOf("1"));
            dcUser.setCreateTime(new Date());

            int count = dcUserMapper.insertSelective(dcUser);
        }


        EventBlock eventBlock = eventBlockMapper.getOne();
        if (eventBlock == null) {
            eventBlock = new EventBlock();
            eventBlock.setBlockHeight(0L);
            eventBlockMapper.insertSelective(eventBlock);
        }

        if (blockHeight != null && blockHeight > eventBlock.getBlockHeight()) {
            EventBlockConf.eventBlock = new AtomicLong(blockHeight);
            eventBlock.setBlockHeight(blockHeight);
            eventBlockMapper.updateEventBlock(eventBlock);
        } else {
            EventBlockConf.eventBlock = new AtomicLong(eventBlock.getBlockHeight());
        }


        NttTxSum nttTxSum = nttTxSumMapper.getOne();
        if (nttTxSum == null) {
            nttTxSum = new NttTxSum();
            nttTxSum.setFlowIn(BigDecimal.ZERO);
            nttTxSum.setFlowOut(BigDecimal.ZERO);
            nttTxSumMapper.insertSelective(nttTxSum);
        }
    }
}
