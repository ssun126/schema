package com.dongwoo.SQM.system.controller;

import com.dongwoo.SQM.system.dto.*;
import com.dongwoo.SQM.system.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.harmony.pack200.NewAttributeBands;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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

    @GetMapping("/member/myPage")
    public String myPage(HttpSession session, Model model) {
        // 세션에 저장된 나의 이메일 가져오기
        String loginId = (String) session.getAttribute("loginId");
        System.out.println("loginId????"+loginId);
        MemberDTO memberDTO = memberService.findByMemberId(loginId);
        model.addAttribute("member", memberDTO);
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
        System.out.println("memberDTO????"+memberDTO.getUSER_idx());
        boolean result = memberService.update(memberDTO);
        if (result) {
            return "redirect:/member?id=" + memberDTO.getUSER_idx();
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

//기가입 정보 조회

        MemberDTO comPanyDTO  = memberService.getCOMPANYCODE(memberDTO.getCOMCODE());


        memberDTO.setCOMAPPDATE(comPanyDTO.getCOMAPPDATE()); //워런티 협약상태 제출일
        memberDTO.setCOMOKDATE(comPanyDTO.getCOMOKDATE());  // 워런티 협약상태 승일일 또는 반려일
        memberDTO.setCOMSTATUS(comPanyDTO.getCOMSTATUS());  // 관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)

        memberDTO.setCOMFILENAME(comPanyDTO.getCOMFILENAME());  // 워런티 파일 이름
        memberDTO.setCOMFILEPATH(comPanyDTO.getCOMFILEPATH());  // 워런티 파일 Path

        model.addAttribute("comPanyDTO", comPanyDTO);

//
        //return "redirect:/member/warranty"; // warranty 페이지로 리다이렉트
        //제출일 ,승인일 ,관리상태  , 파일  ,  반려사유 리턴해서 보여줘야됨.

        return "/member/warranty";
    }


    //파일 업로드 후 최종 제출
    @PostMapping("/member/resubmit")
    public String resubmitData(@ModelAttribute MemberDTO memberFileDTO , HttpSession session,Model model) {

        MemberDTO memberDTO = (MemberDTO) session.getAttribute("joinData");

        //세션 정보 삭제후 다시 들어온거..!!
        if (session.getAttribute("joinData") != null) {
            System.out.println("joinData 속성이 세션에 존재합니다.");
        } else {
            System.out.println("joinData 속성이 세션에 없습니다.");
            return "/login";
        }

        memberDTO.setCOMFILENAME(memberFileDTO.getCOMFILENAME());
        memberDTO.setCOMFILENAME("파일경로~");
        //실제 파일 저장 처리//

//TEST 2024.10.28 작업중.================================================================
        session.removeAttribute("joinData");
        model.addAttribute("승인 요청 되었습니다.", "저장완료");
        //return "/member/warranty";

        //return "redirect:/member/warranty";
        return "member/warranty";

//TEST //TEST 2024.10.28 작업중.=========================================================


/*
        // 데이터 처리
        String COMCODE = memberDTO.getCOMCODE();
        String USERID = memberDTO.getUSERID();
        String MainUserName = memberDTO.getUSERSELECT();  //메인작업자.

        //Step 1 .USERINFO 만들기.  ID 생성
        UserInfoDTO userinfoDTO = new UserInfoDTO();
        userinfoDTO.setUSERID(memberDTO.getUSERID());
        userinfoDTO.setUSERPWD(memberDTO.getUSERPWD());
        userinfoDTO.setUSERNAME(MainUserName);
        userinfoDTO.setUSERGUBN("1");  //사용자 구분 (0:동우화인켐, 1:업체)
        userinfoDTO.setUSERSTATUS("N"); // 사용자 상태 (Y:사용, N:미사용)
        userinfoDTO.setREGDWUSERIDX("1");  //등록자 //로그인 사용자.

        memberService.saveUserInfo(userinfoDTO);

        // 저장후 만들어진 USERIDX 받아오자.
        UserInfoDTO userNewUserinfoDTO = memberService.findByUserId(USERID);
        int USERIDX = userNewUserinfoDTO.getUSERIDX();


        //Step 2 .USERINFOCOMPANYUSER 만들기. //추가 사용자 생성
        //공동 작업자 영역.=============================
        String USERNAME = memberDTO.getUSERNAME();
        String USERPOSITION = memberDTO.getUSERPOSITION();
        String USERDEPT = memberDTO.getUSERDEPT();
        String USEREMAIL = memberDTO.getUSEREMAIL();
        String USERPHONE = memberDTO.getUSERPHONE();

        // 각 필드를 쉼표로 구분하여 배열로 변환
        String[] usernameArray = USERNAME.split(",");
        String[] userpositionArray = USERPOSITION.split(",");
        String[] userdeptArray = USERDEPT.split(",");
        String[] useremailArray = USEREMAIL.split(",");
        String[] userphoneArray = USERPHONE.split(",");

        for (int i = 0; i < usernameArray.length; i++) {

            //공동 작업자 저장.
            UserInfoCompanyUserDTO userInfoCompanyUserDTO = new UserInfoCompanyUserDTO();

            userInfoCompanyUserDTO.setUSERIDX(USERIDX); //위에서 만들어진 사용자 IDX
            userInfoCompanyUserDTO.setCOMCODE(COMCODE);
            userInfoCompanyUserDTO.setUSERNAME(usernameArray[i]);
            userInfoCompanyUserDTO.setUSERPOSITION(userpositionArray[i]);
            userInfoCompanyUserDTO.setUSERDEPT( userdeptArray[i]);
            userInfoCompanyUserDTO.setUSEREMAIL(useremailArray[i]);
            userInfoCompanyUserDTO.setUSERPHONE(userphoneArray[i]);

            memberService.saveUserInfoCompany(userInfoCompanyUserDTO);
        }

        //공동 작업자 생성후 COMUSERIDX 가져오기. where.   밴더 코드 : COMCODE  AND  이름 USERNAME :MainUserName
        UserInfoCompanyUserDTO parmaDTO = new UserInfoCompanyUserDTO();
        parmaDTO.setCOMCODE(COMCODE); //위에서 만들어진 사용자 IDX
        parmaDTO.setUSERNAME(MainUserName);

        UserInfoCompanyUserDTO userInfoCompanyUserDTO = memberService.findByCompanyUserName(parmaDTO);
        int COMUSERIDX = userInfoCompanyUserDTO.getCOMUSERIDX();

        //Step 3 .USERINFOCOMPANY 만들기. //신청상태 HIS 생성

        UserInfoCompanyDTO userInfoCompanyDTO  = new UserInfoCompanyDTO();
        userInfoCompanyDTO.setUSERIDX(USERIDX);
        userInfoCompanyDTO.setCOMCODE(COMCODE);
        userInfoCompanyDTO.setCOMUSERIDX(COMUSERIDX);   //메인 업무자.... 공동 작업자
        userInfoCompanyDTO.setIDPWADDREASON(memberDTO.getIDPWADDREASON());
        userInfoCompanyDTO.setUSERSTATUS("0");  //관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)

        memberService.saveUserInfoCompanyHis(userInfoCompanyDTO);

        //Step 4 사용자 추가정보 (업체) 저장후  -> (업체/접속목적) 저장. USERINFOCOMPANYCONNECTGOAL  //키가 잘못된것 같은대 ?
//        USERIDX				NUMBER(4)		NOT NULL		--// 사용자 IDX   ▶
//        , GOALIDX			NUMBER(4)		NOT NULL		--// 접속목적 IDX  ▶  기초코드 ?
//        , BASECODE			VARCHAR2(50)	NOT NULL		--// 코드


        ////Step 5 Final  COMPANYCODE 업데이트
        ComPanyCodeDTO comPanyCodeDTO = new ComPanyCodeDTO();
        comPanyCodeDTO.setCOMCODE(COMCODE);
        comPanyCodeDTO.setVENDORWORKKIND(memberDTO.getVENDORWORKKIND());  // VENDOR 업종 형태 (D:제조사, L:물류사)
        comPanyCodeDTO.setCOMUSERIDX(COMUSERIDX);   // 메인 업무자

        comPanyCodeDTO.setCOMPANYNAME(memberDTO.getCOMPANYNAME());
        comPanyCodeDTO.setFACTORYNAME(memberDTO.getFACTORYNAME());

        comPanyCodeDTO.setBUSNUMBER(memberDTO.getBUSNUMBER());
        comPanyCodeDTO.setCOMADDRESS(memberDTO.getCOMADDRESS());
        comPanyCodeDTO.setCOMCEONAME(memberDTO.getCOMCEONAME());
        comPanyCodeDTO.setCOMCEOPHONE(memberDTO.getCOMCEOPHONE());
        comPanyCodeDTO.setCOMCEOEMAIL(memberDTO.getCOMCEOEMAIL());

        comPanyCodeDTO.setCOMFILENAME(memberDTO.getCOMFILENAME());   //워런티 파일 이름
        comPanyCodeDTO.setCOMFILEPATH(memberDTO.getCOMFILEPATH());   //워런티 파일 Path
        comPanyCodeDTO.setUPDWUSERIDX(USERIDX);   //업데이트 사용자.

        memberService.updateCompanyCode(comPanyCodeDTO);
        //업데이트

        // 세션에서 데이터 삭제
        session.removeAttribute("joinData");
        System.out.println("joinData 속성.세션 삭제");
        
        //redirectAttributes.addFlashAttribute("message", "승인 요청 되었습니다.");
        model.addAttribute("승인 요청 되었습니다.", "저장완료");
        //return "/member/warranty";

        //return "redirect:/member/warranty";
        return "/login";



 */
    }


    //ID 찾기
    @PostMapping("/member/id-check")
    public @ResponseBody String idCheck(@RequestParam("memberID") String memberID) {
        System.out.println("memberid = " + memberID);
        String checkResult = memberService.idCheck(memberID);
        return checkResult;
    }


    @PostMapping("/member/vendor-check")
    public @ResponseBody Map<String, Object> vendorCheck(@RequestParam("searchCode") String searchCode ,String searchType) {
        System.out.println("vendorCode = " + searchCode);

        //마스터등록 여부 확인
        MemberDTO memberDTO = memberService.basicvendorNumCheck(searchType,searchCode);
        String USERSTATUS = ""; //관리상태 (0:대기, 1:검토중, 2:승인, 3:반려) ★
        String checkResult = "N";


        Map<String, Object> response = new HashMap<>();


        if(memberDTO != null) {

            //등록중인건이 있는지 체크. USER_INFO_COMPANY
            MemberDTO comPanyDTO  = memberService.vendorNumCheck(searchCode);

            if(comPanyDTO == null ){
                checkResult = "no"; //신규.
            }else {
                USERSTATUS = comPanyDTO.getUSERSTATUS();

                //USERINFO 를 검색하자.   USER_INFO_COMPANY.USERIDX
                UserInfoDTO userUserinfoDTO = memberService.findByUserIdx(comPanyDTO.getUSERIDX());

                //ID /PASS 업체 정보 바인딩 항목
                response.put("USERID", userUserinfoDTO.getUSERID());
                response.put("USERNAME", userUserinfoDTO.getUSERNAME());
                response.put("USERPWD", userUserinfoDTO.getUSERPWD());
                response.put("IDPWADDREASON", comPanyDTO.getIDPWADDREASON());


                //공동 작업자 가져오기.   where.   밴더 코드 : COMCODE
                UserInfoCompanyUserDTO parmaDTO = new UserInfoCompanyUserDTO();
                parmaDTO.setCOMCODE(comPanyDTO.getCOMCODE()); //위에서 만들어진 사용자 IDX
                List<UserInfoCompanyUserDTO> companyUserList = memberService.findByCompanyUserAll(parmaDTO);
                response.put("companyUserList", companyUserList);

                if(USERSTATUS.equals("2")) {
                    checkResult = "ok";  //기가입 업체.
                }else{
                    //메세지 리턴.
                    switch(USERSTATUS) {
                        case "0":
                            checkResult = "대기";
                            break;
                        case "1":
                            checkResult = "검토중";
                            break;
                        case "3":
                            checkResult = "반려";
                            break;
                        default:
                            throw new RuntimeException("관리자 문의.");
                    }
                }
            }

        }else {
            checkResult = "not" ;
        }

        response.put("status", checkResult);
        if(memberDTO != null ) { //마스터 등록이 있으면.

            //업체 정보 바인딩 항목
            response.put("COMCODE", memberDTO.getCOMCODE()); //Vendor
            response.put("COMNAME", memberDTO.getCOMNAME()); //귀사정보
            response.put("VENDORWORKKIND", memberDTO.getVENDORWORKKIND()); //업종형태
            response.put("COMNATION", memberDTO.getCOMNATION()); //국가

            response.put("COMPANYNAME", memberDTO.getCOMPANYNAME());   //회사명
            response.put("FACTORYNAME", memberDTO.getFACTORYNAME());  //제조 공장명
            response.put("BUSNUMBER", memberDTO.getBUSNUMBER());  //사업자등록번호
            response.put("COMADDRESS", memberDTO.getCOMADDRESS());  //회사주소
            response.put("COMCEONAME", memberDTO.getCOMCEONAME());  //CEO 성명 (영문)
            response.put("COMCEOPHONE", memberDTO.getCOMCEOPHONE());  //CEO 연락처 (영문)
            response.put("COMCEOEMAIL", memberDTO.getCOMCEOEMAIL());  //CEO e-mail (영문)
        }
        //response.put("VENDORWORKKIND", memberDTO.getVENDORWORKKIND());  //접속목적 코드화 받아서 동적 생성해야됨

        return response;
    }

}
