package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.*;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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
    //업체별-Auth type별 정보 update
    public int updateAuth(AuditMgmtDTO auditMgmtDTO) {
        return sql.update("AuditMgmt.updateAuth", auditMgmtDTO);
    }

    //Auth 업체별/메뉴별 승인/반려 처리
    public int saveAuthResult(AuditMgmtDTO auditMgmtDTO) {
        return sql.update("AuditMgmt.saveAuthResult", auditMgmtDTO);
    }

    //업체별-Auth type별 평가항목 insert
    public int insertItemPoint(AuditItemPointDTO auditItemPoint) {
        return sql.insert("AuditMgmt.insertItemPoint", auditItemPoint);
    }

    //심사항목별 승인 상태 조회
    public AuditMgmtDTO getCompanyAuth(Map<String, Object> params) {
        return sql.selectOne("AuditMgmt.getCompanyAuth", params);
    }

    //심사항목별 History
    public List<AuditMgmtHistDTO> getCompanyAuthHistory(Map<String, Object> params) {
        return sql.selectList("AuditMgmt.getCompanyAuthHistory", params);
    }
    public AuditMgmtHistDTO getCompanyAuthHistoryDetail(Map<String, Object> params) {
        return sql.selectOne("AuditMgmt.getCompanyAuthHistory", params);
    }

    public int insertFileInfo(AuditMgmtDTO auditMgmtDTO) {
        return sql.insert("AuditMgmt.insertFile", auditMgmtDTO);
    }

    //업체별-Auth type별 첨부 파일 조회
    public List<AuditMgmtDTO> getCompanyAuthFile(Map<String, Object> params) {
        return sql.selectList("AuditMgmt.getCompanyAuthFile", params);
    }
    //업체별-Auth type별 평가항목 점수 조회
    public List<AuditItemPointDTO> getCompanyAuthItemPoint(Map<String, Object> params) {
        return sql.selectList("AuditMgmt.getCompanyAuthItemPoint", params);
    }

    //업체별 만료일 조회
    public List<HashMap> getExpDateList(Map<String, Object> params) {
        return sql.selectList("AuditMgmt.getExpDateList", params);
    }

    public Map<String, String> getUserInfo(Map<String, Object> parameterMap) {
        return sql.selectOne("AuditMgmt.getUserInfo", parameterMap);
    }

    //상태 업데이트
    public int updateStatus(AuditMgmtDTO auditMgmtDTO) {
        return sql.update("AuditMgmt.updateStatus", auditMgmtDTO);
    }

    //선택 업체의 Audit 결과 조회
    public List<AuditMgmtDTO> getAuditResult(Map<String, Object> params) {
        return sql.selectList("AuditMgmt.getAuditResult", params);
    }
    //업체별 Audit 결과 조회
    public List<AuditMgmtDTO> searchCompanies(Map<String, Object> params) {
        return sql.selectList("AuditMgmt.searchCompanies", params);
    }
}
