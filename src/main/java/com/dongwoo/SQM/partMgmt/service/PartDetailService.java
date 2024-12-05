package com.dongwoo.SQM.partMgmt.service;

import com.dongwoo.SQM.partMgmt.dto.partDetailEtcDTO;
import com.dongwoo.SQM.partMgmt.dto.partDetailHalGDTO;
import com.dongwoo.SQM.partMgmt.dto.partDetailMsdsDTO;
import com.dongwoo.SQM.partMgmt.dto.partDetailRohsDTO;
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

        orgName = (String) map.get("FILE_NAME");
        orgPath = (String) map.get("FILE_PATH");

        if( orgName != null && orgPath != null ){
            //파일 삭제
            File file = new File(orgName+orgPath);
            if(file.exists()) file.delete();
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

    //svhc
    public List<SvhcListDTO> getSvhcData(){
        return  partMgmtRepository.getSvhcData();

    }

    //declar
    public List<DeclarationDTO> getDeclarData(){
        return partMgmtRepository.getDeclarData();

    }



}
