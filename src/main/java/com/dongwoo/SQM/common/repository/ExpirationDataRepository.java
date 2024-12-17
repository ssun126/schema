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

    public void sendExpAlert(String CODE1, int CODE2, String MES_KIND,String GUBN,String KIND,String SEND_TYPE,String SEND_FROM,String SEND_TO, String SEND_TITLE, String SEND_BODY, int REG_DW_USER_IDX,int  REG_COM_USER_IDX){
        HashMap<String ,Object> data = new HashMap<>();

        data.put("CODE1",CODE1);
        data.put("CODE2",CODE2);
        data.put("MES_KIND",MES_KIND);
        data.put("GUBN",GUBN);
        data.put("KIND",KIND);
        data.put("SEND_TYPE",SEND_TYPE);
        data.put("SEND_FROM",SEND_FROM);
        data.put("SEND_TO",SEND_TO);
        data.put("SEND_TITLE",SEND_TITLE);
        data.put("SEND_BODY",SEND_BODY);
        data.put("REG_DW_USER_IDX",REG_DW_USER_IDX);
        data.put("REG_COM_USER_IDX",REG_COM_USER_IDX);

        sql.insert("expirationDate.sendExpAlert", data);

    }
}
