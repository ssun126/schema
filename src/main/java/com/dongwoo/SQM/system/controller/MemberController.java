package com.dongwoo.SQM.system.controller;

import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;

import com.dongwoo.SQM.system.dto.*;
import com.dongwoo.SQM.system.service.MemberService;
import com.dongwoo.SQM.siteMgr.service.UserMgrService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final UserMgrService userMgrService;

    @GetMapping("/member/agree")
    public String agreeForm(Model model) {
        return "member/agree";
    }

    @GetMapping("/member/join")
    public String joinForm(Model model) {
        return "member/join";
    }

    @GetMapping("/member/warranty")
    public String warrantyForm(Model model) {
        return "member/warranty";
    }

    @GetMapping("/member/approve")
    public String approveForm(Model model) {
        return "member/approve";
    }

    @GetMapping("/member/find")
    public String findForm(Model model) {
        return "member/find";
    }

    @PostMapping("/member/save")
    public String save(@ModelAttribute MemberDTO memberDTO){
        memberService.save(memberDTO);

        return "/login";
    }

    @GetMapping("/member")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();
        // 어떠한 html로 가져갈 데이터가 있다면 model사용
        model.addAttribute("memberList", memberDTOList);
        return "/member/list";
    }

    @GetMapping("/member/{id}")
    public String findById(@PathVariable int id, Model model) {
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member", memberDTO);
        return "/member/detail";
    }

    //마이페이지 2024.11.24 sylee
    @GetMapping("/member/myPage")
    public String myPage(HttpSession session, Model model,Authentication authentication) {


        String loginId = authentication.getName();
        //System.out.println("loginId????"+loginId);

        UserMgrDTO memberDTO = memberService.findByMemberId(loginId);

        //업체 사용자 접속목적 코드
        List<UserMgrDTO> memberGoalList = memberService.findConnectGoalByUserId(loginId);

        model.addAttribute("member", memberDTO);
        model.addAttribute("memberGoalList", memberGoalList); // 리스트 추가

        return "/member/myPage";
    }

    @GetMapping("/member/update")
    public String updateForm(HttpSession session, Model model) {
        // 세션에 저장된 나의 이메일 가져오기
        String loginEmail = (String) session.getAttribute("loginEmail");
        System.out.println("loginEmail????"+loginEmail);
        MemberDTO memberDTO = memberService.findByMemberEmail(loginEmail);
        model.addAttribute("member", memberDTO);
        return "/member/update";
    }

    @PostMapping("/member/update")
    public String update(@ModelAttribute MemberDTO memberDTO) {
        System.out.println("memberDTO????"+memberDTO.getUSER_IDX());
        boolean result = memberService.update(memberDTO);
        if (result) {
            return "redirect:/member?id=" + memberDTO.getUSER_IDX();
        } else {
            return "index";
        }
    }

    @GetMapping("/member/delete/{id}")
    public String deleteById(@PathVariable int id) {
        memberService.delete(id);
        return "redirect:/member/";
    }

    @PostMapping("/member/email-check")
    public @ResponseBody String emailCheck(@RequestParam("memberEmail") String memberEmail) {
        System.out.println("memberEmail = " + memberEmail);
        String checkResult = memberService.emailCheck(memberEmail);
        return checkResult;
    }

    //회원 가입 정보 입력후 유효성 검사 완료 했을때  세션에 담고 다음버튼으로 warranty 페이지 이동
    @PostMapping("/member/warranty")
    public String JoinWarranty(@ModelAttribute MemberDTO memberDTO, HttpSession session , Model model) {
        session.setAttribute("joinData", memberDTO);  //

        //기존 가입 정보 조회  워런티 정보 조회해서 넣는다.
        MemberDTO comPanyDTO  = memberService.getCOMPANYCODE(memberDTO.getCOM_CODE());

        String dateStr="";
        String comDateString =comPanyDTO.getCOM_APP_DATE();
        if (comDateString != null) {
             dateStr = comDateString.substring(0, 10);
        }
        memberDTO.setCOM_APP_DATE(dateStr); //워런티 협약상태 제출일

        dateStr="";
        comDateString =comPanyDTO.getCOM_OK_DATE();
        if (comDateString != null) {
            dateStr = comDateString.substring(0, 10);
        }
        memberDTO.setCOM_OK_DATE(dateStr);  // 워런티 협약상태 승일일 또는 반려일
        //워런티
        String sComManageStatus = switch (comPanyDTO.getCOM_MANAGE_STATUS()) {
            case "1" -> "검토중";
            case "2" -> "승인";
            case "3" -> "반려";
            default -> "대기"; //"0"
        };
        memberDTO.setCOM_MANAGE_STATUS(sComManageStatus);  // 워런티~~!!! 관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)


        memberDTO.setCOM_FILE_NAME(comPanyDTO.getCOM_FILE_NAME());  // 워런티 파일 이름
        memberDTO.setCOM_FILE_PATH(comPanyDTO.getCOM_FILE_PATH());  // 워런티 파일 Path

        //ID 가입 신청 정보
        //등록중 건이 있는지 체크.
        //select * from USER_INFO_COMPANY where USER_STATUS NOT IN('0','2') and  COM_CODE=#{COM_CODE}
        MemberDTO user_Info_CompanyDTO  = memberService.vendorNumCheck(comPanyDTO.getCOM_CODE());

        if(user_Info_CompanyDTO != null) {
            memberDTO.setUSER_STATUS(user_Info_CompanyDTO.getUSER_STATUS());     //ID 가입 상태 (0:대기, 1:검토중, 2:승인, 3:반려)
            memberDTO.setRETURN_REASON(user_Info_CompanyDTO.getRETURN_REASON()); //ID 반려사유
        }else{
            memberDTO.setUSER_STATUS("0");
        }



        model.addAttribute("comPanyDTO", comPanyDTO);
        
        return "/member/warranty";
    }


    //파일 업로드 후 최종 제출
    @PostMapping("/member/resubmit")
    public String resubmitData(@ModelAttribute MemberDTO memberFileDTO,HttpSession session, Model model) {

        MemberDTO memberDTO = (MemberDTO) session.getAttribute("joinData");

        //세션 정보 삭제후 다시 들어온거 방지.!!
        if (session.getAttribute("joinData") == null) {
            model.addAttribute("resultMsg", "가입 정보가 없습니다.");
            return "/member/warranty";
        }

        String uploadDir = "C:\\upload\\";  // 반드시 이 경로가 존재해야 함
        memberDTO.setCOM_FILE_NAME(memberFileDTO.getCOM_FILE_NAME());
        memberDTO.setCOM_FILE_PATH(uploadDir);
        //실제 파일 저장 처리//

        // 데이터 처리
        String COM_CODE = memberDTO.getCOM_CODE();
        String USERID = memberDTO.getUSER_ID();
        String MainUSER_NAME = memberDTO.getUSERSELECT();  //메인작업자.

        //Step 1 .USER_INFO 만들기. (로그인 ID 생성)
        UserInfoDTO userinfoDTO = new UserInfoDTO();
        userinfoDTO.setUSER_ID(memberDTO.getUSER_ID());

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(memberDTO.getUSER_PWD());
        userinfoDTO.setUSER_PWD(encodedPassword);

        userinfoDTO.setUSER_NAME(MainUSER_NAME);
        userinfoDTO.setUSER_GUBN("1");  //사용자 구분 (0:동우화인켐, 1:업체)
        userinfoDTO.setUSER_STATUS("N"); // 사용자 상태 (Y:사용, N:미사용)
        //userinfoDTO.setREG_DW_USER_IDX("1");  //등록자 //로그인 사용자.

        memberService.saveUserInfo(userinfoDTO);  // 없으면 insert 있으면 update

        // 저장후 만들어진 USER_IDX 받아오자.
        UserInfoDTO userNewUserinfoDTO = memberService.findByUserId(USERID);  //
        int USER_IDX = userNewUserinfoDTO.getUSER_IDX();


        //Step 2 .USER_INFO_COMPANY_USER 만들기. //추가 사용자 생성
        //공동 작업자 영역.=============================
        String USER_NAME = memberDTO.getUSER_NAME();
        String USER_POSITION = memberDTO.getUSER_POSITION();
        String USER_DEPT = memberDTO.getUSER_DEPT();
        String USER_EMAIL = memberDTO.getUSER_EMAIL();
        String USER_PHONE = memberDTO.getUSER_PHONE();

        // 각 필드를 쉼표로 구분하여 배열로 변환
        String[] USER_NAMEArray = USER_NAME.split(",");
        String[] USER_POSITIONArray = USER_POSITION.split(",");
        String[] USER_DEPTArray = USER_DEPT.split(",");
        String[] USER_EMAILArray = USER_EMAIL.split(",");
        String[] USER_PHONEArray = USER_PHONE.split(",");

        for (int i = 0; i < USER_NAMEArray.length; i++) {

            //공동 작업자 저장.
            UserInfoCompanyUserDTO userInfoCompanyUserDTO = new UserInfoCompanyUserDTO();

            userInfoCompanyUserDTO.setUSER_IDX(USER_IDX); //위에서 만들어진 사용자 IDX
            userInfoCompanyUserDTO.setCOM_CODE(COM_CODE);
            userInfoCompanyUserDTO.setUSER_NAME(USER_NAMEArray[i]);
            userInfoCompanyUserDTO.setUSER_POSITION(USER_POSITIONArray[i]);
            userInfoCompanyUserDTO.setUSER_DEPT(USER_DEPTArray[i]);
            userInfoCompanyUserDTO.setUSER_EMAIL(USER_EMAILArray[i]);
            userInfoCompanyUserDTO.setUSER_PHONE(USER_PHONEArray[i]);

            // 없으면 insert 있으면 update 여기서 키값은   COM_CODE ,USER_NAME
            memberService.saveUserInfoCompany(userInfoCompanyUserDTO);
        }

        //공동 작업자 생성후 COM_USER_IDX 가져오기. where.   밴더 코드 : COM_CODE  AND  이름 USER_NAME :MainUSER_NAME
        UserInfoCompanyUserDTO parmaDTO = new UserInfoCompanyUserDTO();
        parmaDTO.setCOM_CODE(COM_CODE); //위에서 만들어진 사용자 IDX
        parmaDTO.setUSER_NAME(MainUSER_NAME);

        UserInfoCompanyUserDTO userInfoCompanyUserDTO = memberService.findByCompanyUserName(parmaDTO);
        int COM_USER_IDX = userInfoCompanyUserDTO.getCOM_USER_IDX();

        //Step 3 .USER_INFO_COMPANY 만들기. //신청상태 HIS 생성

        UserInfoCompanyDTO userInfoCompanyDTO  = new UserInfoCompanyDTO();
        userInfoCompanyDTO.setUSER_IDX(USER_IDX);
        userInfoCompanyDTO.setCOM_CODE(COM_CODE);
        userInfoCompanyDTO.setCOM_USER_IDX(COM_USER_IDX);   //★ 메인 업무자.... 공동 작업자
        userInfoCompanyDTO.setID_PW_ADD_REASON(memberDTO.getID_PW_ADD_REASON());           //ID/PW 추가 사유
        userInfoCompanyDTO.setID_ADD_TYPE(memberDTO.getID_ADD_TYPE());           //ID 추가 정보 (0:신규, 1:추가)
        userInfoCompanyDTO.setUSER_STATUS("1");  //관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)

        memberService.saveUserInfoCompanyHis(userInfoCompanyDTO);

        //Step 4 사용자 추가정보 (업체) 저장
        //checkboxDataJson
        String checkboxesJson =  memberDTO.getCheckboxDataJson();

        //해당 유저 접속 목적 전체 삭제.  delete from USER_INFO_COMPANY_CONNECT_GOAL WHERE USER_IDX =#{USER_IDX}
        UserMgrDTO userMgrDTO = new UserMgrDTO();
        userMgrDTO.setUSER_IDX(USER_IDX);
        userMgrService.deleteConnectGoal(userMgrDTO);

        // 체크박스 데이터 JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> selectedGoals = objectMapper.readValue(checkboxesJson, List.class);

            for (Map<String, Object> goal : selectedGoals) {
                String base_code = (String) goal.get("id");
                Boolean checked = (Boolean) goal.get("checked");
                System.out.println("체크박스들: " + "ID: " + base_code + ", Checked: " + checked);

                if(checked) {
                    userMgrDTO.setBASE_CODE(base_code);
                    userMgrService.insertConnectGoal(userMgrDTO);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }


        ////Step 5 Final  COMPANY_CODE 업데이트

        //select * from USER_INFO_COMPANY where USER_STATUS NOT IN('0','2') and  COM_CODE=#{COM_CODE}
        //기존 상태확인
        MemberDTO comPanyDTO  = memberService.getCOMPANYCODE(memberDTO.getCOM_CODE());
        //워런티 승인일
        String comManageStatus = comPanyDTO.getCOM_MANAGE_STATUS();

        ComPanyCodeDTO comPanyCodeDTO = new ComPanyCodeDTO();
        comPanyCodeDTO.setCOM_CODE(COM_CODE);
        comPanyCodeDTO.setVENDOR_WORK_KIND(memberDTO.getVENDOR_WORK_KIND());  // VENDOR 업종 형태 (D:제조사, L:물류사)
        comPanyCodeDTO.setCOM_USER_IDX(COM_USER_IDX);   // 메인 업무자(이거 안씀.!!)

        comPanyCodeDTO.setCOMPANY_NAME(memberDTO.getCOMPANY_NAME());
        comPanyCodeDTO.setFACTORY_NAME(memberDTO.getFACTORY_NAME());

        comPanyCodeDTO.setBUS_NUMBER(memberDTO.getBUS_NUMBER());
        comPanyCodeDTO.setCOM_ADDRESS(memberDTO.getCOM_ADDRESS());
        comPanyCodeDTO.setCOM_CEO_NAME(memberDTO.getCOM_CEO_NAME());
        comPanyCodeDTO.setCOM_CEO_PHONE(memberDTO.getCOM_CEO_PHONE());
        comPanyCodeDTO.setCOM_CEO_EMAIL(memberDTO.getCOM_CEO_EMAIL());

        //워런티 s
        comPanyCodeDTO.setCOM_FILE_NAME(memberDTO.getCOM_FILE_NAME());   //워런티 파일 이름
        comPanyCodeDTO.setCOM_FILE_PATH(memberDTO.getCOM_FILE_PATH());   //워런티 파일 Path
        comPanyCodeDTO.setUP_DW_USER_IDX(USER_IDX);   //업데이트 사용자.
        comPanyCodeDTO.setCOM_MANAGE_STATUS("1"); //관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)
        //워런티 e

        String resultMsg = "";
        if(comManageStatus.equals("2")) {
            //ID 추가 상태는 워런티! 건들면 안됨!
            memberService.updateCpCodeCPUser(comPanyCodeDTO);
        }else {
            memberService.updateCompanyCode(comPanyCodeDTO);
        }

//        동우 RC 관리자에게 메일 발송
//        <내용 예시>
//        1) 업체코드 : 20000123
//        2) 업체명 : AAABBBCCC
//        회원가입 신청이 발생 하였습니다.

        // 세션에서 데이터 삭제
        session.removeAttribute("joinData");
        System.out.println("joinData 속성.세션 삭제");

        System.out.println("comManageStatus: " + comManageStatus);

        if(comManageStatus.equals("2")) { //워런티 승인 상태면
            resultMsg ="ID추가 요청 되었습니다.";
        }else if(comManageStatus.equals("0")){
            resultMsg = "승인 요청 되었습니다.";
        }else{
            resultMsg = "수정 요청 되었습니다.";
        }


        model.addAttribute("resultMsg", resultMsg);

        return "/member/warranty";

    }


    @PostMapping("/member/warrantyfileupload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("findfile") MultipartFile file) {
        // 파일을 저장할 경로 (C 드라이브의 upload 폴더)
        String uploadDir = "C:\\upload\\";  // 반드시 이 경로가 존재해야 함 ,밴터 코드 별로 관리가 되려면. 코드 도 받아야 될듯.

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 비어 있습니다.");
        }

        try {
            // 파일 저장
            File destinationFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(destinationFile);
            return ResponseEntity.ok("파일 업로드 성공: " + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 파일 다운로드 처리
    @GetMapping("/member/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) {
        String filePath = "C:\\upload\\" + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();

        try {
            // 파일 이름을 UTF-8로 인코딩
            String encodedFileName = URLEncoder.encode(file.getName(), "UTF-8");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }


    //ID 찾기
    @PostMapping("/member/id-check")
    public @ResponseBody String idCheck(@RequestParam("memberID") String memberID) {
        System.out.println("memberid = " + memberID);
        String checkResult = memberService.idCheck(memberID);
        return checkResult;
    }


    @PostMapping("/member/vendor-check")
    public @ResponseBody Map<String, Object> vendorCheck(@RequestParam("searchCode") String searchCode ,@RequestParam("searchType") String searchType) {
        log.info("vendorCode = " + searchCode);

        //마스터등록 여부 확인  COMPANY_CODE
        MemberDTO memberDTO = memberService.basicvendorNumCheck(searchType,searchCode);
        String USER_STATUS = ""; //관리상태 (0:대기, 1:검토중, 2:승인, 3:반려) ★
        String checkResult = "N";
        String User_ID = "";

        Map<String, Object> response = new HashMap<>();


        if(memberDTO != null) {

            //등록중 건이 있는지 체크.
            //select * from USER_INFO_COMPANY where USER_STATUS NOT IN('0','2') and  COM_CODE=#{COM_CODE}
            MemberDTO comPanyDTO  = memberService.vendorNumCheck(searchCode);

            if(comPanyDTO == null ){

                //★ 기가입 여부만 체크.
                //select * from USER_INFO_COMPANY where USER_STATUS = '2' and  COM_CODE=#{COM_CODE}
                List<MemberDTO> approveComPanyListDTO  = memberService.findApproveCompany(searchCode);

                int approveCount = approveComPanyListDTO.size();
                System.out.println("승인된 USER_STATUS= 2 갯수: " + approveCount);

                if(approveCount > 0) {
                    checkResult = "ok";  //기가입 업체.
                }else {
                    checkResult = "no"; //신규.
                }

            }else {
                USER_STATUS = comPanyDTO.getUSER_STATUS();  //(0:대기, 1:검토중, 2:승인, 3:반려)

                //USER_INFO 를 검색하자.   USER_INFO_COMPANY.USER_IDX
                UserInfoDTO userUserinfoDTO = memberService.findByUserIdx(comPanyDTO.getUSER_IDX());

                User_ID = userUserinfoDTO.getUSER_ID();

                int com_user_idx = comPanyDTO.getCOM_USER_IDX();  //메인작업자.
                String com_user_Name = ""; //메인작업자 명

                //공동 작업자 가져오기.  USER_INFO_COMPANY  where.   밴더 코드 : COM_CODE ,사용자 : USER_IDX
                UserInfoCompanyUserDTO parmaDTO = new UserInfoCompanyUserDTO();
                parmaDTO.setCOM_CODE(comPanyDTO.getCOM_CODE()); //위에서 만들어진 밴더 코드
                parmaDTO.setUSER_IDX(comPanyDTO.getUSER_IDX()); //위에서 만들어진 사용자 IDX
                List<UserInfoCompanyUserDTO> companyUserList = memberService.findByCompanyUserComCode(parmaDTO);
                response.put("companyUserList", companyUserList);

                //추출
                for (UserInfoCompanyUserDTO user : companyUserList) {
                    if (user.getCOM_USER_IDX() == com_user_idx) {
                        com_user_Name = user.getUSER_NAME();
                    }
                }

                //ID /PASS 업체 정보 바인딩 항목
                response.put("USER_ID", userUserinfoDTO.getUSER_ID());
                response.put("USER_NAME", com_user_Name);    //메인 담당자 select 컨트럴 바인딩.
                response.put("USER_PWD", ""); //수정시 재설정하자.
                response.put("ID_PW_ADD_REASON", comPanyDTO.getID_PW_ADD_REASON());
                response.put("ID_ADD_TYPE", comPanyDTO.getID_ADD_TYPE());
                

                //메세지 리턴.
                checkResult = switch (USER_STATUS) {
                    case "0" -> "대기";
                    case "1" -> "검토중";
                    case "3" -> "반려";
                    default -> throw new RuntimeException("관리자 문의.");
                };
            }

        }else {
            checkResult = "not" ; //업체코드 내역이 없어 가입이 불가합니다.
        }

        response.put("status", checkResult);
        if(memberDTO != null ) { //마스터 등록이 있으면.

            //업체 정보 바인딩 항목
            response.put("COM_CODE", memberDTO.getCOM_CODE()); //Vendor
            response.put("COM_NAME", memberDTO.getCOM_NAME()); //귀사정보
            response.put("VENDOR_WORK_KIND", memberDTO.getVENDOR_WORK_KIND()); //업종형태 VENDOR 업종 형태 (D:제조사, L:물류사)
            response.put("COM_NATION", memberDTO.getCOM_NATION()); //국가

            response.put("COMPANY_NAME", memberDTO.getCOMPANY_NAME());   //회사명
            response.put("FACTORY_NAME", memberDTO.getFACTORY_NAME());  //제조 공장명
            response.put("BUS_NUMBER", memberDTO.getBUS_NUMBER());  //사업자등록번호
            response.put("COM_ADDRESS", memberDTO.getCOM_ADDRESS());  //회사주소
            response.put("COM_CEO_NAME", memberDTO.getCOM_CEO_NAME());  //CEO 성명 (영문)
            response.put("COM_CEO_PHONE", memberDTO.getCOM_CEO_PHONE());  //CEO 연락처 (영문)
            response.put("COM_CEO_EMAIL", memberDTO.getCOM_CEO_EMAIL());  //CEO e-mail (영문)
        }

        //업체 사용자 접속목적 코드
        List<UserMgrDTO> memberGoalList = memberService.findConnectGoalByUserId(User_ID);
        response.put("memberGoalList", memberGoalList);

        return response;
    }

}
