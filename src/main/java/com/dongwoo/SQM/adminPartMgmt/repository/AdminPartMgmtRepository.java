package com.dongwoo.SQM.adminPartMgmt.repository;

import com.dongwoo.SQM.adminPartMgmt.dto.AdminPartMgmtDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AdminPartMgmtRepository {
     private final SqlSessionTemplate sql;

    public List<AdminPartMgmtDTO> searchAdminPartMgmt(AdminPartMgmtDTO partMgmtDTO){
        return sql.selectList("adminPartMgmt.searchAdminPartMgmt",partMgmtDTO);
    }

    public List<HashMap> getPartMSDSExpList(String EXP_DATE, String COM_CODE, String COM_NAME, int EXP_MONTH){
        HashMap<String,Object> data = new HashMap<>();
        data.put("EXP_DATE",EXP_DATE);
        data.put("COM_CODE",COM_CODE);
        data.put("COM_NAME",COM_NAME);
        data.put("EXP_MONTH",EXP_MONTH);

        return sql.selectList("adminPartMgmt.getPartMSDSExpList",data);
    }

    public List<HashMap> getPartDeclExpList(String EXP_DATE, String COM_CODE, String COM_NAME, int EXP_MONTH){
        HashMap<String,Object> data = new HashMap<>();
        data.put("EXP_DATE",EXP_DATE);
        data.put("COM_CODE",COM_CODE);
        data.put("COM_NAME",COM_NAME);
        data.put("EXP_MONTH",EXP_MONTH);

        return sql.selectList("adminPartMgmt.getPartDeclExpList",data);
    }

    public List<HashMap> getPartEtcExpList(String EXP_DATE, String COM_CODE, String COM_NAME, int EXP_MONTH){
        HashMap<String,Object> data = new HashMap<>();
        data.put("EXP_DATE",EXP_DATE);
        data.put("COM_CODE",COM_CODE);
        data.put("COM_NAME",COM_NAME);
        data.put("EXP_MONTH",EXP_MONTH);

        return sql.selectList("adminPartMgmt.getPartEtcExpList",data);
    }

}
