package com.dongwoo.SQM.companyInfo.controller;

import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoParamDTO;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import com.dongwoo.SQM.companyInfo.dto.PartCodeDTO;
import com.dongwoo.SQM.companyInfo.service.CompanyInfoService;
import com.dongwoo.SQM.companyInfo.service.PartCodeService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.system.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PartCodeController {

    private final CompanyInfoService companyInfoService;
    private final PartCodeService partCodeService;
    private final MemberService memberService;


    //자재등록
    @GetMapping("/admin/companyInfo/partCodeList")
    public String cpMaterial(Model model) {
        List<BaseCodeDTO> deptList = partCodeService.GetBaseCode("CpWorkCode");
        List<BaseCodeDTO> plantList = partCodeService.GetBaseCode("PLANT");

        model.addAttribute("deptList", deptList);
        model.addAttribute("plantList", plantList);

        return "partCodeMgmt/partCodeList";
    }


    //자재 등록 목록검색 LIST
    @PostMapping("/admin/companyInfo/getPartCodeList")
    public ResponseEntity<?> getcpMaterial(@RequestBody PartCodeDTO partCodeDTO ) {
        try {
            System.out.println("Received partCodeDTO: " + partCodeDTO);
            List<PartCodeDTO> partCodeList = partCodeService.partCodeList(partCodeDTO);
            return ResponseEntity.ok(partCodeList);
        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }

    @PostMapping("/admin/companyInfo/materialList")
    public String partCodeMgmtApi(Criteria criteria, Model model) {
        return "partCodeMgmt/apiList";
    }


    @PostMapping("/admin/companyInfo/getMaterialList")
    @ResponseBody
    public List<HashMap> getMaterialList(HttpServletRequest req) {
        String code = req.getParameter("searchCodeMaterialCode");
        String name = req.getParameter("searchNameMaterialCode");
        return partCodeService.getMaterialList(code,name);
    }


    //업체정보 신규 저장
    @PostMapping("/admin/companyInfo/setPartCode")
    @ResponseBody
    public ResponseEntity<?> setPartCode(@RequestBody PartCodeDTO partCodeDTO ,Authentication authentication) {
        System.out.println("Received partCodeDTO: " + partCodeDTO);

        String loginId = authentication.getName();

        UserMgrDTO memberDTO = memberService.findByMemberId(loginId);
        partCodeDTO.setREG_DW_USER_IDX(memberDTO.getUSER_IDX());
        partCodeDTO.setUP_DW_USER_IDX(memberDTO.getUSER_IDX());

        //저장전 등록여부 검사

        PartCodeDTO paramPartCodeDTO = new PartCodeDTO();
        //Key 중복 검사.
        paramPartCodeDTO.setPART_CODE(partCodeDTO.getPART_CODE());
        paramPartCodeDTO.setPLANT_CODE(partCodeDTO.getPLANT_CODE());

        List<PartCodeDTO> partCodeList = partCodeService.partCodeList(paramPartCodeDTO);

        String savetype = partCodeDTO.getSavetype();
        // 중복 체크
        if (partCodeList != null && !partCodeList.isEmpty()) {
            if(savetype.equals("update")) {
                //update
                int resultCnt = partCodeService.setPartCode(partCodeDTO);

                if (resultCnt > 0) {
                    return ResponseEntity.status(HttpStatus.CREATED).body("수정 되었습니다.");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("수정 실패");
                }
            }else {
                //insert
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 등록된 코드가 있습니다.");
            }

        } else {
            //insert
            int resultCnt = partCodeService.setPartCode(partCodeDTO);

            if (resultCnt > 0) {
                return ResponseEntity.status(HttpStatus.CREATED).body("등록 성공");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 실패");
            }
        }
    }

    @PostMapping("/admin/companyInfo/deletePartCode")
    @ResponseBody
    public ResponseEntity<?> deletePartCodes(@RequestBody Map<String, List<Map<String, String>>> requestData) {
        List<Map<String, String>> selectedData = requestData.get("selectedData");

        for (Map<String, String> data : selectedData) {
            String partCode = data.get("partCode");
            String plantCode = data.get("plantCode");

            //삭제
            int result = partCodeService.deletePartCode(partCode, plantCode);

            if (result == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패! 관리자에게 문의하세요");
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("삭제 되었습니다.");

    }

}
