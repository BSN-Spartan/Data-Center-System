package com.spartan.dc.service.impl;

import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.enums.ChainPriceStateEnum;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.DateUtils;
import com.spartan.dc.core.util.user.UserGlobals;
import com.spartan.dc.core.util.user.UserLoginInfo;
import com.spartan.dc.dao.write.ChainPriceMapper;
import com.spartan.dc.dao.write.ChainSalePriceMapper;
import com.spartan.dc.model.ChainPrice;
import com.spartan.dc.model.ChainSalePrice;
import com.spartan.dc.model.vo.req.AddChainSalePriceReqVO;
import com.spartan.dc.service.ChainPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author liqiuyue
 * @create 2022/8/8 18:11
 * @description Charge manager service.
 */
@Service
public class ChainPriceServiceImpl extends BaseService implements ChainPriceService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Resource
    private ChainSalePriceMapper chainSalePriceMapper;

    @Resource
    private ChainPriceMapper chainPriceMapper;

    @Override
    public Map<String, Object> queryChainSalePriceList(DataTable<Map<String, Object>> dataTable) {

        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());

        Map<String, Object> condition = dataTable.getCondition();

        List<Map<String, Object>> list = chainSalePriceMapper.queryPriceList(condition);

        return dataTable.getReturnData(list);
    }


    @Override
    public Boolean addPrice(AddChainSalePriceReqVO reqVO) {

        // Determine if there is pending price information
        ChainSalePrice chainSalePrice = chainSalePriceMapper.selectSalePriceByChainId(reqVO.getChainId(), ChainPriceStateEnum.PENDING.getCode());

        if(Objects.nonNull(chainSalePrice)){
            throw new GlobalException("The chain currently has price information to be reviewed");
        }else{
            // Get the chainPrice information
            ChainPrice chainPrice = chainPriceMapper.getOneByChainId(reqVO.getChainId());
            if(Objects.isNull(chainPrice)){
                throw new GlobalException("No price information exists for this chain");
            }
            // Query operator information
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Object object = WebUtils.getSessionAttribute(request, UserGlobals.USER_SESSION_KEY);
            UserLoginInfo sysUserPrincipal = (UserLoginInfo) object;

            logger.info("Add new price information");
            // Add new price information
            chainSalePrice = new ChainSalePrice();
            chainSalePrice.setChainId(reqVO.getChainId());
            chainSalePrice.setChainPriceId(chainPrice.getChainPriceId());
            chainSalePrice.setSalePrice(new BigDecimal(1));
            chainSalePrice.setGas(new BigDecimal(reqVO.getSalePrice()).divide(new BigDecimal("100"),2));
            chainSalePrice.setState(ChainPriceStateEnum.PENDING.getCode());
            chainSalePrice.setStartTime(DateUtils.parseDate(reqVO.getStartDate()));
            chainSalePrice.setCreateTime(new Date());
            chainSalePrice.setCreateUserId(sysUserPrincipal.getUserId());
            chainSalePriceMapper.insertSelective(chainSalePrice);
            logger.info("End of new price information");

        }
        return true;
    }

    @Override
    public Map<String, Object> getSalePriceDetail(Integer salePriceId) {
        return chainSalePriceMapper.getSalePriceById(salePriceId);
    }

    //audit
    @Override
    public Boolean toAudit(String checkRemark, Short checkResult, Integer salePriceId) {
        try {
            // Operator information
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Object object = WebUtils.getSessionAttribute(request, UserGlobals.USER_SESSION_KEY);
            UserLoginInfo sysUserPrincipal = (UserLoginInfo) object;

            // Get data to be reviewed
            ChainSalePrice chainSalePrice = chainSalePriceMapper.selectByPrimaryKey(Long.valueOf(salePriceId));

            if(Objects.isNull(chainSalePrice)){
                throw new GlobalException("This review data is not found");
            }else{
                // Modify review status
                chainSalePrice.setState(checkResult);
                chainSalePrice.setCheckTime(new Date());
                chainSalePrice.setCheckRemark(checkRemark);
                chainSalePrice.setCheckUserId(sysUserPrincipal.getUserId());
                chainSalePriceMapper.updateByPrimaryKeySelective(chainSalePrice);
                logger.info("Modify review data successfully");
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new GlobalException(ex.getMessage());
        }
    }

}
