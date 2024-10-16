package com.dongwoo.SQM.system.repository;

import com.dongwoo.SQM.system.dto.MenuDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MenuRepository {
    private final SqlSessionTemplate sql;

    public static List<List<MenuDTO>> menuList = new ArrayList<List<MenuDTO>>();
}
