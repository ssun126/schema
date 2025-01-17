package com.dongwoo.SQM.partMgmt.service;

import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.partMgmt.repository.PartMgmtRepository;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartMgmtService {
    private final PartMgmtRepository partMgmtRepository;

    public List<HashMap> getPlantList() {
        return partMgmtRepository.getPlantList();
    }

    public List<HashMap> getApprovalStatus() {
        return partMgmtRepository.getApprovalStatus();
    }

    public List<PartMgmtDTO> searchPartMgmt(PartMgmtDTO parmDTO) {

        return partMgmtRepository.searchPartMgmt(parmDTO);
    }

    public PartMgmtDTO getPartMgmt(int PM_IDX) {

        return partMgmtRepository.getPartMgmt(PM_IDX);
    }

    public List<HashMap> searchPartCodeList(String COM_CODE, String code, String Name) {
        return partMgmtRepository.searchPartCodeList(COM_CODE, code, Name);
    }

    public PartMgmtDTO getPartMgmtData(String idx) {
        return partMgmtRepository.getPartMgmtData(idx);
    }

    public List<HashMap> getpartCodeList() {
        return partMgmtRepository.getpartCodeList();
    }

    public int save(PartMgmtDTO partMgmtDTO) {
        return partMgmtRepository.save(partMgmtDTO);
    }

    public int updatePartMgmt(PartMgmtDTO partMgmtDTO) {
        return partMgmtRepository.updatePartMgmt(partMgmtDTO);
    }

    public int deletePartMgmt(String idx) {
        String[] arrIdx = idx.split(",");
        int flag = 0;
        for (int i = 0; i < arrIdx.length; i++) {
            flag += partMgmtRepository.deletePartMgmt(arrIdx[i]);
        }
        return flag;
    }

    public void updateApprovalStatus(int idx, String status) {
        partMgmtRepository.updateApprovalStatus(idx, status);
    }

    public void updateActive(String status, int idx) {
        partMgmtRepository.updateActive(status, idx);
    }

    public void updateActiveList(String active , String idxList) {
        partMgmtRepository.updateActiveList(active, idxList);
    }

    //상단 공통 자재코드 정보
    public PartMgmtDTO getPartData(String idx) {
        return partMgmtRepository.getPartData(idx);
    }


    /*********************************************************************************************************************
     ** Detail v페이지
     ** Msds / Rohs / Halogen 페이지 **
     *********************************************************************************************************************/
    //msds 데이터 로직
    public int insertMsdsData(partDetailMsdsDTO msdsDTO) {
        return partMgmtRepository.insertMsdsData(msdsDTO);
    }

    public int updateMsdsData(partDetailMsdsDTO msdsDTO) {
        return partMgmtRepository.updateMsdsData(msdsDTO);
    }

    //rohs 데이터 로직

    public int insertRohsData(partDetailRohsDTO rohsDTO) {
        return partMgmtRepository.insertRohsData(rohsDTO);
    }

    public int updateRohsData(partDetailRohsDTO rohsDTO) {
        return partMgmtRepository.updateRohsData(rohsDTO);
    }

    //halg 데이터 로직
    public int insertHalogenData(partDetailHalGDTO halGDTO) {
        return partMgmtRepository.saveHalogenData(halGDTO);
    }

    public int updateHalogenData(partDetailHalGDTO halGDTO) {
        return partMgmtRepository.updateHalogenData(halGDTO);
    }

    //etc 데이터 로직
    public int insertEtcData(partDetailEtcDTO etcDTO) {
        return partMgmtRepository.insertEtcData(etcDTO);
    }

    public int updateEtcData(partDetailEtcDTO etcDTO) {
        return partMgmtRepository.updateEtcData(etcDTO);
    }

    public int deleteEtcData(int ETC_IDX) {
        return partMgmtRepository.deleteEtcData(ETC_IDX);
    }

    //detail  첫번째 페이지 데이터 가져오기
    public partDetailMsdsDTO getMsdsData(String idx) {
        return partMgmtRepository.getMsdsData(idx);
    }
    public partDetailRohsDTO getRohsData(String idx) {
        return partMgmtRepository.getRohsData(idx);
    }
    public partDetailHalGDTO getHalgData(String idx) {
        return partMgmtRepository.getHalgData(idx);
    }
    public List<partDetailEtcDTO> getEtcData(String idx) {
        return partMgmtRepository.getEtcData(idx);
    }

    //파일삭제
    public void MsdsDeleteFile (int idx) { partMgmtRepository.MsdsDeleteFile(idx);}
    public void RohsDeleteFile (int idx) { partMgmtRepository.RohsDeleteFile(idx);}
    public void HalogenDeleteFile (int idx) { partMgmtRepository.HalogenDeleteFile(idx);}

    public partDetailEtcDTO getEtcDataIdx (int idx) { return partMgmtRepository.getEtcDataIdx(idx);}
    public void EtcDeleteFile (int idx) { partMgmtRepository.EtcDeleteFile(idx);}

    /*********************************************************************************************************************
     ** Detail v페이지
     ** svhc 페이지 **
     *********************************************************************************************************************/
    //svhc 데이터 로직
    public int insertSvhcData(PartDetailSvhcDTO svhcDTO) {
        return partMgmtRepository.insertSvhcData(svhcDTO);
    }

    public int updateSvhcData(PartDetailSvhcDTO svhcDTO) {
        return partMgmtRepository.updateSvhcData(svhcDTO);
    }

    public PartDetailSvhcDTO getDetailSvhcData(String idx) {
        return partMgmtRepository.getDetailSvhcData(idx);
    }

    public List<SvhcListDTO> getSvhcData(){
        return  partMgmtRepository.getSvhcData();

    }

    public void SvhcDeleteFile (int idx) { partMgmtRepository.SvhcDeleteFile(idx);}

    /*********************************************************************************************************************
     ** Detail v페이지
     ** 제품환경 관리물질 페이지 **
     *********************************************************************************************************************/
    //svhc 데이터 로직
    public int insertDeclData(partDetailDeclarDTO declarDTO) {
        return partMgmtRepository.insertDeclData(declarDTO);
    }

    public int updateDeclData(partDetailDeclarDTO declarDTO) {
        return partMgmtRepository.updateDeclData(declarDTO);
    }

    public partDetailDeclarDTO getDetailDeclData(String idx) {
        return partMgmtRepository.getDetailDeclData(idx);
    }

    public List<DeclarationDTO> getDeclData(){
        return  partMgmtRepository.getDeclarData();

    }

    public void DeclDeleteFile (int idx) { partMgmtRepository.DeclDeleteFile(idx);}


    /*********************************************************************************************************************
     ** Detail v페이지
     ** SCCS / 성분명세서 / 기타보증 페이지 **
     *********************************************************************************************************************/
    //SCCS 데이터 로직
    public partDetailSccsDTO getSccsData(String idx) {
        return partMgmtRepository.getSccsData(idx);
    }

    public int insertSccsData(partDetailSccsDTO sccsDTO) {
        return partMgmtRepository.insertSccsData(sccsDTO);
    }

    public int updateSccsData(partDetailSccsDTO sccsDTO) {
        return partMgmtRepository.updateSccsData(sccsDTO);
    }

    //성분명세서 데이터 로직
    public partDetailIngredDTO getIngredData(String idx) {
        return partMgmtRepository.getIngredData(idx);
    }

    public int insertIngredData(partDetailIngredDTO ingredDTO) {
        return partMgmtRepository.insertIngredData(ingredDTO);
    }

    public int updateIngredData(partDetailIngredDTO ingredDTO) {
        return partMgmtRepository.updateIngredData(ingredDTO);
    }


    //기타보증 데이터 로직
    public List<partDetailGuarantDTO> getGuarantData(String idx) {
        return partMgmtRepository.getGuarantData(idx);
    }

    public int insertGuarantData(partDetailGuarantDTO guarantDTO) {
        return partMgmtRepository.insertGuarantData(guarantDTO);
    }

    public int updateGuarantData(partDetailGuarantDTO guarantDTO) {
        return partMgmtRepository.updateGuarantData(guarantDTO);
    }

    public int deleteGuarantData(int GUARANT_IDX) {
        return partMgmtRepository.deleteGuarantData(GUARANT_IDX);
    }

    //파일삭제
    public void SccsDeleteFile (int idx) { partMgmtRepository.SccsDeleteFile(idx);}
    public void IngredDeleteFile (int idx) { partMgmtRepository.IngredDeleteFile(idx);}

    public partDetailGuarantDTO getGuarantDataIdx (int idx) { return partMgmtRepository.getGuarantDataIdx(idx);}
    public void GuarantDeleteFile (int idx) { partMgmtRepository.GuarantDeleteFile(idx);}


    /**********************
     * initConfirmChk
     * **************/
    public void initConfirmChk(int pmidx){partMgmtRepository.initConfirmChk(pmidx);}


    /**********************
     * history data set
     * **************/
    public void setHistoryData(int pmidx, int userIdx , String gubun){

        partMgmtRepository.setHistoryData(pmidx,userIdx,gubun);
    }

    /* MSDS/ RoHS Halogen  */
    public partDetailMsdsDTO getOrignMsdsData(int pmidx){ return partMgmtRepository.getOrignMsdsData(pmidx);}
    public partDetailRohsDTO getOrignRohsData(int pmidx){ return partMgmtRepository.getOrignRohsData(pmidx);}
    public partDetailHalGDTO getOrignHalgData(int pmidx){ return partMgmtRepository.getOrignHalgData(pmidx);}

    /* Svhc */
    public PartDetailSvhcDTO getOrignSvhcData(int pmidx) { return partMgmtRepository.getOrignSvhcData(pmidx);}

    /* 제품환경 관리물질 */
    public partDetailDeclarDTO getOrignDeclData(int pmidx) { return partMgmtRepository.getOrignDeclData(pmidx);}

    /* Sccs 성분명세 */
    public partDetailSccsDTO getOrignSccsData(int pmidx) { return partMgmtRepository.getOrignSccsData(pmidx);}
    public partDetailIngredDTO getOrignIngredData(int pmidx) { return partMgmtRepository.getOrignIngredData(pmidx);}

    /*********************************************************************************************************************
     ** Detail v페이지
     ** 파일 관련
     *********************************************************************************************************************/

    public Boolean deleteFileData (String orgName, String orgPath){

        Boolean flag = true;
        try{
            if( orgName != null && orgPath != null ){
                //파일 삭제
                File file = new File(orgName+orgPath);
                if(file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            flag=false;
            //throw new RuntimeException(e);
        }


        return flag;

    }

    public void deleteFileData_Idx (String idx){
        String orgName= null;
        String orgPath= null;

//        HashMap<String,Object> map = partMgmtRepository.getFileData(idx);
//        if(map != null){
//            orgName = (String) map.get("FILE_NAME");
//            orgPath = (String) map.get("FILE_PATH");
//
//            if( orgName != null && orgPath != null ){
//                //파일 삭제
//                File file = new File(orgName+orgPath);
//                if(file.exists()) file.delete();
//            }
//        }


    }

    public String uploadFileData(String partcode, MultipartFile fileInfo){
        String sUrl = "/"+partcode+"/";
        String filePath = "";

        try{
            String fileName = fileInfo.getOriginalFilename();
            Path path = Paths.get("./uploads"+sUrl+fileName);
            Files.createDirectories((path.getParent()));
            fileInfo.transferTo(path);
            //sampleFileDTOList.add(sampleFileDTO);

            filePath ="./uploads"+sUrl;

        }catch (IOException e){
            e.printStackTrace();
            //model.addAttribute("message","파일업로드중 오류 : " + file.getOriginalFilename());

        }

        return filePath;
    }

    public Map<String,String> getMsdsFileData (int idx){ return  partMgmtRepository.getMsdsFileData(idx);}
    public Map<String,String> getRohsFileData (int idx){ return  partMgmtRepository.getRohsFileData(idx);}
    public Map<String,String> getHalgFileData (int idx){ return  partMgmtRepository.getHalgFileData(idx);}
    public Map<String,String> getEtcFileData (int idx){ return  partMgmtRepository.getEtcFileData(idx);}
    public Map<String,String> getDetailSvhcFileData (int idx){ return  partMgmtRepository.getDetailSvhcFileData(idx);}
    public Map<String,String> getDetailDeclFileData (int idx){ return  partMgmtRepository.getDetailDeclFileData(idx);}
    public Map<String,String> getSccsFileData (int idx){ return  partMgmtRepository.getSccsFileData(idx);}
    public Map<String,String> getIngredFileData (int idx){ return  partMgmtRepository.getIngredFileData(idx);}
    public Map<String,String> getGuarantDataFileData (int idx){ return  partMgmtRepository.getGuarantDataFileData(idx);}





}
