package com.dongwoo.SQM.companyInfo.repository;

import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CpCodeRepository {
    private final SqlSessionTemplate sql;

    public int save(CompanyInfoDTO companyInfoDTO) {
        return sql.insert("CompanyInfo.save", companyInfoDTO);
    }

    public List<CompanyInfoDTO> findAll() {
        return sql.selectList("CompanyInfo.findAll");
    }

    public List<CompanyInfoDTO> getList(Criteria criteria) {
        return sql.selectList("CompanyInfo.getList", criteria);
    }

    public int getTotal(){
        return sql.selectOne("CompanyInfo.getTotal");
    }

    public CompanyInfoDTO findById(int id) {
        return sql.selectOne("CompanyInfo.findById", id);
    }

    public void update(CompanyInfoDTO companyInfoDTO) {
        sql.update("CompanyInfo.update", companyInfoDTO);
    }

    public void delete(int id) {
        sql.delete("CompanyInfo.delete", id);
    }


}
