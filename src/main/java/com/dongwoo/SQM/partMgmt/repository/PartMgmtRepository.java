package com.dongwoo.SQM.partMgmt.repository;

import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PartMgmtRepository {
    private final SqlSessionTemplate sql;

    public List<HashMap> getPlantList(){
        return sql.selectList("partManagement.getPlantList");
    }

    public List<HashMap> getApprovalStatus(){
        return sql.selectList("partManagement.getApprovalStatus");
    }

    public List<PartMgmtDTO> searchPartMgmt(PartMgmtDTO partMgmtDTO){
        return sql.selectList("partManagement.searchPartMgmt",partMgmtDTO);
    }

    public List<HashMap> getpartCodeList(){ return sql.selectList("partManagement.partCodeList");}

    public int save(PartMgmtDTO partMgmtDTO){
        return sql.insert("partManagement.save",partMgmtDTO);
    }

    public HashMap<String,Object> getFileData(String idx){

//        HashMap<String,Object> map = new HashMap<>();
//        map.put("idx",idx);
//        map.put("gubun",gubun);
        return sql.selectOne("partManagement.getEtcFileData",idx);
    }

    //detailpage
    public int updateMsdsData(partDetailMsdsDTO msdsDTO){ return sql.update("partManagement.msdsUpdate",msdsDTO);}

    public int updateRohsData(partDetailRohsDTO rohsDTO){ return sql.update("partManagement.rohsUpdate",rohsDTO);}

    public int updateHalogenData (partDetailHalGDTO halgDTO){ return sql.update("partManagement.halgUpdate",halgDTO);}

    public int updateEtcData (partDetailEtcDTO etcDTO){ return sql.update("partManagement.etcUpdate",etcDTO);}

    public int saveMsdsData(partDetailMsdsDTO msdsDTO){ return sql.insert("partManagement.msdsSave",msdsDTO);}

    public int saveRohsData(partDetailRohsDTO rohsDTO){ return sql.insert("partManagement.rohsSave",rohsDTO);}

    public int saveHalogenData(partDetailHalGDTO halgDTO){ return sql.insert("partManagement.halgSave",halgDTO);}

    public int saveEtcData(partDetailEtcDTO etcDTO){ return sql.insert("partManagement.etcSave",etcDTO);}

    public List<SvhcListDTO> getSvhcData(){return sql.selectList("partManagement.getSvhcList");}

    public List<DeclarationDTO> getDeclarData(){return sql.selectList("partManagement.getDeclarList");}



}
