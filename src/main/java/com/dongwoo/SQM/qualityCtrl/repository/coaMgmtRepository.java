package com.dongwoo.SQM.qualityCtrl.repository;

import com.dongwoo.SQM.qualityCtrl.dto.coaMgmtDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class coaMgmtRepository {
    private final SqlSessionTemplate sql;

    public List<coaMgmtDTO> getCOAList(coaMgmtDTO coaMgmtDTO) {
        //if(Objects.equals(SearchType, "List")) {
        return sql.selectList("coaMgmt.getCOAList", coaMgmtDTO);
    }
}
