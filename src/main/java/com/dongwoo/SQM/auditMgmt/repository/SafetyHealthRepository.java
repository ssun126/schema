package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.*;
import com.dongwoo.SQM.siteMgr.dto.SafetyItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SafetyHealthRepository {
    private final SqlSessionTemplate sql;
    //파일 정보 저장
    public int insertFileInfo(SafetyHealthDTO safetyHealth) {
        return sql.insert("SafetyHealth.insertFile", safetyHealth);
    }
    //평가항목 점수 가져오기
    public List<AuditItemPointDTO> getCompanyAuthItemPoint(Map<String, Object> params) {
        return sql.selectList("SafetyHealth.getCompanyAuthItemPoint", params);
    }

}
