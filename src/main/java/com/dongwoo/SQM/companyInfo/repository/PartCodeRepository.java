package com.dongwoo.SQM.companyInfo.repository;

import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoParamDTO;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import com.dongwoo.SQM.companyInfo.dto.PartCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PartCodeRepository {
    private final SqlSessionTemplate sql;

    //코드 바인딩
    public List<BaseCodeDTO> GetBaseCode(String GROUP_CODE) {
        return sql.selectList("partCode.GetBaseCode",GROUP_CODE );
    }

    //업체 리스트 검색 2024.12.2
    public List<PartCodeDTO> partCodeList(PartCodeDTO partCodeDTO ) {
        //if(Objects.equals(SearchType, "List")) {
        return sql.selectList("partCode.partCodeList", partCodeDTO);
    }


}
