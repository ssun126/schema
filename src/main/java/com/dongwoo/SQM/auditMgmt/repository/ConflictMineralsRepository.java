package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.*;
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
    //평가항목 점수 저장
    public int insertItem(ConflictMineralsDTO conflict) {
        return sql.insert("ConflictMinerals.insertItem", conflict);
    }
    //history 저장
    public int insertHistory(ConflictMineralsDTO conflict) {
        return sql.insert("ConflictMinerals.insertItem", conflict);
    }

    public int updateItem(ConflictMineralsDTO conflict) {
        return sql.update("ConflictMinerals.updateItem", conflict);
    }

    //업체별-Auth type별 첨부 파일 조회
    public List<ConflictMineralsDTO> getCompanyAuthFile(Map<String, Object> params) {
        return sql.selectList("ConflictMinerals.getCompanyAuthFile", params);
    }
    //업체별-분쟁광물 정보 조회
    public List<ConflictMineralsDTO> getConflictData(Map<String, Object> params) {
        return sql.selectList("ConflictMinerals.getConflictMinerals", params);
    }

    //분쟁광물 리스트 가져오기
    public ConflictMineralsDTO findByPartItem(Map<String, Object> params) {
        return sql.selectOne("ConflictMinerals.findByPartItem", params);
    }
}
