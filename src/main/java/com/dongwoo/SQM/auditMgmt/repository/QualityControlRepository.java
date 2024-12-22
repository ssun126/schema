package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.AuditItemPointDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class QualityControlRepository {
    private final SqlSessionTemplate sql;
    //평가항목 점수 가져오기
    public List<AuditItemPointDTO> getCompanyAuthItemPoint(Map<String, Object> params) {
        return sql.selectList("qualityItem.getCompanyAuthItemPoint", params);
    }
}
