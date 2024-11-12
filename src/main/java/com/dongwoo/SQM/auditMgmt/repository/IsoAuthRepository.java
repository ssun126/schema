package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.board.dto.Criteria;
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

    //전체 리스트 조회
    public List<IsoAuthItemDTO> getList(Criteria criteria) {
        return sql.selectList("IsoAuthItem.getList", criteria);
    }

    //전체 리스트 수
    public int getTotal(){
        return sql.selectOne("IsoAuthItem.getTotal");
    }

    //업체 코드로 정보 조회
    public IsoAuthItemDTO findByCompanyId(String id) {
        return sql.selectOne("IsoAuthItem.findByCompanyId", id);
    }
    
    //검색어와 페이징으로 리스트 조회
    public List<IsoAuthItemDTO> findByCriteria(Map<String, Object> params) {
        return sql.selectList("IsoAuthItem.findByCriteria", params);
    }

    //검색어로 조회된 리스트 수
    public int countByKeyword(Map<String, Object> params) {
        return sql.selectOne("IsoAuthItem.countByKeyword", params);
    }
}
