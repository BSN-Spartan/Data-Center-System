package com.spartan.dc.dao.write;

import com.spartan.dc.core.vo.req.DcSystemConfReqVO;
import com.spartan.dc.core.vo.resp.DcSystemConfRespVO;
import com.spartan.dc.model.DcSystemConf;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DcSystemConfMapper {
    int deleteByPrimaryKey(Long confId);

    int insert(DcSystemConf record);

    int insertSelective(DcSystemConf record);

    DcSystemConf selectByPrimaryKey(Long confId);

    int updateByPrimaryKeySelective(DcSystemConf record);

    int updateByPrimaryKey(DcSystemConf record);

    List<DcSystemConfRespVO> querySystemConf(Short type);

    void updateDcSystemConf(@Param("dcSystemConfReqVO") List<DcSystemConfReqVO> dcSystemConfReqVO);

    String querySystemValue(@Param("type") Short type,@Param("confCode") String confCode);

    DcSystemConf querySystemConfByCode(String confCode);

}