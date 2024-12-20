package com.dongwoo.SQM.partMgmt.service;

import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.partMgmt.repository.PartMgmtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartMgmtService {
    private final PartMgmtRepository partMgmtRepository;

    public List<HashMap> getPlantList(){
        return partMgmtRepository.getPlantList();
    }

    public List<HashMap> getApprovalStatus(){
        return partMgmtRepository.getApprovalStatus();
    }

    public List<PartMgmtDTO> searchPartMgmt(PartMgmtDTO parmDTO) {

        return partMgmtRepository.searchPartMgmt(parmDTO);
    }

    public PartMgmtDTO getPartMgmt(int PM_IDX) {

        return partMgmtRepository.getPartMgmt(PM_IDX);
    }

    public List<HashMap> searchPartCodeList(String COM_CODE, String code, String Name){
        return partMgmtRepository.searchPartCodeList(COM_CODE, code, Name);
    }

    public PartMgmtDTO getPartMgmtData(String idx) {
        return partMgmtRepository.getPartMgmtData(idx);
    }

    public List<HashMap> getpartCodeList(){ return partMgmtRepository.getpartCodeList();}

    public int save(PartMgmtDTO partMgmtDTO){
        return partMgmtRepository.save(partMgmtDTO);
    }
    public int updatePartMgmt(PartMgmtDTO partMgmtDTO) { return partMgmtRepository.updatePartMgmt(partMgmtDTO);}

    public int deletePartMgmt(String idx) {
        String[] arrIdx = idx.split(",");
        int flag =0;
        for(int i =0; i<arrIdx.length; i++){
            flag += partMgmtRepository.deletePartMgmt(arrIdx[i]);
        }
        return flag;
    }

    public void updateApprovalStatus(int idx, String status){
         partMgmtRepository.updateApprovalStatus(idx,status);
    }

    public void updateActive(String status, int idx){
        partMgmtRepository.updateActive(status,idx);
    }


    /*********************************************************************************************************************
    ** Detail v페이지
    ** Msds / Rohs / Halogen 페이지 **
    *********************************************************************************************************************/
    //msds 데이터 로직
    public int insertMsdsData(partDetailMsdsDTO msdsDTO){
        return partMgmtRepository.insertMsdsData(msdsDTO);
    }
    public int updateMsdsData(partDetailMsdsDTO msdsDTO){
        return partMgmtRepository.updateMsdsData(msdsDTO);
    }

    //rohs 데이터 로직

    public int insertRohsData(partDetailRohsDTO rohsDTO){
        return partMgmtRepository.insertRohsData(rohsDTO);
    }
    public int updateRohsData(partDetailRohsDTO rohsDTO) {
        return partMgmtRepository.updateRohsData(rohsDTO);
    }

    //halg 데이터 로직
    public int insertHalogenData(partDetailHalGDTO halGDTO){
        return partMgmtRepository.saveHalogenData(halGDTO);
    }
    public int updateHalogenData(partDetailHalGDTO halGDTO){
        return partMgmtRepository.updateHalogenData(halGDTO);
    }

    //etc 데이터 로직
    public int insertEtcData(partDetailEtcDTO etcDTO){
        return partMgmtRepository.insertEtcData(etcDTO);
    }
    public int deleteEtcData(int ETC_IDX){
        return partMgmtRepository.deleteEtcData(ETC_IDX);
    }

    /*********************************************************************************************************************
     ** Detail v페이지
     ** svhc 페이지 **
     *********************************************************************************************************************/
}
