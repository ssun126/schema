package com.dongwoo.SQM.qualityCtrl.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.qualityCtrl.dto.coaMgmtDTO;
import com.dongwoo.SQM.qualityCtrl.service.coaMgmtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class coaMgmtController {

    private final coaMgmtService coaMgmtService;


    @GetMapping("/admin/qualityCtrl/coaMgmt")
    public String coaList(Model model) {
        //등록일  // 기본값 오늘날짜. sStartDate

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sStartDate = today.format(formatter);

        model.addAttribute("sStartDate", sStartDate);
        return "coaMgmt/coaList";
    }


    //COA LIST 검색
    @PostMapping("/admin/qualityCtrl/getCOAList")
    public ResponseEntity<?> getcoaList(@RequestBody coaMgmtDTO coaMgmtdto) {
        try {
            System.out.println("Received scoreMgmtDTO: " + coaMgmtdto);

//            reqParam.put("TOKEN_USER_ID", tokenInfo.get("USER_ID"));
//            reqParam.put("TOKEN_USER_TYPE", tokenInfo.get("USER_TYPE"));
//            reqParam.put("TOKEN_USER_LANG", tokenInfo.get("USER_LANG"));
//            reqParam.put("TOKEN_SITE_ID", tokenInfo.get("SITE_ID"));

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_gubun = user.getUSER_GUBUN();
            String user_id = user.getUsername();


            coaMgmtdto.setTOKEN_USER_TYPE("AU");   //user_gubun :  관리자 , 사용자 권한 구분 확인
            coaMgmtdto.setTOKEN_USER_LANG("KR");   //언어 설정 확인.
            coaMgmtdto.setTOKEN_USER_ID("tykkim@lg.com"); //user_id 유저 매핑 해야됨.   test ===  >   tester  ,tykkim@lg.com
            List<coaMgmtDTO> coaList = coaMgmtService.getCOAList(coaMgmtdto);

            //System.out.println("select companyUserList: " + scoreList);
            return ResponseEntity.ok(coaList);

        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }













    @GetMapping("/user/qualityCtrl/scoreMgmt")
    public String getMain(Model model) {
        return "scoreMgmt/main";
    }
}
