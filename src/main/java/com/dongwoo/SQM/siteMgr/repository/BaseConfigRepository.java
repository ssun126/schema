package com.dongwoo.SQM.siteMgr.repository;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BaseConfigRepository {
    private final SqlSessionTemplate sql;

}
