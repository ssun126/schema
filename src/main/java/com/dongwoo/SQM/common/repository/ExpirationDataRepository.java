package com.dongwoo.SQM.common.repository;

import com.dongwoo.SQM.common.dto.ExpirationDateDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
@RequiredArgsConstructor
public class ExpirationDataRepository {
    private final SqlSessionTemplate sql;

    public ExpirationDateDTO getExpiration(String CODE1, int CODE2, String EXP_KIND) {
        HashMap<String,Object> data = new HashMap<>();
        data.put("CODE1",CODE1);
        data.put("CODE2",CODE2);
        data.put("EXP_KIND",EXP_KIND);

        return sql.selectOne("expirationDate.getExpiration", data);
    }

    public void setExpiration(String CODE1, int CODE2, String EXP_KIND, int EXP_MONTH, String EXP_BODY, int UP_DW_USER_IDX) {
        HashMap<String,Object> data = new HashMap<>();
        data.put("CODE1",CODE1);
        data.put("CODE2",CODE2);
        data.put("EXP_KIND",EXP_KIND);
        data.put("EXP_MONTH",EXP_MONTH);
        data.put("EXP_BODY",EXP_BODY);
        data.put("UP_DW_USER_IDX",UP_DW_USER_IDX);

        sql.update("expirationDate.setExpiration", data);
    }
}
