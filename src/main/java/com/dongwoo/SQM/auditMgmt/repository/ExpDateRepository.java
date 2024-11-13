package com.dongwoo.SQM.auditMgmt.repository;

import com.dongwoo.SQM.auditMgmt.dto.ExpDateDTO;
import com.dongwoo.SQM.auditMgmt.dto.ExpDateDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ExpDateRepository {
    private final SqlSessionTemplate sql;

    public int save(ExpDateDTO expDateDTO) {
        return sql.insert("ExpDate.save", expDateDTO);
    }

    //전체 리스트 조회
    public List<ExpDateDTO> getList(Criteria criteria) {
        return sql.selectList("ExpDate.getList", criteria);
    }

    //전체 리스트 수
    public int getTotal(){
        return sql.selectOne("ExpDate.getTotal");
    }

    //만료일  업체 코드로 정보 조회
    public ExpDateDTO findByExpDate(Map<String, Object> params) {
        return sql.selectOne("ExpDate.findByExpDate", params);
    }

    //업체 코드로 만료일 정보 조회
    public List<ExpDateDTO> findByCompanyId(Map<String, Object> params) {
        return sql.selectList("ExpDate.findByCompanyId", params);
    }

    //iso 검색 리스트 조회
    public List<ExpDateDTO> isoSearchComList(Map<String, Object> params) {
        return sql.selectList("ExpDate.isoSearchComList", params);
    }

    //검색어로 조회된 리스트 수
    public int countByKeyword(Map<String, Object> params) {
        return sql.selectOne("ExpDate.countByKeyword", params);
    }
}
