package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeclarationRepository {
    private final SqlSessionTemplate sql;

    public List<DeclarationDTO> findAll(){
        return  sql.selectList("Declaration.findAll");
    }

    public void deleteAll(){
         sql.delete("Declaration.deleteAll");
    }
}
