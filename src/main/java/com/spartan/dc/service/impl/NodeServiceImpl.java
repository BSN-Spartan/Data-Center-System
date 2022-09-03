package com.spartan.dc.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.conf.NodeAddressRule;
import com.spartan.dc.core.conf.NodeAddressRuleConf;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.dc.DataCenter;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.AesUtil;
import com.spartan.dc.core.util.enums.ApplyNodeStateEnum;
import com.spartan.dc.dao.write.DcNodeMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.DcNode;
import com.spartan.dc.model.vo.req.AddDcNodeReqVO;
import com.spartan.dc.model.vo.req.OffNetworkReqVO;
import com.spartan.dc.service.NodeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author wxq
 * @create 2022/8/15 13:21
 * @description node manager
 */
@Service
public class NodeServiceImpl extends BaseService implements NodeService {
    @Autowired
    DcNodeMapper dcNodeMapper;

    @Autowired
    private SysDataCenterMapper sysDataCenterMapper;

    @Autowired
    private NodeAddressRuleConf nodeAddressRuleConf;

    @Override
    public Map<String, Object> queryNodeList(DataTable<Map<String, Object>> dataTable) {
        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());

        List<Map<String, Object>> list = dcNodeMapper.queryNodeList(dataTable.getCondition());

        return dataTable.getReturnData(list);
    }

    @Override
    public boolean offNetwork(OffNetworkReqVO vo) {
        return false;
    }

    @Override
    public boolean addNode(AddDcNodeReqVO vo, Credentials credentials) throws Exception {
        DcNode dcNodeMapperOneByNodeID = dcNodeMapper.getOneByNodeID(vo.getNodeCode());
        if (dcNodeMapperOneByNodeID != null) {
            throw new GlobalException("Node code already exist");
        }

        List<NodeAddressRule> nodeAddressRules = nodeAddressRuleConf.getList();
        if (CollectionUtils.isEmpty(nodeAddressRules)) {
            throw new GlobalException("Node address validation rule is not configured");
        }
        Optional<NodeAddressRule> nodeAddressRule = nodeAddressRules.stream().filter(t -> t.getChainId() == (vo.getChainId())).findFirst();
        if (!nodeAddressRule.isPresent()) {
            throw new GlobalException("No valid node address validation rule is found");
        }
        String regular = nodeAddressRule.get().getRegularFormat();
        boolean isMatch = Pattern.matches(regular, vo.getNodeAddress());
        if (!isMatch) {
            throw new GlobalException("Node address does not match the validation rule");
        }

        // check data center
        String dataCenterToken = checkDataCenterInfo(vo.getPassword(), credentials);

        DcNode dcNode = new DcNode();
        BeanUtils.copyProperties(vo, dcNode);
        String applySign = AesUtil.encrypt(vo.getApplySign(), dataCenterToken);
        dcNode.setChainId(vo.getChainId());
        dcNode.setApplySign(applySign);
        dcNode.setCreateTime(new Date());
        dcNode.setState(ApplyNodeStateEnum.APPLY_PENDING.getCode());
        return dcNodeMapper.insert(dcNode) >= 0;
    }
}
