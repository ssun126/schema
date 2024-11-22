package com.dongwoo.SQM.common.repository;

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
/*
    public List<CodeDto> selectCodeList(CodeDto codeDto) {
        return sql.selectList("Menu.getAllAdminSubMenu", codeDto);
    }

    public CodeDto selectCodeByCd(String code) {
        return sql.selectOne("Menu.getAllAdminSubMenus", code);
    }

    public int deleteCode(CodeDto codeDto) {
        return sql.delete("Board.delete", codeDto);
    }*/
}
