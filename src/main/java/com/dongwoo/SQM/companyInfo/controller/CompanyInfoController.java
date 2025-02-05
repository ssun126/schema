package com.dongwoo.SQM.companyInfo.controller;

import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoParamDTO;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import com.dongwoo.SQM.companyInfo.service.CompanyInfoService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.system.dto.*;
import com.dongwoo.SQM.system.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CompanyInfoController {
    private final CompanyInfoService companyInfoService;
    private final MemberService memberService;

    //사용자 회사정보
    @GetMapping("/user/companyInfo/company")
    public String cpInfoMain(Model model, Authentication authentication) {

        String loginId = authentication.getName();
        //System.out.println("loginId????"+loginId);

        //로그인된 ID 회사 코드 알아오기. 여기는 업체 유저 전용
        MemberDTO loginMemberDTO  = memberService.findCpLoginID(loginId);
        MemberDTO memberDTO = memberService.basicvendorNumCheck("VendorNum",loginMemberDTO.getCOM_CODE());
        model.addAttribute("member", memberDTO);
        //System.out.println("memberDTO: "+memberDTO);

        //공동 작업자
        UserInfoCompanyUserDTO parmaDTO = new UserInfoCompanyUserDTO();
        parmaDTO.setUSER_ID(loginId);
        List<UserInfoCompanyUserDTO> companyUserList = memberService.findByMemberInfoAll(parmaDTO);
        model.addAttribute("companyUserList", companyUserList);
        //System.out.println("companyUserList: "+companyUserList);

        //부서
        List<BaseCodeDTO> deptList = companyInfoService.GetBaseCode("CpWorkCode");
        model.addAttribute("deptList", deptList);

        //업체코드 사업본부
        CompanyInfoParamDTO companyInfoParamDTO = new CompanyInfoParamDTO();
        companyInfoParamDTO.setCOM_CODE(loginMemberDTO.getCOM_CODE());
        List<CompanyInfoDTO> companyCodeWorkList = companyInfoService.findCompanyCodeWork(companyInfoParamDTO);
        model.addAttribute("companyCodeWorkList", companyCodeWorkList);

        //회원 ID 관리
        List<CompanyInfoDTO> companyUserIDList = companyInfoService.findCompanyCodeWorkEx(companyInfoParamDTO);
        model.addAttribute("companyUserIDList", companyUserIDList);
        //System.out.println("companyUserIDList: "+companyUserIDList);

        return "companyInfo/cpInfoMain";
    }

    //사용자 회사정보 수정
    @PostMapping("/user/companyInfo/updateCompanyInfo")
    @ResponseBody
    public String updateCompanyInfo(@ModelAttribute MemberDTO memberDTO, Authentication authentication ,Model model) {

        String loginId = authentication.getName();
        MemberDTO loginMemberDTO  = memberService.findCpLoginID(loginId);

        ComPanyCodeDTO comPanyCodeDTO = new ComPanyCodeDTO();
        comPanyCodeDTO.setCOM_CODE(memberDTO.getCOM_CODE());
        comPanyCodeDTO.setVENDOR_WORK_KIND(memberDTO.getVENDOR_WORK_KIND());  // VENDOR 업종 형태 (D:제조사, L:물류사)

        comPanyCodeDTO.setCOMPANY_NAME(memberDTO.getCOMPANY_NAME());
        comPanyCodeDTO.setFACTORY_NAME(memberDTO.getFACTORY_NAME());
        comPanyCodeDTO.setBUS_NUMBER(memberDTO.getBUS_NUMBER());
        comPanyCodeDTO.setCOM_ADDRESS(memberDTO.getCOM_ADDRESS());
        comPanyCodeDTO.setCOM_CEO_NAME(memberDTO.getCOM_CEO_NAME());
        comPanyCodeDTO.setCOM_CEO_PHONE(memberDTO.getCOM_CEO_PHONE());
        comPanyCodeDTO.setCOM_CEO_EMAIL(memberDTO.getCOM_CEO_EMAIL());
        comPanyCodeDTO.setUP_DW_USER_IDX(loginMemberDTO.getUSER_IDX());   //업데이트 사용자.

        //System.out.println("comPanyCodeDTO: "+comPanyCodeDTO);
        memberService.updateCpCodeCPUser(comPanyCodeDTO);  //업데이트  COMPANY_CODE

        model.addAttribute("message", "Company 정보가 업데이트되었습니다.");  //
        return "ok";
    }


    @PostMapping("/user/companyInfo/updateCompanyUserInfo")
    @ResponseBody
    public Map<String, String> updateCompanyInfoUser(@RequestBody List<UserInfoCompanyUserDTO> CompanyUserList) {
        Map<String, String> response = new HashMap<>();
        try {

            //공동 사용자 삭제 (휴지통 버튼 누른 유저들.)
            List<Integer> companyUserIdxList = new ArrayList<>();
            String comCode = null;
            int useridx = 0;

            for (UserInfoCompanyUserDTO companyUser : CompanyUserList) {
                if (companyUser.getCOM_USER_IDX() != 0) {
                    companyUserIdxList.add(companyUser.getCOM_USER_IDX());
                }

                if (comCode == null) {
                    comCode = companyUser.getCOM_CODE();
                }

                if (useridx == 0) {
                    useridx = companyUser.getUSER_IDX();
                }
            }

            if (comCode != null && !companyUserIdxList.isEmpty()) {
                memberService.deleteCompanyUser(comCode, useridx,companyUserIdxList);
            }

            //공동 작업자 저장.
            for (UserInfoCompanyUserDTO companyUser : CompanyUserList) {

               System.out.println("companyUser: "+companyUser);

                // MAIN_COM_USER_IDX 메인작업자 변경시 SC_USER_INFO 이름 업데이트
                if(Objects.equals(companyUser.getMAIN_USER_YN(), "Y")) {
                    UserInfoDTO userInfoDTO =  new UserInfoDTO();
                    userInfoDTO.setUSER_NAME(companyUser.getUSER_NAME());
                    userInfoDTO.setUSER_IDX(companyUser.getUSER_IDX());
                    memberService.updateUserName(userInfoDTO);
                }

               UserInfoCompanyUserDTO userInfoCompanyUserDTO = new UserInfoCompanyUserDTO();
                userInfoCompanyUserDTO.setCOM_USER_IDX(companyUser.getCOM_USER_IDX());
                userInfoCompanyUserDTO.setUSER_IDX(useridx);
                userInfoCompanyUserDTO.setCOM_CODE(companyUser.getCOM_CODE());
                userInfoCompanyUserDTO.setUSER_NAME(companyUser.getUSER_NAME());
                userInfoCompanyUserDTO.setUSER_POSITION(companyUser.getUSER_POSITION());
                userInfoCompanyUserDTO.setUSER_DEPT(companyUser.getUSER_DEPT());
                userInfoCompanyUserDTO.setUSER_EMAIL(companyUser.getUSER_EMAIL());
                userInfoCompanyUserDTO.setUSER_PHONE(companyUser.getUSER_PHONE());
               // 없으면 insert 있으면 update 여기서 키값은 COM_USER_IDX
               memberService.updateUserInfoCompany(userInfoCompanyUserDTO);
           }

            response.put("status", "success");
            response.put("message", "공동 업무자가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "저장 중 오류가 발생했습니다.");
        }
        return response;
    }

    @PostMapping("/user/companyInfo/deleteCompanyUserInfo")
    @ResponseBody
    public Map<String, String> deleteCompanyUserInfo(@RequestParam("USER_IDX") int USER_IDX , Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        try {
            String loginId = authentication.getName();
            MemberDTO loginMemberDTO  = memberService.findCpLoginID(loginId);
            //SC_USER_INFO ID 상태 변경
            UserInfoDTO userInfoDTO =  new UserInfoDTO();
            userInfoDTO.setUSER_IDX(USER_IDX);
            userInfoDTO.setUSER_STATUS("N"); //사용자 상태 (Y:사용, N:미사용)
            userInfoDTO.setDEL_DW_USER_IDX(loginMemberDTO.getUSER_IDX());
            memberService.updateUserStatus(userInfoDTO);

            //SC_USER_INFO_COMPANY ID 상태 변경
            UserInfoCompanyDTO userInfoCompanyDTO  = new UserInfoCompanyDTO();
            userInfoCompanyDTO.setUSER_IDX(USER_IDX);
            userInfoCompanyDTO.setRETURN_REASON("업체 사용자 삭제");
            userInfoCompanyDTO.setUSER_STATUS("0");  //관리상태 (0:대기,삭제 , 1:검토중, 2:승인, 3:반려)
            memberService.deleteUserInfoCompanyHis(userInfoCompanyDTO);

            /* 삭제 처리후 가비지 DATA 처리방법 확인 할것.
            select * from SC_USER_INFO_COMPANY_USER
            SELECT * FROM SC_USER_INFO_COMPANY_CONNECT_GOAL
            */

            response.put("status", "success");
            response.put("message", "삭제 처리 되었습니다.");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "처리 중 오류가 발생했습니다.");
        }
        return response;
    }
    
    //승인,반려처리
    @PostMapping("/user/companyInfo/approvalCompanyUser")
    @ResponseBody
    public Map<String, String> approvalCompanyUser(
             @RequestParam("USER_IDX") int USER_IDX
            ,@RequestParam("ApprovalType") String ApprovalType
            ,@RequestParam("RETURN_REASON") String ReturnReason
            ,@RequestParam("COME_CODE") String comeCode
            ,@RequestParam("ID_ADD_TYPE") String idAddType
            , Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        try {

            String loginId = authentication.getName();
            UserMgrDTO loginMemberDTO = memberService.findByMemberId(loginId);  //동우 로그인 사용자

            //SC_USER_INFO ID 상태 변경
            UserInfoDTO userInfoDTO =  new UserInfoDTO();
            userInfoDTO.setUSER_IDX(USER_IDX);
            if(ApprovalType.equals("Y")) {
                userInfoDTO.setUSER_STATUS("Y"); //사용자 상태 (Y:사용, N:미사용)
            }else{
                userInfoDTO.setUSER_STATUS("N");
            }
            userInfoDTO.setDEL_DW_USER_IDX(loginMemberDTO.getUSER_IDX());
            memberService.approvalUserStatus(userInfoDTO);  //승인,반려.

            //SC_USER_INFO_COMPANY ID 상태 변경
            UserInfoCompanyDTO userInfoCompanyDTO  = new UserInfoCompanyDTO();
            userInfoCompanyDTO.setUSER_IDX(USER_IDX);
            if(ApprovalType.equals("N")) {
                userInfoCompanyDTO.setRETURN_REASON(ReturnReason);
            }else{
                userInfoCompanyDTO.setRETURN_REASON("승인완료");
            }

            if(ApprovalType.equals("Y")) {
                userInfoCompanyDTO.setUSER_STATUS("2");  //관리상태 (0:대기,삭제 , 1:검토중, 2:승인, 3:반려)
            }else {
                userInfoCompanyDTO.setUSER_STATUS("3");
            }
            memberService.approvalUserInfoCompanyHis(userInfoCompanyDTO);

            ComPanyCodeDTO comPanyCodeDTO = new ComPanyCodeDTO();
            comPanyCodeDTO.setCOM_CODE(comeCode);
            if(ApprovalType.equals("Y")) {
                comPanyCodeDTO.setCOM_MANAGE_STATUS("2");  // Warranty 관리상태 (0:대기,삭제 , 1:검토중, 2:승인, 3:반려)
            }else {

                if(idAddType.equals("0")) {//신규
                    comPanyCodeDTO.setCOM_MANAGE_STATUS("3");
                }else{
                    comPanyCodeDTO.setCOM_MANAGE_STATUS("2");
                    //추가 아이디 신청 반려시 워런티는 변경 하지 않는다.!
                    //기본 승인이 안되면 추가 신청도 안된다.
                }

            }
            comPanyCodeDTO.setUP_DW_USER_IDX(loginMemberDTO.getUSER_IDX());
            memberService.approvalCpCode(comPanyCodeDTO);  //업데이트  COMPANY_CODE

            response.put("status", "success");
            if(ApprovalType.equals("Y")) {
                response.put("message", "승인 처리 되었습니다.");
            }else{
                response.put("message", "반려 처리 되었습니다.");
                //이메일 발송!!!
                //연락처 e-mail로 발송
                //반려 처리 후 30일 이내
                //재제출이 없을시 자동 삭제
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "처리 중 오류가 발생했습니다.");
        }
        return response;
    }

    @GetMapping("/admin/companyInfo/company")
    @PreAuthorize("hasRole('ADMIN')")
    public String isoAuthAdminMain(Model model) {
        return "companyInfo/main";
    }

    @PostMapping("/companyInfo/save")
    public String save(CpCodeDTO cpCodeDTO) throws IOException {
       // System.out.println("companyInfoDTO = " + companyInfoDTO);
       // companyInfoService.save(companyInfoDTO);
        return "redirect:/companyInfo/list";
    }

    @GetMapping("/companyInfo/list")
    public String findAll(Model model) {
       // List<CompanyInfoDTO> companyInfoDTOList = companyInfoService.findAll();
       // model.addAttribute("companyInfoList", companyInfoDTOList);
        //System.out.println("companyInfoDTOList = " + companyInfoDTOList);
        return "/companyInfo/list";
    }

    @PostMapping("/admin/companyInfo/company/save")
    public ResponseEntity<?> setCompanyData(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();
        log.info("Request Body: " + requestBody);  // 요청 본문 로그 확인

        // JSON 파싱하여 CompanyInfoDTO 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        CpCodeDTO cpCodeDTO = objectMapper.readValue(requestBody, CpCodeDTO.class);
        log.info("Parsed CompanyInfoDTO: " + cpCodeDTO);

        // 데이터 처리
        int resultCnt = companyInfoService.save(cpCodeDTO);

        if (resultCnt > 0) {
            return ResponseEntity.ok("Form submitted successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Form submission failed!");
        }
    }

    @GetMapping("/companyInfo/update/{id}")
    public String update(@PathVariable("id") int id, Model model) {
        //CompanyInfoDTO companyInfoDTO = companyInfoService.findById(id);
        //model.addAttribute("companyInfo", companyInfoDTO);
        return "/companyInfo/update";
    }

    @PostMapping("/companyInfo/update/{id}")
    public String update(CompanyInfoDTO companyInfoDTO, Model model) {
        //companyInfoService.update(companyInfoDTO);
       /* CompanyInfoDTO dto = companyInfoService.findById(companyInfoDTO.getBOARD_IDX());
        model.addAttribute("companyInfo", dto);*/
        return "/companyInfo/detail";
    }

    @GetMapping("/companyInfo/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        //companyInfoService.delete(id);
        return "redirect:/companyInfo/list";
    }
}
