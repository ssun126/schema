package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.config.security.UserCustom;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class IsoAuthRepository {
    private final SqlSessionTemplate sql;

    //인증서 정보 저장
    public int insertItem(IsoAuthItemDTO isoAuthItemDTO) {
        return sql.insert("IsoAuthItem.insertItem", isoAuthItemDTO);
    }

    public int updateItem(IsoAuthItemDTO isoAuthItemDTO) {
        return sql.insert("IsoAuthItem.updateItem", isoAuthItemDTO);
    }
    //인증서 전체 저장
    public void saveItemAll(IsoAuthItemDTO isoAuthItemDTO) {
        sql.insert("IsoAuthItem.saveItem", isoAuthItemDTO);
    }

    //파일path 가져오기
    public String getFilePath(String fileName) {
        return sql.selectOne("IsoAuthItem.getFileName", fileName);
    }

    //전체 인증서 리스트 조회
    public List<IsoAuthItemDTO> getList(String code) {
        return sql.selectList("IsoAuthItem.getList", code);
    }

    //검색된 인증서 리스트 조회
    public AuditMgmtDTO getCompanyAuth(Map<String, Object> params) {
        return sql.selectOne("IsoAuthItem.getCompanyAuth", params);
    }

    //검색된 인증서 리스트 조회
    public List<HashMap> getExpDateList(Map<String, Object> params) {
        return sql.selectList("IsoAuthItem.getExpDateList", params);
    }

    //전체 리스트 수
    public int getTotal(){
        return sql.selectOne("IsoAuthItem.getTotal");
    }

    //iso 인증코드와 업체 코드로 정보 조회
    public IsoAuthItemDTO findByIsoAuthItem(Map<String, Object> params) {
        return sql.selectOne("IsoAuthItem.findByIsoAuthItem", params);
    }

    //iso 인증코드와 업체 코드로 정보 조회
    public List<IsoAuthItemDTO> getIsoAuthItems(Map<String, Object> params) {
        return sql.selectList("IsoAuthItem.getIsoAuthItems", params);
    }

    //iso 인증코드와 업체 코드로 수 조회
    public int selectAuthItem(IsoAuthItemDTO isoAuthItemDTO) {
        return sql.selectOne("IsoAuthItem.selectAuthItem", isoAuthItemDTO);
    }

    //업체 코드로 수 조회
    public String selectAuthState(IsoAuthItemDTO isoAuthItemDTO) {
        return sql.selectOne("IsoAuthItem.selectAuthState", isoAuthItemDTO);
    }

    //업체 코드로 iso 정보 조회
    public List<IsoAuthItemDTO> findByCompanyId(Map<String, Object> params) {
        return sql.selectList("IsoAuthItem.findByCompanyId", params);
    }
    
    //검색어와 페이징으로 리스트 조회
    public List<IsoAuthItemDTO> searchCompanies(Map<String, Object> params) {
        return sql.selectList("IsoAuthItem.searchCompanies", params);
    }

    //검색어로 조회된 리스트 수
    public int countByKeyword(Map<String, Object> params) {
        return sql.selectOne("IsoAuthItem.countByKeyword", params);
    }

    //Auth 업체별/메뉴별 승인/반려 처리
    public int saveAuthResult(AuditMgmtDTO auditMgmtDTO) {
        return sql.update("IsoAuthItem.saveAuthResult", auditMgmtDTO);
    }

    //상태 업데이트
    public int updateStatus(IsoAuthItemDTO isoAuthItemDTO) {
        return sql.update("IsoAuthItem.updateStatus", isoAuthItemDTO);
    }
}
