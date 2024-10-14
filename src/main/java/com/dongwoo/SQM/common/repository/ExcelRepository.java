package com.dongwoo.SQM.common.repository;

import com.dongwoo.SQM.common.dto.ExcelDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExcelRepository {
    private final SqlSessionTemplate sql;

    public int save(ExcelDTO excelDTO) {
        System.out.println("excelDTO = " + excelDTO);
        return sql.insert("excel.save", excelDTO);
    }

    public List<ExcelDTO> findAll() {
        return sql.selectList("excel.findAll");
    }
}
