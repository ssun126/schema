package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BaseCodeRepository {
    private final SqlSessionTemplate sql;

    public int save(BaseCodeDTO baseCodeDTO) {
        return sql.insert("BaseCode.save", baseCodeDTO);
    }

    public List<BaseCodeDTO> findAll() {
        return sql.selectList("BaseCode.findAll");
    }

    public List<BaseCodeDTO> getbaseGubunList() {
        return sql.selectList("BaseCode.getbaseGubunList");
    }

    public List<BaseCodeDTO> getbaseGroupCDList(){
        return sql.selectList("BaseCode.getbaseGroupCDList");
    }

    public BaseCodeDTO getbaseCodeInfo(int idx){
        return sql.selectOne("BaseCode.getbaseCodeInfoIdx",idx);
    }
    public BaseCodeDTO getbaseCodeInfo(String idx){
        return sql.selectOne("BaseCode.getbaseCodeInfo",idx);
    }
    public BaseCodeDTO getbaseCodeInfoCode(String BASE_CODE){
        return sql.selectOne("BaseCode.getbaseCodeInfoCode",BASE_CODE);
    }

    public List<BaseCodeDTO> findSearch(String sGubun, String sCodeGroup, String sKey, String sTextval){
        HashMap<String,Object> data = new HashMap<>();
        data.put("sGubun",sGubun);
        data.put("sCodeGroup",sCodeGroup);
        data.put("sKey",sKey);
        data.put("sTextval",sTextval);

        return sql.selectList("BaseCode.findSearch",data);
    }

    public BaseCodeDTO findByCodeName(BaseCodeDTO baseCodeDTO) {
        return sql.selectOne("BaseCode.findByCodeName", baseCodeDTO);
    }

    public List<BaseCodeDTO> findByCodeGroup(BaseCodeDTO baseCodeDTO) {
        return sql.selectList("BaseCode.findByCodeGroup", baseCodeDTO);
    }

    public List<BaseCodeDTO> findByCodeGroupAll() {
        return sql.selectList("BaseCode.findByCodeGroupAll");
    }

    public List<BaseCodeDTO> findByCodeGroupUse(String BASE_CODE) {
        return sql.selectList("BaseCode.findByCodeGroupUse", BASE_CODE);
    }
    public void update(BaseCodeDTO baseCodeDTO) {
        sql.update("BaseCode.update", baseCodeDTO);
    }

    public void delete(int CODEID) {
        sql.delete("BaseCode.delete", CODEID);
    }
}
