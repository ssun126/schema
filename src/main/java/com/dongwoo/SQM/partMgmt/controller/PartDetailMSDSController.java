package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/partMgmt")
public class PartDetailMSDSController {

    private final PartDetailService partDetailService;


    //수정 버튼 시 이동 페이지
    @GetMapping("/goDetail")
    public String partMgmtGoDetail(@RequestParam("PM_IDX") String idx, Model model){
        model.addAttribute("PM_IDX",idx);
        return "partMgmtList/detail";
    }

    //다음 버튼 클릭 시 저장 후 다음 페이지로 이동
    @PostMapping("/goSvhc")
    public String partMgmtGoSvhc(@ModelAttribute partDetailMsdsDTO msdsDTO
                             , @ModelAttribute partDetailRohsDTO rohsDTO
                             , @ModelAttribute partDetailHalGDTO halgDTO
                             , @ModelAttribute partDetailEtcDTO etcDTO
                             , @RequestParam("MSDS_FILE") MultipartFile mdsdFile
                             , @RequestParam("ROHS_FILE") MultipartFile rohsFile
                             , @RequestParam("HALOGEN_FILE") MultipartFile halgFile
                             , @RequestParam("ETC_FILE") MultipartFile[] etcFile
                             , Model model){
        log.info("test===========================" + etcDTO);
        //log.info("test12222==================="+ etcFile);
        String pm_idx = msdsDTO.getPM_IDX();
        //msds
        //파일 저장 및 삭제
        if(msdsDTO.getFILE_STATUS().equals("Del")) {
            partDetailService.deleteFileData(msdsDTO.getMSDS_FILE_NAME(),msdsDTO.getMSDS_FILE_PATH());
        }
        //신규파일 업로드 후 경로 가져오기
        if(!mdsdFile.isEmpty()){
            String etc_filepath = partDetailService.uploadFileData(msdsDTO.getMSDS_PART_CODE(),mdsdFile);
            String etc_filename = mdsdFile.getOriginalFilename();

            msdsDTO.setMSDS_FILE_NAME(etc_filename);
            msdsDTO.setMSDS_FILE_PATH(etc_filepath);

        }
        partDetailService.saveMsdsData(msdsDTO);

//        //
//        String msdsFilePath = partDetailService.deleteFileData(msdsDTO.getMSDS_FILE_NAME(),msdsDTO.getMSDS_FILE_NAME(),msdsDTO.getMSDS_FILE_PATH(),msdsDTO.getMSDS_FILE_PATH(),"MSDS",mdsdFile);
//        String msdsFileName = msdsFilePath.substring((msdsFilePath.lastIndexOf("/"))+1);
//        msdsDTO.setMSDS_FILE_NAME(msdsFileName);
//        msdsDTO.setMSDS_FILE_PATH(msdsFilePath);

        //rohs
        if(rohsDTO.getFILE_STATUS().equals("Del")) {
            partDetailService.deleteFileData(rohsDTO.getROHS_FILE_NAME(),rohsDTO.getROHS_FILE_PATH());
        }
        //신규파일 업로드 후 경로 가져오기
        if(!rohsFile.isEmpty()){
            String etc_filepath = partDetailService.uploadFileData(msdsDTO.getMSDS_PART_CODE(),rohsFile);
            String etc_filename = rohsFile.getOriginalFilename();

            rohsDTO.setROHS_FILE_NAME(etc_filename);
            rohsDTO.setROHS_FILE_PATH(etc_filepath);
        }
        partDetailService.saveRohsData(rohsDTO);


        //halogen
        if(halgDTO.getFILE_STATUS().equals("Del")) {
            partDetailService.deleteFileData(halgDTO.getHALOGEN_FILE_NAME(),halgDTO.getHALOGEN_FILE_PATH());
        }
        //신규파일 업로드 후 경로 가져오기
        if(!halgFile.isEmpty()){
            String etc_filepath = partDetailService.uploadFileData(msdsDTO.getMSDS_PART_CODE(),halgFile);
            String etc_filename = halgFile.getOriginalFilename();

            halgDTO.setHALOGEN_FILE_NAME(etc_filename);
            halgDTO.setHALOGEN_FILE_PATH(etc_filepath);

        }
        partDetailService.saveHalogenData(halgDTO);

        //etc
        String ETC_IDX = etcDTO.getETC_IDX();
        String PM_IDX = etcDTO.getPM_IDX();
        String CONFIRM_DATE = etcDTO.getETC_CONFIRM_DATE();
        String ANALYSE_ENTRY = etcDTO.getETC_ANALYSE_ENTRY();
        String ANALYSE_RESULT = etcDTO.getETC_ANALYSE_RESULT();
        String FILE_NAME = etcDTO.getETC_FILE_NAME() == null ? "":etcDTO.getETC_FILE_NAME();
        String FILE_PATH = etcDTO.getETC_FILE_PATH()== null ? "":etcDTO.getETC_FILE_PATH();
        String FILE_STATUS = etcDTO.getFILE_STATUS();


        String[] ETC_ETC_IDX = ETC_IDX.split(",");
        String[] ETC_PM_IDX = PM_IDX.split(",");
        String[] ETC_CONFIRM_DATE = CONFIRM_DATE.split(",");
        String[] ETC_ANALYSE_ENTRY = ANALYSE_ENTRY.split(",");
        String[] ETC_ANALYSE_RESULT = ANALYSE_RESULT.split(",");
        String[] ETC_FILE_NAME = FILE_NAME.split(",");
        String[] ETC_FILE_PATH = FILE_PATH.split(",");
        String[] ETC_FILE_STATUS = FILE_STATUS.split(",");

        for(int i = 0; i<etcFile.length; i++){
            partDetailEtcDTO newetcdto = new partDetailEtcDTO();

            newetcdto.setETC_IDX(ETC_ETC_IDX[i]);
            newetcdto.setPM_IDX(ETC_PM_IDX[i]);
            newetcdto.setETC_CONFIRM_DATE(ETC_CONFIRM_DATE[i]);
            newetcdto.setETC_ANALYSE_ENTRY(ETC_ANALYSE_ENTRY[i]);
            newetcdto.setETC_ANALYSE_RESULT(ETC_ANALYSE_RESULT[i]);

            //파일 새로 등록(수정) 시 기존 파일 삭제
            if(ETC_FILE_STATUS[i].equals("Del")) {
                partDetailService.deleteFileData(ETC_FILE_NAME[i],ETC_FILE_PATH[i]);
                newetcdto.setETC_FILE_NAME("");
                newetcdto.setETC_FILE_PATH("");
            }
            //신규파일 업로드 후 경로 가져오기
            MultipartFile files = etcFile[i];
            if(!files.isEmpty()){
                String etc_filepath = partDetailService.uploadFileData(msdsDTO.getMSDS_PART_CODE(),files);
                String etc_filename = files.getOriginalFilename();

                newetcdto.setETC_FILE_NAME(etc_filename);
                newetcdto.setETC_FILE_PATH(etc_filepath);

            }

            partDetailService.saveEtcData(newetcdto);
        }

        //다음버튼(insert, or updqte)
        //저장버튼(insert, or updqte)
        //msds
//        if(msdsDTO.getMSDS_IDX() == 0) {
//            partDetailService.saveMsds(msdsDTO);
//            partDetailService.saveMsds(msdsDTO);
//            partDetailService.saveMsds(msdsDTO);
//        }else{
//            partDetailService.updateMsds(msdsDTO);
//        }


        if(msdsDTO.getINFO_FLAG().equals("save")){
            return "redirect:/user/partMgmt/matReg";
        }else{
            model.addAttribute("PM_IDX",pm_idx);
            return "partMgmtList/svhcDetail";
        }


    }

    //저장 시 저장 후 목록으로 반환
//    @PostMapping("/saveDetail")
//    public String saveDetail(@ModelAttribute partDetailMsdsDTO msdsDTO
//                             , @ModelAttribute partDetailRohsDTO rohsDTO
//                             , @ModelAttribute partDetailHalGDTO hangDTO
//                             , @ModelAttribute partDetailEtcDTO etcDTO){
//
//        log.info("test===========================" + msdsDTO);
//
//        return "/user/partMgmt/matReg";
//    }


}
