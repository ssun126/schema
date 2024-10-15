package com.dongwoo.SQM.system.code.controller;

import com.dongwoo.SQM.system.code.dto.BaseCodeDTO;
import com.dongwoo.SQM.system.code.service.BaseCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BaseCodeController {

    private final BaseCodeService baseCodeService;
    @GetMapping("/baseCode/save")
    public String save() {
        return "/baseCode/save";
    }

    @PostMapping("/baseCode/save")
    public String save(BaseCodeDTO baseCodeDTO) throws IOException {
        log.info("post baseCodeDTO = {}", baseCodeDTO);
        baseCodeService.save(baseCodeDTO);
        return "redirect:/baseCode/list";
    }

    @GetMapping("/baseCode/list")
    public String findAll(Model model) {
        List<BaseCodeDTO> baseCodeDTOList = baseCodeService.findAll();
        model.addAttribute("baseCodeList", baseCodeDTOList);
        log.info("get List = {}", baseCodeDTOList);
        return "/baseCode/list";
    }

    /**
     *  코드그룹으로 검색
     * @param baseCodeDTO
     * @return
     */
    @PostMapping("/baseCode/getCode")
    public String findByCodeGroup(BaseCodeDTO baseCodeDTO) {
        // 상세내용 가져옴
        List<BaseCodeDTO> baseCodeDTOList = baseCodeService.findByCodeGroup(baseCodeDTO);
        log.info("baseCodeDTO = {}", baseCodeDTOList);
        return "/baseCode/detail";
    }

    /**
     * 코드명으로 검색
     *
     * @param baseCodeDTO
     * @return
     */
    @GetMapping("/baseCode/get")
    @ResponseBody
    public List<BaseCodeDTO> findByCode(BaseCodeDTO baseCodeDTO){
        List<BaseCodeDTO> baseCodeDTOList= baseCodeService.findByCodeGroup(baseCodeDTO);
        log.info("get baseCodeDTOList = {}", baseCodeDTOList);
        return baseCodeDTOList;
    }
}
