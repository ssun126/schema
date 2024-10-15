package com.dongwoo.SQM.system.menu.repository;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MenuRepository {
    private final SqlSessionTemplate sql;


}
