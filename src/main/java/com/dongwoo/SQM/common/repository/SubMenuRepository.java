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


    public List<SecurityUrlMatcherDTO> getAllAdminSubMenu() {
        return sql.selectList("Menu.getAllAdminSubMenu");
    }

    public List<SecurityUrlMatcherDTO> getAllUserSubMenu() {
        return sql.selectList("Menu.getAllUserSubMenu");
    }

    public List<SecurityUrlMatcherDTO> getAllAdminThirdMenu() {
        return sql.selectList("Menu.getAllAdminThirdMenu");
    }

    public List<SecurityUrlMatcherDTO> getAllUserThirdMenu() {
        return sql.selectList("Menu.getAllUserThirdMenu");
    }
}
