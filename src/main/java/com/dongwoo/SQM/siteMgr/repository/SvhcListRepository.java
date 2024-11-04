package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class SvhcListRepository {

    private final SqlSessionTemplate sql;

    public List<SvhcListDTO> findAll(){
        return sql.selectList("SvhcList.findAll");
    }

}
