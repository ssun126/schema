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

import java.util.HashMap;
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

    //지켐스 자재코드 정보 조회 INTER_PART_MATERIAL
    public List<HashMap> getMaterialList(Map<String, Object> params) {
        return sql.selectList("partCode.getMaterialList", params);
    }

    //업체코드 팝업 조회 SC_COMPANY_CODE
    public List<HashMap> getCompanyList(Map<String, Object> params) {
        return sql.selectList("partCode.getCompanyList", params);
    }

    public List<CompanyInfoDTO> getCompanyInfoList(Criteria criteria) {
        return sql.selectList("partCode.getCompanyInfoList",criteria);
    }

    public int setPartCode(PartCodeDTO partCodeDTO) {
        String savetype  = partCodeDTO.getSavetype() ;

        if(savetype.equals("update")) {
            return sql.update("partCode.updatePartCode", partCodeDTO);
        }else {
            return sql.insert("partCode.setPartCode", partCodeDTO);
        }
    }

    public int deletePartCode(String partCode, String plantCode) {
        PartCodeDTO partCodeDTO = new PartCodeDTO();
        partCodeDTO.setPART_CODE(partCode);
        partCodeDTO.setPLANT_CODE(plantCode);
        return sql.delete("partCode.deletePartCode", partCodeDTO);
    }



}
