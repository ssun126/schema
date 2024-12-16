package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.QualityItemDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QualityItemRepository {
    private final SqlSessionTemplate sql;

    public List<QualityItemDTO> findAll(){
        return sql.selectList("qualityItem.findAll");
    }

    public void deletAll(){
        sql.delete("qualityItem.deletAll");
    }
}
