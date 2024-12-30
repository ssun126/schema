package com.dongwoo.SQM.qualityCtrl.repository;

import com.dongwoo.SQM.qualityCtrl.dto.coaMgmtDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class coaMgmtRepository {
    private final SqlSessionTemplate sql;

    public List<BaseCodeDTO> GetBaseCode(String GROUP_CODE) {
        return sql.selectList("coaMgmt.GetBaseCode",GROUP_CODE );
    }

    public List<BaseCodeDTO> GetBaseCodePLANT(String GROUP_CODE) {
        return sql.selectList("coaMgmt.GetBaseCodePLANT",GROUP_CODE );
    }

    public List<coaMgmtDTO> getCOAList(coaMgmtDTO coaMgmtDTO) {
        //if(Objects.equals(SearchType, "List")) {
        return sql.selectList("coaMgmt.getCOAList", coaMgmtDTO);
    }

    //사용자 팝업 조회
    public List<HashMap> getUserList(Map<String, Object> params) {
        return sql.selectList("coaMgmt.getUserList", params);
    }
}
