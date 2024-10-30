package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.service.BaseCodeService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    //@GetMapping("/baseCode/list")
    @GetMapping("/siteMgr/baseCode")
    public String findAll(Model model) {
        log.info("test-===============findall");
        List<BaseCodeDTO> baseCodeDTOList = baseCodeService.findAll();
        List<BaseCodeDTO> baseGubunList = baseCodeService.getbaseGubunList();
        List<BaseCodeDTO> baseGroupCDList = baseCodeService.getbaseGroupCDList();
        //기초코드 리스트
        model.addAttribute("baseCodeList", baseCodeDTOList);
        //검색,모달 업무구분 리스트
        model.addAttribute("baseGubunList",baseGubunList);
        //모달 그룹코드 리스트
        model.addAttribute("baeGroupCDList",baseGroupCDList);
        log.info("get List = {}", baseCodeDTOList);
        return "/baseCode/list";
    }

    @PostMapping("/baseCode/baseCode_Info")
    public @ResponseBody Map<String, String> getBaseConfig_Info(@RequestParam("status") String idx) {
        System.out.println("status = " + idx);
        BaseCodeDTO baseCodeDTO = baseCodeService.getBaseCode_Info(idx);
        log.info("================test22222222aa");


        HashMap<String,String> response = new HashMap<>();

        if(baseCodeDTO != null) {
            response.put("status", "ok");
            response.put("BASE_CODE_IDX", String.valueOf(baseCodeDTO.getBASE_CODE_IDX()));
            response.put("GUBN", String.valueOf(baseCodeDTO.getGUBN()));
            response.put("GROUP_CODE", String.valueOf(baseCodeDTO.getGROUP_CODE()));
            response.put("BASE_CODE", String.valueOf(baseCodeDTO.getBASE_CODE()));
            response.put("BASE_NAME", String.valueOf(baseCodeDTO.getBASE_NAME()));
            response.put("BASE_VALUE", String.valueOf(baseCodeDTO.getBASE_VALUE()));
            response.put("BASE_SUMMARY", String.valueOf(baseCodeDTO.getBASE_SUMMARY()));
            response.put("BASE_SORT", String.valueOf(baseCodeDTO.getBASE_SORT()));
            response.put("BASE_STATUS", String.valueOf(baseCodeDTO.getBASE_STATUS()));
            response.put("BASE_OPTION", String.valueOf(baseCodeDTO.getBASE_OPTION()));

        }
        return response;
    }

    @GetMapping("/baseCode/action")
    public String save(@ModelAttribute BaseCodeDTO baseCodeDTO,HttpSession session) {
        log.info("test111111");
        //log.info(GUBN);
        //log.info(String.valueOf(baseCodeDTO.getREG_DW_USER_IDX()));
        String sUserID = (String) session.getAttribute("loginId");
        if(sUserID==null) sUserID="0";
        log.info(sUserID);
        //baseCodeDTO.setREG_DW_USER_IDX();

        //String sFlag = httpServletRequest.getParameter("baseConfigFlag");
        String sFlag = baseCodeDTO.getINFO_FLAG();
        log.info(sFlag);
        // add : 추가, Mod : 수정, Del :  삭제
        if(sFlag.equals("Add")) {
            log.info("test= 추가");
            baseCodeDTO.setREG_DW_USER_IDX(1);
            baseCodeService.save(baseCodeDTO);
            log.info("test= 추가끝");
        }else if(sFlag.equals("Mod")){

            baseCodeService.update(baseCodeDTO);
        }else{
            int sidx = baseCodeDTO.getBASE_CODE_IDX();
            baseCodeService.delete(sidx);
        }


        return  "redirect:/siteMgr/baseCode";
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
