package com.spartan.dc.controller.dc.recharge;

import com.spartan.dc.controller.BaseController;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.model.vo.req.AddChainSalePriceReqVO;
import com.spartan.dc.service.ChainPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;


/**
 * @author liqiuyue
 * @create 2022/11/7 18:09
 * @description charge manager
 */
@RestController
@RequestMapping("price/")
@ApiIgnore
public class PriceManageController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(PriceManageController.class);

    @Autowired
    private ChainPriceService chainPriceService;

//    @RequiredPermission
    @PostMapping(value = "list")
    public ResultInfo queryPriceList(@RequestBody DataTable<Map<String, Object>> dataTable) {

        logger.info("Price Information List");
        Map<String, Object> chargeList = chainPriceService.queryChainSalePriceList(dataTable);
        return ResultInfoUtil.successResult(chargeList);
    }


//    @RequiredPermission
    @PostMapping(value = "add")
    public ResultInfo addPrice(@RequestBody @Validated AddChainSalePriceReqVO vo) throws Exception {

        boolean result = chainPriceService.addPrice(vo);
        if (result) {
            return ResultInfoUtil.successResult("New success");
        } else {
            return ResultInfoUtil.errorResult("The new failure");
        }
    }

    //    @RequiredPermission
    @RequestMapping(value = "detail/{salePriceId}")
    public ResultInfo getSalePriceDetail(@PathVariable("salePriceId") Integer salePriceId) {

        logger.info("Get price details......");


        Map<String, Object> salePriceDetail = chainPriceService.getSalePriceDetail(salePriceId);

        Map<String, Object> salePriceInfo = new HashMap<>();
        salePriceInfo.put("salePriceDetail",salePriceDetail);
        return ResultInfoUtil.successResult(salePriceInfo);
    }


    @RequestMapping(value = "audit/{salePriceId}")
    public ResultInfo auditSalePriceDetail(@PathVariable("salePriceId") Integer salePriceId) {

        logger.info("Review price information details......");


        Map<String, Object> salePriceDetail = chainPriceService.getSalePriceDetail(salePriceId);

        Map<String, Object> salePriceInfo = new HashMap<>();
        salePriceInfo.put("salePriceDetail",salePriceDetail);
        return ResultInfoUtil.successResult(salePriceInfo);
    }


    /**
     *
     * Review price information
     * @param checkRemark
     * @param checkResult
     * @param salePriceId
     * @return
     */
    @PostMapping(value = "audit")
    @ResponseBody
    public ResultInfo audit(@RequestParam(value = "checkRemark", required = false) String checkRemark,
                            @RequestParam(value = "checkResult", required = false) Short checkResult,
                            @RequestParam(value = "salePriceId", required = false) Integer salePriceId
    ) {

        logger.info("==Review price information==");
        try {
            Boolean result = chainPriceService.toAudit(checkRemark, checkResult, salePriceId);
            if (result) {
                return ResultInfoUtil.successResult("New success");
            } else {
                return ResultInfoUtil.errorResult("The new failure");
            }

        } catch (Exception e) {
            logger.error("Failed to review the price information:", e);
            throw new GlobalException("Failed to review the price information");
        }
    }



}
