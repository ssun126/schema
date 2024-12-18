package com.dongwoo.SQM.partMgmt.service;

import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.partMgmt.repository.PartMgmtRepository;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartDetailService {

    private final PartMgmtRepository partMgmtRepository;

    public void deleteFileData (String orgName, String orgPath){

        if( orgName != null && orgPath != null ){
            //파일 삭제
            File file = new File(orgName+orgPath);
            if(file.exists()) file.delete();
        }

    }

    public void deleteFileData_Idx (String idx){
        String orgName= null;
        String orgPath= null;

        HashMap<String,Object> map = partMgmtRepository.getFileData(idx);
        if(map != null){
            orgName = (String) map.get("FILE_NAME");
            orgPath = (String) map.get("FILE_PATH");

            if( orgName != null && orgPath != null ){
                //파일 삭제
                File file = new File(orgName+orgPath);
                if(file.exists()) file.delete();
            }
        }


    }

    public String uploadFileData(String partcode, MultipartFile fileInfo){
        String sUrl = "/"+partcode+"/";
        String filePath = "";

        try{
            String fileName = fileInfo.getOriginalFilename();
            Path path = Paths.get("./uploads"+sUrl+fileName);
            Files.createDirectories((path.getParent()));
            fileInfo.transferTo((path));
            //sampleFileDTOList.add(sampleFileDTO);

            filePath ="./uploads"+sUrl;

        }catch (IOException e){
            e.printStackTrace();
            //model.addAttribute("message","파일업로드중 오류 : " + file.getOriginalFilename());

        }

        return filePath;
    }

    //상단 공통 자재코드 정보
    public PartMgmtDTO getPartData(String idx){
        return partMgmtRepository.getPartData(idx);
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

    public void saveMsdsData(partDetailMsdsDTO msdsDTO){

        if(msdsDTO.getMSDS_IDX().equals("")){
            partMgmtRepository.saveMsdsData(msdsDTO);
        }else{
            partMgmtRepository.updateMsdsData(msdsDTO);
        }
    }

    public void saveRohsData(partDetailRohsDTO rohsDTO){
        if(rohsDTO.getROHS_IDX().equals("")){
            partMgmtRepository.saveRohsData(rohsDTO);
        }else{
            partMgmtRepository.updateRohsData(rohsDTO);
        }

    }

    public void saveHalogenData(partDetailHalGDTO halGDTO){
        if(halGDTO.getHALOGEN_IDX().equals("")){
            partMgmtRepository.saveHalogenData(halGDTO);
        }else{
            partMgmtRepository.updateHalogenData(halGDTO);
        }

    }

    public void saveEtcData(partDetailEtcDTO etcDTO,MultipartFile files,String comCode){
        if(etcDTO.getETC_IDX().equals("")){
            partMgmtRepository.saveEtcData(etcDTO);
        }else{
            //수정
            if(!files.isEmpty()){
                //기존파일 삭제
                deleteFileData_Idx(etcDTO.getETC_IDX());
                //String etc_filepath = uploadFileData(msdsDTO.getMSDS_PART_CODE(),files);
                String etc_filepath = uploadFileData(comCode,files);
                String etc_filename = files.getOriginalFilename();

                etcDTO.setETC_FILE_NAME(etc_filename);
                etcDTO.setETC_FILE_PATH(etc_filepath);

            }
            partMgmtRepository.updateEtcData(etcDTO);
        }

    }

    public void deleteEtcData(String idx){
        partMgmtRepository.deleteEtcData(idx);
    }

    //svhc
    public List<SvhcListDTO> getSvhcData(){
        return  partMgmtRepository.getSvhcData();

    }

    public PartDetailSvhcDTO getDetailSvhcData(String idx){
        return partMgmtRepository.getDetailSvhcData(idx);
    }

    public int  saveDetailSvhcData(PartDetailSvhcDTO partDetailSvhcDTO, MultipartFile files, String comCode){
        if(!files.isEmpty()) {
            String svhc_filepath = uploadFileData(comCode,files);
            String svhc_filename = files.getOriginalFilename();

            partDetailSvhcDTO.setFILE_NAME(svhc_filename);
            partDetailSvhcDTO.setFILE_PATH(svhc_filepath);
        }

        if(partDetailSvhcDTO.getSVHC_IDX().equals("")) {
            return partMgmtRepository.saveDetailSvhcData(partDetailSvhcDTO);
        }else{
            return partMgmtRepository.updateDetailSvhcData(partDetailSvhcDTO);
        }
    }

    //declar
    public List<DeclarationDTO> getDeclarData(){
        return partMgmtRepository.getDeclarData();

    }

    public partDetailDeclarDTO getDetailDeclarData(String idx){
        return partMgmtRepository.getDetailDeclarData(idx);
    }

    public int  saveDetailDeclarData(partDetailDeclarDTO declarDTO, MultipartFile files, String comCode){
        if(!files.isEmpty()) {
            String svhc_filepath = uploadFileData(comCode,files);
            String svhc_filename = files.getOriginalFilename();

            declarDTO.setFILE_NAME(svhc_filename);
            declarDTO.setFILE_PATH(svhc_filepath);
        }

        if(declarDTO.getDECL_IDX().equals("")) {
            return partMgmtRepository.saveDetailDeclarData(declarDTO);
        }else{
            return partMgmtRepository.updateDetailDeclarData(declarDTO);
        }
    }

    //sccs
    public partDetailSccsDTO getSccsData(String idx) {
        return partMgmtRepository.getSccsData(idx);
    }


    public int saveSccsData(partDetailSccsDTO sccsDTO){

        if(sccsDTO.getSCCS_IDX().equals("")){
            return partMgmtRepository.saveSccsData(sccsDTO);
        }else{
            return partMgmtRepository.updateSccsData(sccsDTO);
        }
    }

    //ingred
    public partDetailIngredDTO getIngredData(String idx) {
        return partMgmtRepository.getIngredData(idx);
    }

    public int saveIngredData(partDetailIngredDTO ingredDTO){

        if(ingredDTO.getINGRED_IDX().equals("")){
            return partMgmtRepository.saveIngredData(ingredDTO);
        }else{
            return partMgmtRepository.updateIngredData(ingredDTO);
        }
    }

    //guarant
    public List<partDetailGuarantDTO> getGuarantData(String idx) {
        return partMgmtRepository.getGuarantData(idx);
    }

    public int saveGuarantData(partDetailGuarantDTO guarantDTO){

        if(guarantDTO.getGUARANT_IDX().equals("")){
            return partMgmtRepository.saveGuarantData(guarantDTO);
        }else{
            return partMgmtRepository.updateGuarantData(guarantDTO);
        }
    }

    //승인요청
    public int updateApprovalStatus(String idx){
        return partMgmtRepository.updateApprovalStatus(idx);
    }



}
