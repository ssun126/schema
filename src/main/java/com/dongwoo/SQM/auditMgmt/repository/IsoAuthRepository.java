package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.IsoAuthDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class IsoAuthRepository {
    private final SqlSessionTemplate sql;

    public int save(IsoAuthItemDTO isoAuthItemDTO) {
        return sql.insert("IsoAuthItem.save", isoAuthItemDTO);
    }

    //전체 인증서 리스트 조회
    public List<IsoAuthItemDTO> getList(Criteria criteria) {
        return sql.selectList("IsoAuthItem.getList", criteria);
    }

    //검색된 인증서 리스트 조회
    public List<IsoAuthItemDTO> getExpDateList(Map<String, Object> params) {
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

    //업체 코드로 iso 정보 조회
    public List<IsoAuthItemDTO> findByCompanyId(Map<String, Object> params) {
        return sql.selectList("IsoAuthItem.findByCompanyId", params);
    }
    
    //검색어와 페이징으로 리스트 조회
    public List<IsoAuthDTO> searchCompanies(Map<String, Object> params) {
        return sql.selectList("IsoAuthItem.searchCompanies", params);
    }

    //검색어로 조회된 리스트 수
    public int countByKeyword(Map<String, Object> params) {
        return sql.selectOne("IsoAuthItem.countByKeyword", params);
    }
}
