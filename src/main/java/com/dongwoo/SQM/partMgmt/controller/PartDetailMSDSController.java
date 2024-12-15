package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.partMgmt.dto.*;
import com.dongwoo.SQM.partMgmt.service.PartDetailService;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/partMgmt")
public class PartDetailMSDSController {

    private final PartDetailService partDetailService;
    private final PartMgmtService partMgmtService;


    //수정 버튼 시 이동 페이지
    @GetMapping("/goDetail")
    public String partMgmtGoDetail(@RequestParam("PM_IDX") String idx, Model model){

        model.addAttribute("PM_IDX",idx);

        PartMgmtDTO partMgmtDTO = partDetailService.getPartData(idx);
        if(partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
        model.addAttribute("partMgmtDTO",partMgmtDTO);
        log.info("partMgmtDTO=============================" + partMgmtDTO);

        //데이터가 있다면 들고와서 뿌려줌
        //MSDS
        partDetailMsdsDTO msdsDTO = partDetailService.getMsdsData(idx);
        if(msdsDTO == null) msdsDTO = new partDetailMsdsDTO();
        model.addAttribute("msdsDTO",msdsDTO);
        log.info("msdsDTO=============================" + msdsDTO);
        //ROHS
        partDetailRohsDTO rohsDTO = partDetailService.getRohsData(idx);
        if(rohsDTO == null) rohsDTO = new partDetailRohsDTO();
        model.addAttribute("rohsDTO",rohsDTO);
        log.info("rohsDTO=============================" + rohsDTO);
        //HALOGEN
        partDetailHalGDTO halGDTO = partDetailService.getHalgData(idx);
        if(halGDTO == null) halGDTO = new partDetailHalGDTO();
        model.addAttribute("halGDTO",halGDTO);
        log.info("halGDTO=============================" + halGDTO);
        //ETC
        List<partDetailEtcDTO> etcListDTO =partDetailService.getEtcData(idx);
        if(etcListDTO == null) {
            partDetailEtcDTO etcDTO = new partDetailEtcDTO();
            etcListDTO.add(etcDTO);
        }
        model.addAttribute("etcListDTO",etcListDTO);
        log.info("etcListDTO=============================" + etcListDTO);

        return "partMgmtList/detail";
    }

    //다음 버튼 클릭 시 저장 후 다음 페이지로 이동
    @PostMapping("/goSvhc")
    //@ResponseBody
    public String partMgmtGoSvhc(@ModelAttribute partDetailMsdsDTO msdsDTO
                             , @ModelAttribute partDetailRohsDTO rohsDTO
                             , @ModelAttribute partDetailHalGDTO halgDTO
                             , @ModelAttribute partDetailEtcDTO etcDTO
                             , @RequestParam("MSDS_FILE") MultipartFile mdsdFile
                             , @RequestParam("ROHS_FILE") MultipartFile rohsFile
                             , @RequestParam("HALOGEN_FILE") MultipartFile halgFile
                             , @RequestParam(value="ETC_FILE",required = false) MultipartFile[] etcFile
                             , @AuthenticationPrincipal UserCustom user
                             , Model model){
        log.info("test===========================" + etcDTO);
        log.info("test22222-----user ====="+user.getUSER_IDX());
        log.info("test22222-----COM_CODE ====="+user.getCOM_CODE());
        //log.info("test12222==================="+ etcFile);
        String pm_idx = msdsDTO.getPM_IDX();

        //msds
        //파일 저장 및 삭제
//
        //신규파일 업로드 후 경로 가져오기if(msdsDTO.getFILE_STATUS().equals("Del")) {
        ////            partDetailService.deleteFileData(msdsDTO.getMSDS_FILE_NAME(),msdsDTO.getMSDS_FILE_PATH());
        ////        }
        //
        if(!mdsdFile.isEmpty()){
            String etc_filepath = partDetailService.uploadFileData(msdsDTO.getMSDS_PART_CODE(),mdsdFile);
            String etc_filename = mdsdFile.getOriginalFilename();

            msdsDTO.setMSDS_FILE_NAME(etc_filename);
            msdsDTO.setMSDS_FILE_PATH(etc_filepath);

        }



        partDetailService.saveMsdsData(msdsDTO);
        //}

//        !rohsDTO.getROHS_CONFIRM_DATE().equals("") || !rohsDTO.getROHS_CONFIRM_DATE().equals("")|| !rohsFile.isEmpty()
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

        if(etcDTO != null){
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

            //파일 새로 등록(수정) 시 기존 파일 삭제
//        for(int j = 0; j < ETC_FILE_STATUS.length; j++) {
//            if(ETC_FILE_STATUS[j].equals("Del")) {
//                partDetailService.deleteFileData(ETC_FILE_NAME[j],ETC_FILE_PATH[j]);
//            }
//        }

            if(etcFile.length>0){
                for(int i = 0; i<etcFile.length; i++){
                    partDetailEtcDTO newetcdto = new partDetailEtcDTO();

                    if(ETC_ETC_IDX.length  !=  0) newetcdto.setETC_IDX(ETC_ETC_IDX[i]);
                    else    newetcdto.setETC_IDX("");

                    newetcdto.setPM_IDX(ETC_PM_IDX[0]);
                    newetcdto.setETC_CONFIRM_DATE(ETC_CONFIRM_DATE[i]);
                    newetcdto.setETC_ANALYSE_ENTRY(ETC_ANALYSE_ENTRY[i]);
                    newetcdto.setETC_ANALYSE_RESULT(ETC_ANALYSE_RESULT[i]);


                    //신규파일 업로드 후 경로 가져오기
                    MultipartFile files = etcFile[i];


                    partDetailService.saveEtcData(newetcdto,files,user.getCOM_CODE());
                }
            }
        }else{
            //데이터없음 - 삭제
            partDetailService.deleteEtcData(pm_idx);
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

        //뭐든 pm_mgmt 작성중으로 변경
        partMgmtService.updateApprovalStatus(pm_idx,"2");


        if(msdsDTO.getINFO_FLAG().equals("save")){
//            model.addAttribute("PM_IDX",pm_idx);
//
//            PartMgmtDTO partMgmtDTO = partDetailService.getPartData(pm_idx);
//            if(partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
//            model.addAttribute("partMgmtDTO",partMgmtDTO);
//            log.info("partMgmtDTO=============================" + partMgmtDTO);
//
//            //데이터가 있다면 들고와서 뿌려줌
//            //MSDS
//            partDetailMsdsDTO msdsDTO2 = partDetailService.getMsdsData(pm_idx);
//            if(msdsDTO2 == null) msdsDTO2 = new partDetailMsdsDTO();
//            model.addAttribute("msdsDTO",msdsDTO2);
//            log.info("msdsDTO=============================" + msdsDTO2);
//            //ROHS
//            partDetailRohsDTO rohsDTO2 = partDetailService.getRohsData(pm_idx);
//            if(rohsDTO2 == null) rohsDTO2 = new partDetailRohsDTO();
//            model.addAttribute("rohsDTO",rohsDTO2);
//            log.info("rohsDTO=============================" + rohsDTO2);
//            //HALOGEN
//            partDetailHalGDTO halGDTO2 = partDetailService.getHalgData(pm_idx);
//            if(halGDTO2 == null) halGDTO2 = new partDetailHalGDTO();
//            model.addAttribute("halGDTO",halGDTO2);
//            log.info("halGDTO=============================" + 2);
//            //ETC
//            List<partDetailEtcDTO> etcListDTO2 =partDetailService.getEtcData(pm_idx);
//            if(etcListDTO2 == null) {
//                partDetailEtcDTO etcDTO2 = new partDetailEtcDTO();
//                etcListDTO2.add(etcDTO2);
//            }
//            model.addAttribute("etcListDTO",etcListDTO2);
//            log.info("etcListDTO=============================" + etcListDTO2);
//
//            return "partMgmtList/detail";

            //목록

            List<HashMap> searchPlantList = partMgmtService.getPlantList();
            //검색 basecode 승인현황
            //List<HashMap> searhApprovalStatus = partMgmtService.getApprovalStatus();

            model.addAttribute("searchPlantList",searchPlantList);
            //model.addAttribute("searhApprovalStatus", searhApprovalStatus);

            return "partMgmtList/main";

        }else{
            model.addAttribute("PM_IDX",pm_idx);
            PartMgmtDTO partMgmtDTO = partDetailService.getPartData(pm_idx);
            if(partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
            model.addAttribute("partMgmtDTO",partMgmtDTO);
            log.info("partMgmtDTO=============================" + partMgmtDTO);
            //svhc data 들고가기
            PartDetailSvhcDTO svhcDTO = partDetailService.getDetailSvhcData(pm_idx);
            if(svhcDTO == null) svhcDTO = new PartDetailSvhcDTO();
            model.addAttribute("svhcDTO",svhcDTO);
            log.info("svhcDTO=============================" + svhcDTO);
            return "partMgmtList/svhcDetail";
        }


    }

    @PostMapping("/goSvhcimsi")
    @ResponseBody
    public String partMgmtGoSvhcimsi(@ModelAttribute partDetailMsdsDTO msdsDTO
            , @ModelAttribute partDetailRohsDTO rohsDTO
            , @ModelAttribute partDetailHalGDTO halgDTO
            , @ModelAttribute partDetailEtcDTO etcDTO
            , @RequestParam("MSDS_FILE") MultipartFile mdsdFile
            , @RequestParam("ROHS_FILE") MultipartFile rohsFile
            , @RequestParam("HALOGEN_FILE") MultipartFile halgFile
            , @RequestParam("ETC_FILE") MultipartFile[] etcFile
            , @AuthenticationPrincipal UserCustom user
            , Model model){
        log.info("test===========================" + etcDTO);
        log.info("test22222-----user ====="+user.getUSER_IDX());
        log.info("test22222-----COM_CODE ====="+user.getCOM_CODE());
        //log.info("test12222==================="+ etcFile);
        String pm_idx = msdsDTO.getPM_IDX();
        String flag = msdsDTO.getINFO_FLAG();

        //msds
        //파일 저장 및 삭제
//
        //신규파일 업로드 후 경로 가져오기if(msdsDTO.getFILE_STATUS().equals("Del")) {
        ////            partDetailService.deleteFileData(msdsDTO.getMSDS_FILE_NAME(),msdsDTO.getMSDS_FILE_PATH());
        ////        }
        //if(!msdsDTO.getMSDS_REG_DATE().equals("") || !msdsDTO.getMSDS_LANG().equals("") || !msdsDTO.getMSDS_APPROVAL_NUM().equals("") || !mdsdFile.isEmpty() ){
        if(!mdsdFile.isEmpty()){
            String etc_filepath = partDetailService.uploadFileData(msdsDTO.getMSDS_PART_CODE(),mdsdFile);
            String etc_filename = mdsdFile.getOriginalFilename();

            msdsDTO.setMSDS_FILE_NAME(etc_filename);
            msdsDTO.setMSDS_FILE_PATH(etc_filepath);

        }
        partDetailService.saveMsdsData(msdsDTO);
        //}

//        !rohsDTO.getROHS_CONFIRM_DATE().equals("") || !rohsDTO.getROHS_CONFIRM_DATE().equals("")|| !rohsFile.isEmpty()
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

        //파일 새로 등록(수정) 시 기존 파일 삭제
//        for(int j = 0; j < ETC_FILE_STATUS.length; j++) {
//            if(ETC_FILE_STATUS[j].equals("Del")) {
//                partDetailService.deleteFileData(ETC_FILE_NAME[j],ETC_FILE_PATH[j]);
//            }
//        }

        if(etcFile.length>0){
            for(int i = 0; i<etcFile.length; i++){
                partDetailEtcDTO newetcdto = new partDetailEtcDTO();

                if(ETC_ETC_IDX.length  !=  0) newetcdto.setETC_IDX(ETC_ETC_IDX[i]);
                else    newetcdto.setETC_IDX("");

                newetcdto.setPM_IDX(ETC_PM_IDX[0]);
                newetcdto.setETC_CONFIRM_DATE(ETC_CONFIRM_DATE[i]);
                newetcdto.setETC_ANALYSE_ENTRY(ETC_ANALYSE_ENTRY[i]);
                newetcdto.setETC_ANALYSE_RESULT(ETC_ANALYSE_RESULT[i]);


                //신규파일 업로드 후 경로 가져오기
                MultipartFile files = etcFile[i];


                partDetailService.saveEtcData(newetcdto,files,user.getCOM_CODE());
            }
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

        return "ok";
//        if(msdsDTO.getINFO_FLAG().equals("save")){
//
//        }else{
//            model.addAttribute("PM_IDX",pm_idx);
//            PartMgmtDTO partMgmtDTO = partDetailService.getPartData(pm_idx);
//            if(partMgmtDTO == null) partMgmtDTO = new PartMgmtDTO();
//            model.addAttribute("partMgmtDTO",partMgmtDTO);
//            log.info("partMgmtDTO=============================" + partMgmtDTO);
//            //svhc data 들고가기
//            PartDetailSvhcDTO svhcDTO = partDetailService.getDetailSvhcData(pm_idx);
//            if(svhcDTO != null) model.addAttribute("svhcDTO",svhcDTO);
//            return "partMgmtList/svhcDetail";
//        }


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
