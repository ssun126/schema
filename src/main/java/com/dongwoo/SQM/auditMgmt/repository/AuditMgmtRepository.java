package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.LabourHRDTO;
import com.dongwoo.SQM.auditMgmt.dto.SafetyHealthDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AuditMgmtRepository {
    private final SqlSessionTemplate sql;

    //업체별-Auth type별 수 조회
    public int selectAuthCnt(AuditMgmtDTO auditMgmtDTO) {
        return sql.selectOne("AuditMgmt.selectAuthCnt", auditMgmtDTO);
    }

    //업체별-Auth type별 정보 조회
    public AuditMgmtDTO selectAuth(AuditMgmtDTO auditMgmtDTO) {
        return sql.selectOne("AuditMgmt.selectAuth", auditMgmtDTO);
    }

    //업체별-Auth type별 정보 insert
    public int insertAuth(AuditMgmtDTO auditMgmtDTO) {
        return sql.insert("AuditMgmt.insertAuth", auditMgmtDTO);
    }
    //업체별-Auth type별 정보 insert
    public int updateAuth(AuditMgmtDTO auditMgmtDTO) {
        return sql.update("AuditMgmt.updateAuth", auditMgmtDTO);
    }

    public AuditMgmtDTO getCompanyAuth(Map<String, Object> params) {
        return sql.selectOne("AuditMgmt.getCompanyAuth", params);
    }

    public int insertFileInfo(AuditMgmtDTO auditMgmtDTO) {
        return sql.insert("AuditMgmt.insertFile", auditMgmtDTO);
    }

    //업체별-Auth type별 첨부 파일 조회
    public List<AuditMgmtDTO> getCompanyAuthFile(Map<String, Object> params) {
        return sql.selectList("AuditMgmt.getCompanyAuthFile", params);
    }
}
