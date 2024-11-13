package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.service.BaseCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class BaseCodeController {

    private final BaseCodeService baseCodeService;
    @GetMapping("/siteMgr/baseCode/save")
    public String save() {
        return "/baseCode/save";
    }

    @PostMapping("/siteMgr/baseCode/save")
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

    @GetMapping("/siteMgr/baseCode/baseCodeInfo")
    public ResponseEntity<?> getBaseConfigInfo(@RequestParam("param1") String idx) {
        BaseCodeDTO baseCodeDTO = baseCodeService.getbaseCodeInfo(idx);

        if (baseCodeDTO != null) {
            return ResponseEntity.ok().body(baseCodeDTO);  // 회사 정보가 있을 경우 응답
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Data not found.");
        }
    }

    @GetMapping("/siteMgr/baseCode/action")
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
            //코드그룹 추가 일 경우 그룹코드와 코드가 동일하게 저장된다
            String sGROUP_CODE= baseCodeDTO.getGROUP_CODE();
            log.info(sGROUP_CODE);
            log.info(baseCodeDTO.getBASE_CODE());
            if(Objects.equals(sGROUP_CODE, "GroupAdd"))
                baseCodeDTO.setGROUP_CODE(baseCodeDTO.getBASE_CODE());
            log.info(baseCodeDTO.getGROUP_CODE());

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

    @GetMapping("/siteMgr/basecode/search")
    public String findSearch(Model model, HttpServletRequest request){
        String sGubun = request.getParameter("GUBUN");
        String sSearchKey = request.getParameter("SEARCHKEY");
        String sSearchVal = request.getParameter("SEARCHVAL");

        List<BaseCodeDTO> baseCodeDTOList = baseCodeService.findSearch(sGubun,sSearchKey,sSearchVal);
        model.addAttribute("baseCodeList",baseCodeDTOList);
        List<BaseCodeDTO> baseGubunList = baseCodeService.getbaseGubunList();
        List<BaseCodeDTO> baseGroupCDList = baseCodeService.getbaseGroupCDList();
        //검색,모달 업무구분 리스트
        model.addAttribute("baseGubunList",baseGubunList);
        //모달 그룹코드 리스트
        model.addAttribute("baeGroupCDList",baseGroupCDList);
        log.info("111get List = {}", baseCodeDTOList);
        //return "/siteMgr/baseConfig";
        return "/baseCode/list";
    }


    /**
     *  코드그룹으로 검색
     * @param baseCodeDTO
     * @return
     */
    @PostMapping("/siteMgr/baseCode/getCode")
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
    @GetMapping("/siteMgr/baseCode/get")
    @ResponseBody
    public List<BaseCodeDTO> findByCode(BaseCodeDTO baseCodeDTO){
        List<BaseCodeDTO> baseCodeDTOList= baseCodeService.findByCodeGroup(baseCodeDTO);
        log.info("get baseCodeDTOList = {}", baseCodeDTOList);
        return baseCodeDTOList;
    }
}
