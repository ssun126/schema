package com.dongwoo.SQM.common.repository;

import com.dongwoo.SQM.common.dto.CommonDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CodeRepository {
    private final SqlSessionTemplate sql;

    //코드 바인딩
    public List<BaseCodeDTO> GetBaseCode(String GROUP_CODE) {
        return sql.selectList("CompanyInfo.GetBaseCode",GROUP_CODE );
    }
    public List<BaseCodeDTO> selectCodeList(BaseCodeDTO codeDto) {
        return sql.selectList("CompanyInfo.GetBaseCode", codeDto);
    }

    public BaseCodeDTO selectCodeByCd(String code) {
        return sql.selectOne("CompanyInfo.GetBaseCode", code);
    }

    public BaseCodeDTO selectCode(BaseCodeDTO codeDto) {
        return sql.selectOne("CompanyInfo.GetBaseCode", codeDto);
    }

    public int deleteCode(BaseCodeDTO codeDto) {
        return sql.delete("Board.delete", codeDto);
    }
}
