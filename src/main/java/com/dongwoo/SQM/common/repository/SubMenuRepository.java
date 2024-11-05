package com.dongwoo.SQM.common.repository;


import com.dongwoo.SQM.common.dto.SecurityUrlMatcherDTO;
import com.dongwoo.SQM.common.dto.SubMenuDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SubMenuRepository {

    private final SqlSessionTemplate sql;

    public List<SubMenuDTO> getAllSubMenu() {
        return sql.selectList("Menu.getAllSubMenu");
    }

    public List<SubMenuDTO> getAllAdminSubMenu() {
        return sql.selectList("Menu.getAllAdminSubMenu");
    }

    public List<SubMenuDTO> getAllUserSubMenu() {
        return sql.selectList("Menu.getAllUserSubMenu");
    }
}
