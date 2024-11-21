package com.dongwoo.SQM.common.repository;

import lombok.RequiredArgsConstructor;
import org.apache.xmlbeans.impl.xb.ltgfmt.Code;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CodeCacheRepository {
    private final SqlSessionTemplate sql;


}
