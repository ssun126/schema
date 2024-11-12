package com.dongwoo.SQM.companyInfo.controller;

import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoParamDTO;
import com.dongwoo.SQM.companyInfo.service.CompanyInfoService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrParamDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CompanyListController {

    private final CompanyInfoService companyInfoService;

    //업체 목록  2024.11.08 sylee
    @GetMapping("/admin/companyInfo/cpList")
    public String cpListMain(Model model) {
        List<BaseCodeDTO> deptList = companyInfoService.GetBaseCode("CpWorkCode");
        model.addAttribute("deptList", deptList);

        return "companyList/cpList";
    }

    //업체 목록 검색 LIST
    @PostMapping("/admin/companyInfo/getCompanyInfo")
    public ResponseEntity<?> getcompanyInfo(@RequestBody CompanyInfoParamDTO companyInfoParamDTO ) {
        try {
            System.out.println("Received companyInfoDTO: " + companyInfoParamDTO);

            List<CompanyInfoDTO> companyList = companyInfoService.findCompanySearch(companyInfoParamDTO , "List");

            //System.out.println("select companyUserList: " + companyUserList);
            return ResponseEntity.ok(companyList);

        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }


    //업체 목록 검색 LIST -> 업체 상세  2024.11.08 sylee
    @GetMapping("/admin/companyInfo/cpDetail")
    public String cpListDetail(@RequestParam("com_code") String com_code , Model model) {
        List<BaseCodeDTO> deptList = companyInfoService.GetBaseCode("CpWorkCode");
        model.addAttribute("deptList", deptList);

        return "companyList/cpDetail";
    }




    //업체 승인 목록  2024.11.11 sylee
    @GetMapping("/admin/companyInfo/cpApproval")
    public String cpApprovalListMain(Model model) {
        List<BaseCodeDTO> deptList = companyInfoService.GetBaseCode("CpWorkCode");
        model.addAttribute("deptList", deptList);

        return "companyList/cpApprovallist";
    }

    //업체 승인 목록 검색 LIST  2024.11.12 sylee
    @PostMapping("/admin/companyInfo/getCompanyApprovalList")
    public ResponseEntity<?> getCompanyApprovalList(@RequestBody CompanyInfoParamDTO companyInfoParamDTO ) {
        try {
            System.out.println("Received companyInfoDTO: " + companyInfoParamDTO);
            List<CompanyInfoDTO> companyList = companyInfoService.findCompanySearch(companyInfoParamDTO ,"Approval");
            return ResponseEntity.ok(companyList);
        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }


/////////////


    @PostMapping("/admin/companyInfo/cpList/save")
    public String save(CompanyInfoDTO companyInfoDTO) throws IOException {
       // companyInfoService.save(companyInfoDTO);
        return "redirect:/user/companyInfo/cpList";
    }

    @GetMapping("/admin/companyInfo/cpList/list")
    public String findAll(Model model) {
       // List<CompanyInfoDTO> companyInfoDTOList = companyInfoService.findAll();
       // model.addAttribute("companyInfoList", companyInfoDTOList);
        return "/companyInfo/list";
    }

    @GetMapping("/admin/companyInfo/cpList/{id}")
    public String findById(@PathVariable("id") int id, Model model) {

        // 상세내용 가져옴
       // CompanyInfoDTO companyInfoDTO = companyInfoService.findById(id);
       // model.addAttribute("companyInfo", companyInfoDTO);
//        if (companyInfoDTO.getATTACHED_FILE().equals(1)) {
//            List<CompanyInfoFileDTO> companyInfoFileDTOList = companyInfoService.findFile(id);
//            model.addAttribute("companyInfoFileList", companyInfoFileDTOList);
//        }
        return "/companyInfo/detail";
    }

    @GetMapping("/admin/companyInfo/cpList/update/{id}")
    public String update(@PathVariable("id") int id, Model model) {
        //CompanyInfoDTO companyInfoDTO = companyInfoService.findById(id);
        //model.addAttribute("companyInfo", companyInfoDTO);
        return "/companyInfo/update";
    }

    @PostMapping("/admin/companyInfo/cpList/update/{id}")
    public String update(CompanyInfoDTO companyInfoDTO, Model model) {
        //companyInfoService.update(companyInfoDTO);
       /* CompanyInfoDTO dto = companyInfoService.findById(companyInfoDTO.getBOARD_IDX());
        model.addAttribute("companyInfo", dto);*/
        return "/companyInfo/detail";
    }

    @GetMapping("/admin/companyInfo/cpList/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        //companyInfoService.delete(id);
        return "redirect:/companyInfo/list";
    }








}
