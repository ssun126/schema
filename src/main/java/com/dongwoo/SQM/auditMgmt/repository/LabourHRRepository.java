package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.dto.LabourHRDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LabourHRRepository {
    private final SqlSessionTemplate sql;

    public int insertLabour(LabourHRDTO labourHRDTO) {
        return sql.insert("IsoAuthItem.insertItem", labourHRDTO);
    }
}
