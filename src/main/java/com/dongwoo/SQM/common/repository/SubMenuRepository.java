package com.dongwoo.SQM.common.repository;


import com.dongwoo.SQM.common.dto.SecurityUrlMatcherDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubMenuRepository {

    private final SqlSessionTemplate sql;


    public List<SecurityUrlMatcherDTO> getAllSubMenu() {
        return sql.selectList("Menu.getAllSubMenu");
    }
}
