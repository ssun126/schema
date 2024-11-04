package com.dongwoo.SQM.common.repository;


import com.dongwoo.SQM.common.dto.SecurityUrlMatcherDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SecurityUrlMatcherRepository {

    private final SqlSessionTemplate sql;

    public List<SecurityUrlMatcherDTO> getAllMenu() {
        return sql.selectList("Menu.getAllMenu");
    }

    public List<SecurityUrlMatcherDTO> getAllAdminMenu() {
        return sql.selectList("Menu.getAllAdminMenu");
    }
    public List<SecurityUrlMatcherDTO> getAllUserMenu() {
        return sql.selectList("Menu.getAllUserMenu");
    }
}
