package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SafetyHealthRepository {
    private final SqlSessionTemplate sql;

    public AuditMgmtDTO getCompanyAuth(Map<String, Object> params) {
        return sql.selectOne("IsoAuthItem.getCompanyAuth", params);
    }
}
