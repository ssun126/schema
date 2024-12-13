package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.ConflictMineralsDTO;
import com.dongwoo.SQM.auditMgmt.dto.LabourHRDTO;
import com.dongwoo.SQM.auditMgmt.dto.SafetyHealthDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ConflictMineralsRepository {
    private final SqlSessionTemplate sql;

    public int insertFileInfo(ConflictMineralsDTO conflict) {
        return sql.insert("ConflictMinerals.insertFile", conflict);
    }

    //업체별-Auth type별 첨부 파일 조회
    public List<ConflictMineralsDTO> getCompanyAuthFile(Map<String, Object> params) {
        return sql.selectList("ConflictMinerals.getCompanyAuthFile", params);
    }
    //업체별-분쟁광물 정보 조회
    public List<ConflictMineralsDTO> getConflictData(Map<String, Object> params) {
        return sql.selectList("ConflictMinerals.getConflictMinerals", params);
    }
}
