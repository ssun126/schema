package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.LabourItemDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LabourItemRepository {
    private final SqlSessionTemplate sql;

    public List<LabourItemDTO> findAll(){
        return sql.selectList("labourItem.findAll");
    }

    public void deletAll(){
        sql.delete("labourItem.deletAll");
    }
}
