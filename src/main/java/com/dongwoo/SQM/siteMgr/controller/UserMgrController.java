package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrParamDTO;
import com.dongwoo.SQM.siteMgr.service.UserMgrService;
import com.dongwoo.SQM.system.dto.MemberDTO;
import com.dongwoo.SQM.system.dto.UserInfoCompanyUserDTO;
import com.dongwoo.SQM.system.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserMgrController {
    private final UserMgrService userMgrService;

    @GetMapping("/siteMgr")
    public String userMgr(Model model) {
        //기초코드 바인딩.
        //왜 두개냐 ? 상단 메뉴 링크.
        //경로가 왜 두개냐 ?
        return "userMgr/list";
    }

    @GetMapping("/userMgr")
    public String userMgrMain(Model model) {
        //기초코드 바인딩.
        //경로가 왜 두개냐 ? 좌측 사이드 메뉴 링크
        return "userMgr/list";
    }

    @PostMapping("/userMgr/save")
    public String save(UserMgrDTO userMgrDTO) throws IOException {
        System.out.println("userMgrDTO = " + userMgrDTO);
        userMgrService.save(userMgrDTO);
        return "redirect:/userMgr/list";
    }

    @GetMapping("/userMgr/list")
    public String findAll(Model model) {
        //List<UserMgrDTO> userMgrDTOList = userMgrService.findAll();
        //model.addAttribute("userMgrList", userMgrDTOList);
        //System.out.println("userMgrDTOList = " + userMgrDTOList);
        return "/userMgr/list";
    }

    @GetMapping("/userMgr/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        // 조회수 처리
        userMgrService.updateHits(id);
        // 상세내용 가져옴
        UserMgrDTO userMgrDTO = userMgrService.findById(id);
        model.addAttribute("userMgr", userMgrDTO);
        System.out.println("userMgrDTO = " + userMgrDTO);
//        if (userMgrDTO.getATTACHED_FILE().equals(1)) {
//            List<UserMgrFileDTO> userMgrFileDTOList = userMgrService.findFile(id);
//            model.addAttribute("userMgrFileList", userMgrFileDTOList);
//        }
        return "/userMgr/detail";
    }

    @GetMapping("/userMgr/update/{id}")
    public String update(@PathVariable("id") int id, Model model) {
        UserMgrDTO userMgrDTO = userMgrService.findById(id);
        model.addAttribute("userMgr", userMgrDTO);
        return "/userMgr/update";
    }

    @PostMapping("/userMgr/update/{id}")
    public String update(UserMgrDTO userMgrDTO, Model model) {
        userMgrService.update(userMgrDTO);
        UserMgrDTO dto = userMgrService.findById(userMgrDTO.getBOARD_IDX());
        model.addAttribute("userMgr", dto);
        return "/userMgr/detail";
    }

    @GetMapping("/userMgr/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        userMgrService.delete(id);
        return "redirect:/userMgr/list";
    }


    //동우 유저검색
    @PostMapping("/userMgr/getUserInfo")
    public ResponseEntity<?> getUserInfo(@RequestBody UserMgrParamDTO userMgrParamDTO ) {
        try {
            System.out.println("Received UserDto: " + userMgrParamDTO);

            List<UserMgrDTO> companyUserList = userMgrService.findUserMgrSearch(userMgrParamDTO);

            System.out.println("select companyUserList: " + companyUserList);
            return ResponseEntity.ok(companyUserList);
        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }


    //사용자 조회 ID
    @PostMapping("/userMgr/getUserInfoByID")
    public ResponseEntity<UserMgrDTO> getUserInfo(@RequestBody Map<String, String> params) {
        String userId = params.get("USER_ID");

        UserMgrDTO userDto = userMgrService.findUserMgrById(userId);

        if (userDto != null) {
            return ResponseEntity.ok(userDto); // 사용자 정보를 응답으로 반환
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 사용자 정보를 찾을 수 없을 때
        }
    }

    // 사용자 정보를 업데이트하는 메서드
    @PostMapping("/userMgr/updateUserMgr")
    public String updateUserMgr(@ModelAttribute UserMgrParamDTO userUpdateDto, Model model) {
        // 사용자 정보를 업데이트하는 로직
        // 예를 들어, DB에서 사용자 정보를 수정하는 서비스 메서드 호출

        // UserService는 사용자 정보를 수정하는 서비스입니다.
        // userService.updateUser(userUpdateDto);




        // 업데이트가 완료된 후의 로직
        model.addAttribute("message", "사용자 정보가 성공적으로 업데이트되었습니다.");
        return "redirect:/userMgr/list"; // 성공 시, 사용자 관리 페이지로 리다이렉트
    }


}
