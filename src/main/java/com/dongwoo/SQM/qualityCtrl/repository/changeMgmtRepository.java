package com.dongwoo.SQM.qualityCtrl.repository;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class changeMgmtRepository {
    private final SqlSessionTemplate sql;
}
