package com.dongwoo.SQM.companyInfo.controller;

import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.service.CompanyInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class CpCodeMgmtController {
    private final CompanyInfoService companyInfoService;


    @GetMapping("/admin/companyInfo/cpCodeMgmt")
    @PreAuthorize("hasRole('ADMIN')")
    public String cpCodeMgmtMain(Model model) {
        return "companyInfo/main";
    }

    @PostMapping("/admin/companyInfo/save")
    public String save(CompanyInfoDTO companyInfoDTO) throws IOException {
       // System.out.println("companyInfoDTO = " + companyInfoDTO);
       // companyInfoService.save(companyInfoDTO);
        return "redirect:/companyInfo/list";
    }

    @GetMapping("/admin/companyInfo/list")
    public String findAll(Model model) {
       // List<CompanyInfoDTO> companyInfoDTOList = companyInfoService.findAll();
       // model.addAttribute("companyInfoList", companyInfoDTOList);
        //System.out.println("companyInfoDTOList = " + companyInfoDTOList);
        return "/companyInfo/list";
    }

    @GetMapping("/admin/companyInfo/{id}")
    public String findById(@PathVariable("id") int id, Model model) {

        // 상세내용 가져옴
       // CompanyInfoDTO companyInfoDTO = companyInfoService.findById(id);
       // model.addAttribute("companyInfo", companyInfoDTO);
        //System.out.println("companyInfoDTO = " + companyInfoDTO);
//        if (companyInfoDTO.getATTACHED_FILE().equals(1)) {
//            List<CompanyInfoFileDTO> companyInfoFileDTOList = companyInfoService.findFile(id);
//            model.addAttribute("companyInfoFileList", companyInfoFileDTOList);
//        }
        return "/companyInfo/detail";
    }

    @GetMapping("/admin/companyInfo/update/{id}")
    public String update(@PathVariable("id") int id, Model model) {
        //CompanyInfoDTO companyInfoDTO = companyInfoService.findById(id);
        //model.addAttribute("companyInfo", companyInfoDTO);
        return "/companyInfo/update";
    }

    @PostMapping("/admin/companyInfo/update/{id}")
    public String update(CompanyInfoDTO companyInfoDTO, Model model) {
        //companyInfoService.update(companyInfoDTO);
       /* CompanyInfoDTO dto = companyInfoService.findById(companyInfoDTO.getBOARD_IDX());
        model.addAttribute("companyInfo", dto);*/
        return "/companyInfo/detail";
    }

    @GetMapping("/admin/companyInfo/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        //companyInfoService.delete(id);
        return "redirect:/companyInfo/list";
    }
}
