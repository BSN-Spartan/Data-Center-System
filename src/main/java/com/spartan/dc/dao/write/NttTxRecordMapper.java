package com.spartan.dc.dao.write;

import com.spartan.dc.model.NttTxRecord;
import com.spartan.dc.model.vo.resp.NttRewardRespVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NttTxRecordMapper {
    int deleteByPrimaryKey(Long txRecordId);

    int insert(NttTxRecord record);

    int insertSelective(NttTxRecord record);

    NttTxRecord selectByPrimaryKey(Long txRecordId);

    int updateByPrimaryKeySelective(NttTxRecord record);

    int updateByPrimaryKey(NttTxRecord record);

    /**
     * Query ntt list.
     * @param condition
     * @return
     */
    List<Map<String, Object>> queryNttList(Map<String, Object> condition);

    NttTxRecord getOneByTxHash(@Param("txHash") String txHash, @Param("txType")Short txType);


    /**
     * @return
     */
    NttRewardRespVO getNTTRecentAward();

    NttTxRecord getOneByMd5SignAndTxType(@Param("md5Sign") String md5Sign, @Param("txType") Short txType);
}