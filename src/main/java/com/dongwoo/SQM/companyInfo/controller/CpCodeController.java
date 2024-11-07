package com.dongwoo.SQM.companyInfo.controller;

import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.service.CompanyInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CpCodeController {
    private final CompanyInfoService companyInfoService;

    @GetMapping("/admin/companyInfo/cpCode")
    public String cpCodeMgmtMain(Criteria criteria, Model model) {
        List<CompanyInfoDTO> companyInfoList = companyInfoService.getList(criteria);
        model.addAttribute("companyList", companyInfoList);
        model.addAttribute("pageMaker", new PageDTO(companyInfoService.getTotal(), 10, criteria));
        log.info("companyList = " + companyInfoList);
        return "cpCodeMgmt/main";
    }

    @PostMapping("/admin/companyInfo/cpCode/save")
    public String cpCodeSave(CompanyInfoDTO companyInfoDTO) throws IOException {
       // companyInfoService.save(companyInfoDTO);
        return "redirect:/companyInfo/list";
    }

    @GetMapping("/admin/companyInfo/cpCode/list")
    public String findAll(Model model) {
       // List<CompanyInfoDTO> companyInfoDTOList = companyInfoService.findAll();
       // model.addAttribute("companyInfoList", companyInfoDTOList);
        return "/companyInfo/list";
    }

    @PostMapping("/admin/companyInfo/cpCode/{id}")
    public CompanyInfoDTO findById(@PathVariable("id") String id, Model model) {
        // 상세내용 가져옴

        return companyInfoService.findByCompanyId(id);
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
