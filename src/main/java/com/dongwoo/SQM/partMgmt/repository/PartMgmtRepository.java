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
import java.util.Map;

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

    public PartMgmtDTO getPartMgmt(int PM_IDX){
        return sql.selectOne("partManagement.getPartMgmt",PM_IDX);
    }

    public List<HashMap> searchPartCodeList(String COM_CODE, String code, String Name){
        HashMap<String,Object> data = new HashMap<>();
        data.put("COM_CODE",COM_CODE);
        data.put("PART_CODE",code);
        data.put("PART_NAME",Name);

        return sql.selectList("partManagement.searchPartCodeList", data);
    }

    public List<HashMap> getpartCodeList(){ return sql.selectList("partManagement.partCodeList");}

    public int save(PartMgmtDTO partMgmtDTO){
        return sql.insert("partManagement.save",partMgmtDTO);
    }


    public int updatePartMgmt (PartMgmtDTO partMgmtDTO) { return  sql.update("partManagement.updatePartMgmt",partMgmtDTO);}

    public int deletePartMgmt(String idx){return sql.delete("partManagement.deletePartMgmt",idx);}



    public PartMgmtDTO getPartMgmtData(String idx){
        return sql.selectOne("partManagement.getPartMgmtData",idx);
    }

    public void updateApprovalStatus(int idx, String status){
        HashMap<String,Object> map = new HashMap<>();
        log.info("idx : "+idx +"++++++++ status : "+status);
        map.put("idx",idx);
        map.put("status",status);
        sql.update("partManagement.updateApprovalStatus",map);
    }

    public void updateActive(String status,int idx ){
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("idx",idx);

        sql.update("partManagement.updateActive",map);
    }
    public void updateActiveList(String status,String idx ){
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",status);
        map.put("idxList",idx);

        sql.update("partManagement.updateActiveList",map);
    }

    //detailpage
    public PartMgmtDTO getPartData(String idx) { return sql.selectOne("partManagement.getPartData",idx);}


    /*********************************************************************************************************************
     ** Detail v페이지
     ** MSDS / HALGEN / ROHS/ 기타 페이지 **
     *********************************************************************************************************************/

    public partDetailMsdsDTO getMsdsData(String idx) {return sql.selectOne("partManagement.getPartDetailMsdsData",idx);}
    public partDetailRohsDTO getRohsData(String idx) {return sql.selectOne("partManagement.getPartDetailRohsData",idx);}
    public partDetailHalGDTO getHalgData(String idx) {return sql.selectOne("partManagement.getPartDetailHalgData",idx);}
    public List<partDetailEtcDTO> getEtcData(String idx) {return sql.selectList("partManagement.getPartDetailEtcData",idx);}

    //public int saveMsdsData(partDetailMsdsDTO msdsDTO){ return sql.insert("partManagement.msdsSave",msdsDTO);}
    //public int updateMsdsData(partDetailMsdsDTO msdsDTO){ return sql.update("partManagement.msdsUpdate",msdsDTO);}

    //신규msds 저장로직
    public int insertMsdsData(partDetailMsdsDTO msdsDTO){ return sql.insert("partManagement.msdsSave",msdsDTO);}
    public int updateMsdsData(partDetailMsdsDTO msdsDTO){ return sql.update("partManagement.msdsUpdate",msdsDTO);}

    public int insertRohsData(partDetailRohsDTO rohsDTO){ return sql.insert("partManagement.rohsSave",rohsDTO);}
    public int updateRohsData(partDetailRohsDTO rohsDTO){ return sql.update("partManagement.rohsUpdate",rohsDTO);}

    public int updateHalogenData (partDetailHalGDTO halgDTO){ return sql.update("partManagement.halgUpdate",halgDTO);}
    public int saveHalogenData(partDetailHalGDTO halgDTO){ return sql.insert("partManagement.halgSave",halgDTO);}

    public int insertEtcData(partDetailEtcDTO etcDTO){ return sql.insert("partManagement.insertEtcData",etcDTO);}
    public int updateEtcData (partDetailEtcDTO etcDTO){ return sql.update("partManagement.updateEtcData",etcDTO);}
    public int deleteEtcData(int idx){ return sql.delete("partManagement.deleteEtcData",idx);}

    public void MsdsDeleteFile(int idx) { sql.update("partManagement.MsdsDeleteFile",idx);}
    public void RohsDeleteFile(int idx) { sql.update("partManagement.RohsDeleteFile",idx);}
    public void HalogenDeleteFile(int idx) { sql.update("partManagement.HalogenDeleteFile",idx);}

    public partDetailEtcDTO getEtcDataIdx(int idx) { return sql.selectOne("partManagement.EtcDeleteFile",idx);}
    public void EtcDeleteFile(int idx) { sql.update("partManagement.EtcDeleteFile",idx);}



    /*********************************************************************************************************************
     ** Detail v페이지
     ** SVHC 페이지 **
     *********************************************************************************************************************/
    public int insertSvhcData(PartDetailSvhcDTO svhcDTO){ return sql.insert("partManagement.svhcSave",svhcDTO);}
    public int updateSvhcData(PartDetailSvhcDTO svhcDTO){ return sql.update("partManagement.svhcUpdate",svhcDTO);}


    public List<SvhcListDTO> getSvhcData(){return sql.selectList("partManagement.getSvhcList");}

    public PartDetailSvhcDTO getDetailSvhcData(String idx){
        return sql.selectOne("partManagement.getDetailSvhcData",idx);
    }

    public void SvhcDeleteFile(int idx) { sql.update("partManagement.SvhcDeleteFile",idx);}

//    public  int saveDetailSvhcData(PartDetailSvhcDTO svhcDTO) {return sql.insert("partManagement.svhcSave", svhcDTO);}
//    public int updateDetailSvhcData(PartDetailSvhcDTO svhcDTO) {return sql.update("partManagement.svhcUpdate",svhcDTO);}


    /*********************************************************************************************************************
     ** Detail v페이지
     ** 제품환경 관리물질 페이지 **
     *********************************************************************************************************************/
    public List<DeclarationDTO> getDeclarData(){return sql.selectList("partManagement.getDeclarList");}

    public  partDetailDeclarDTO getDetailDeclData (String idx){
        return sql.selectOne("partManagement.getDetailDeclarData",idx);
    }

    public  int insertDeclData(partDetailDeclarDTO declarDTO) {return sql.insert("partManagement.declarSave", declarDTO);}
    public int updateDeclData(partDetailDeclarDTO declarDTO) {return sql.update("partManagement.declarUpdate",declarDTO);}

    public void DeclDeleteFile(int idx) { sql.update("partManagement.DeclDeleteFile",idx);}


    /*********************************************************************************************************************
     ** Detail v페이지
     ** SCCS / 성분명세서 / 기타보증 페이지 **
     *********************************************************************************************************************/
    //sccs
    public  partDetailSccsDTO getSccsData (String idx){
        return sql.selectOne("partManagement.getSccsData",idx);
    }

    public int insertSccsData(partDetailSccsDTO sccsDTO){ return sql.insert("partManagement.sccsSave",sccsDTO);}
    public int updateSccsData(partDetailSccsDTO sccsDTO){ return sql.update("partManagement.sccsUpdate",sccsDTO);}

    //ingred
    public  partDetailIngredDTO getIngredData (String idx){
        return sql.selectOne("partManagement.getIngredData",idx);
    }
    public int insertIngredData(partDetailIngredDTO ingredDTO){ return sql.insert("partManagement.ingredSave",ingredDTO);}
    public int updateIngredData(partDetailIngredDTO ingredDTO){ return sql.update("partManagement.ingredUpdate",ingredDTO);}

    //guarant
    public  List<partDetailGuarantDTO> getGuarantData (String idx){
        return sql.selectList("partManagement.getGuarantData",idx);
    }
    public int insertGuarantData(partDetailGuarantDTO guarantDTO){ return sql.insert("partManagement.guarantSave",guarantDTO);}
    public int updateGuarantData(partDetailGuarantDTO guarantDTO){ return sql.update("partManagement.guarantUpdate",guarantDTO);}
    public int deleteGuarantData(int idx){ return sql.delete("partManagement.deleteguarantData",idx);}


    public void SccsDeleteFile(int idx) { sql.update("partManagement.SccsDeleteFile",idx);}
    public void IngredDeleteFile(int idx) { sql.update("partManagement.IngredDeleteFile",idx);}

    public partDetailGuarantDTO getGuarantDataIdx(int idx) { return sql.selectOne("partManagement.getGuarantDataIdx",idx);}
    public void GuarantDeleteFile(int idx) { sql.update("partManagement.GuarantDeleteFile",idx);}



    public int updateApprovalStatus(String idx){ return sql.update("partManagement.updateApprovalStatus",idx);}


    public void initConfirmChk(int pmidx){
        sql.update("partManagement.initConfirmChk",pmidx);
    }

    /*********************************************************************************************************************
     ** Detail v페이지
     ** History set
     *********************************************************************************************************************/
    public void setHistoryData(int pmidx, int userIdx , String gubun) {
        Map<String,Object> map = new HashMap<>();
        map.put("PM_IDX",pmidx);
        map.put("USER_IDX",userIdx);
        map.put("GUBUN",gubun);

        sql.update("partManagement.setHistoryData",map);
    }

    /* MSDS/ RoHS Halogen  */
    public partDetailMsdsDTO getOrignMsdsData (int pmidx) { return sql.selectOne("partManagement.getOrignMsdsData",pmidx);}
    public partDetailRohsDTO getOrignRohsData (int pmidx) { return sql.selectOne("partManagement.getOrignRohsData",pmidx);}
    public partDetailHalGDTO getOrignHalgData (int pmidx) { return sql.selectOne("partManagement.getOrignHalgData",pmidx);}

    /* Svhc */
    public PartDetailSvhcDTO getOrignSvhcData (int pmidx) { return sql.selectOne("partManagement.getOrignSvhcData",pmidx);}

    /* Svhc */
    public partDetailDeclarDTO getOrignDeclData (int pmidx) { return sql.selectOne("partManagement.getOrignDeclData",pmidx);}

    /* Svhc */
    public partDetailSccsDTO getOrignSccsData (int pmidx) { return sql.selectOne("partManagement.getOrignSccsData",pmidx);}
    public partDetailIngredDTO getOrignIngredData (int pmidx) { return sql.selectOne("partManagement.getOrignIngredData",pmidx);}


    /*********************************************************************************************************************
     ** Detail v페이지
     ** 파일다운로드
     *********************************************************************************************************************/

    public Map<String,String> getMsdsFileData (int idx){ return sql.selectOne("partManagement.getMsdsFileData",idx);}
    public Map<String,String> getRohsFileData (int idx){ return sql.selectOne("partManagement.getRohsFileData",idx);}
    public Map<String,String> getHalgFileData (int idx){ return sql.selectOne("partManagement.getHalgFileData",idx);}
    public Map<String,String> getEtcFileData  (int idx){ return sql.selectOne("partManagement.getEtcFileData",idx);}
    public Map<String,String> getDetailSvhcFileData (int idx){ return sql.selectOne("partManagement.getDetailSvhcFileData",idx);}
    public Map<String,String> getDetailDeclFileData (int idx){ return sql.selectOne("partManagement.getDetailDeclFileData",idx);}
    public Map<String,String> getSccsFileData (int idx){ return sql.selectOne("partManagement.getSccsFileData",idx);}
    public Map<String,String> getIngredFileData (int idx){ return sql.selectOne("partManagement.getIngredFileData",idx);}
    public Map<String,String> getGuarantDataFileData (int idx){ return sql.selectOne("partManagement.getGuarantDataFileData",idx);}




}
