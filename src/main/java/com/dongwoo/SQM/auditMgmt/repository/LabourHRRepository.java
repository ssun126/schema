package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.LabourHRDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class LabourHRRepository {
    private final SqlSessionTemplate sql;

    public int insertFileInfo(LabourHRDTO labourHRDTO) {
        return sql.insert("Labour.insertFile", labourHRDTO);
    }
    public LabourHRDTO getCompanyAuth(Map<String, Object> params) {
        return sql.selectOne("Labour.getCompanyAuth", params);
    }
    //업체별-Auth type별 첨부 파일 조회
    public LabourHRDTO getCompanyAuthFile(Map<String, Object> params) {
        return sql.selectOne("Labour.getCompanyAuthFile", params);
    }

}
