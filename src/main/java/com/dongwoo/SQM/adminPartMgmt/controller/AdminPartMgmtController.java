package com.dongwoo.SQM.adminPartMgmt.controller;

import com.dongwoo.SQM.adminPartMgmt.dto.AdminPartMgmtDTO;
import com.dongwoo.SQM.adminPartMgmt.service.AdminPartMgmtService;
import com.dongwoo.SQM.partMgmt.dto.PartMgmtDTO;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminPartMgmtController {

    private final PartMgmtService partMgmtService;
    private final AdminPartMgmtService adminPartMgmtService;

    @GetMapping("/admin/partMgmt/approvalState")
    public String PartMgmtList(Model model) {
        //바인딩 리스트
        //검색 basecode 취급플랜트
        List<HashMap> searchPlantList = partMgmtService.getPlantList();
        //검색 basecode 승인현황
        //List<HashMap> searhApprovalStatus = partMgmtService.getApprovalStatus();

        model.addAttribute("searchPlantList",searchPlantList);
        //model.addAttribute("searhApprovalStatus", searhApprovalStatus);

        return "approvalState/adminMain";
    }

    @GetMapping("/admin/partMgmt/searchAdminPartMgmt")
    public ResponseEntity<?> searchAdminPartMgmt(@RequestParam("code") String code, @RequestParam("name") String name, @RequestParam("reguser") String reguser,
                                            @RequestParam("useyn") String useyn, @RequestParam("plant") String plant, @RequestParam("approval") String approval,
                                            @RequestParam("sdate") String sdate, @RequestParam("edate") String edate){
        //ist<PartMgmtDTO> partMgmtDTOList = PartMgmtService.searchPartMgmt(code,name,reguser,useyn,plant,approval,sdate,edate);
        try{
            AdminPartMgmtDTO parmDTO = new AdminPartMgmtDTO();
            parmDTO.setPM_PART_CODE(code);
            parmDTO.setPM_PART_NAME(name);
            parmDTO.setREG_USER(reguser);
            parmDTO.setPM_PLANT(plant);
            parmDTO.setPM_ACTIVE_YN(useyn);
            parmDTO.setPM_APPROVAL_STATUS(approval);
            parmDTO.setPM_SDATE(sdate);
            parmDTO.setPM_EDATE(edate);

            List<AdminPartMgmtDTO> partMgmtDTOList = adminPartMgmtService.searchAdminPartMgmt(parmDTO);
            log.info("dataaaaaaaa??????????????"+partMgmtDTOList);
            return ResponseEntity.ok(partMgmtDTOList);

        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }



    @GetMapping("/admin/partMgmt/approvalStateDetail")
    public String PartMgmtDetail(Model model) {
        return "approvalState/detail";
    }

    @GetMapping("/admin/partMgmt/expDateMsds")
    public String expDateMsdsList(Model model) {
        return "expDateMsds/main";
    }

    @GetMapping("/admin/partMgmt/expDateRohs")
    public String expDateRohsList(Model model) {
        return "expDateRohs/main";
    }

    @GetMapping("/admin/partMgmt/expDateEtc")
    public String expDateEtcList(Model model) {
        return "expDateEtc/main";
    }

    @GetMapping("/admin/partMgmt/expDateSvhc")
    public String expDateSvhcList(Model model) {
        return "expDateSvhc/main";
    }

    @GetMapping("/admin/partMgmt/expDateDecl")
    public String expDateDeclList(Model model) {
        return "expDateDecl/main";
    }







}
