package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.SafetyItemDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SafetyItemRepository {
    private final SqlSessionTemplate sql;

    public List<SafetyItemDTO> findAll(){
        return sql.selectList("safetyItem.findAll");
    }

    public void deletAll(){
        sql.delete("safetyItem.deletAll");
    }
}
