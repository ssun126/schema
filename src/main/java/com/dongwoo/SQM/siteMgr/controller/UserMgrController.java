package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrParamDTO;
import com.dongwoo.SQM.siteMgr.service.UserMgrService;
import com.dongwoo.SQM.system.controller.LoginController;
import com.dongwoo.SQM.system.dto.LoginDTO;
import com.dongwoo.SQM.system.dto.MemberDTO;
import com.dongwoo.SQM.system.dto.UserInfoCompanyUserDTO;
import com.dongwoo.SQM.system.dto.UserInfoDTO;
import com.dongwoo.SQM.system.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserMgrController {
    private final UserMgrService userMgrService;

    @GetMapping("/admin/siteMgr/dwUserList")
    public String userMgr(Model model) {
        List<BaseCodeDTO> deptList = userMgrService.GetBaseCode("DEPT");
        model.addAttribute("deptList", deptList);
        System.out.println("userMgrDTOList = " + deptList);

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


    //동우 유저검색 LIST
    @PostMapping("/userMgr/getUserInfo")
    public ResponseEntity<?> getUserInfo(@RequestBody UserMgrParamDTO userMgrParamDTO ) {
        try {
            System.out.println("Received UserDto: " + userMgrParamDTO);

            List<UserMgrDTO> companyUserList = userMgrService.findUserMgrSearch(userMgrParamDTO);

            //System.out.println("select companyUserList: " + companyUserList);
            return ResponseEntity.ok(companyUserList);
        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }

    //사용자 조회 ID POP
    @PostMapping("/userMgr/getUserInfoByID")
    public ResponseEntity<UserMgrDTO> getUserInfo(@RequestBody Map<String, String> params) {
        String userId = params.get("USER_ID");

        UserMgrDTO userDto = userMgrService.findUserMgrById(userId);

        if (userDto != null) {
            return ResponseEntity.ok(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 사용자 정보를 찾을 수 없을 때
        }
    }

    public static String checkValue(String value) {
        if ("on".equals(value)) {
            return "Y";
        } else {
            return "N";
        }
    }



    @PostMapping("/userMgr/updateUserMgr")
    @ResponseBody
    public String updateUserMgr(@ModelAttribute UserMgrDTO userMgrDTO ,Model model) {

        //체크 박스처리 길이 길다.
        //region
        userMgrDTO.setMANAGE_SYSTEM_YN(checkValue(userMgrDTO.getMANAGE_SYSTEM_YN())) ; //이거 용도가 화면에 안보임.

        userMgrDTO.setMANAGE_ISO_YN_01(checkValue(userMgrDTO.getMANAGE_ISO_YN_01())) ;
        userMgrDTO.setMANAGE_ISO_YN_02(checkValue(userMgrDTO.getMANAGE_ISO_YN_02())) ;
        userMgrDTO.setMANAGE_ISO_YN_03(checkValue(userMgrDTO.getMANAGE_ISO_YN_03())) ;
        userMgrDTO.setMANAGE_ISO_YN_04(checkValue(userMgrDTO.getMANAGE_ISO_YN_04())) ;
        userMgrDTO.setMANAGE_ISO_YN_05(checkValue(userMgrDTO.getMANAGE_ISO_YN_05())) ;
        userMgrDTO.setMANAGE_ISO_YN_06(checkValue(userMgrDTO.getMANAGE_ISO_YN_06())) ;
        userMgrDTO.setMANAGE_ISO_YN_07(checkValue(userMgrDTO.getMANAGE_ISO_YN_07())) ;
        userMgrDTO.setMANAGE_ISO_YN_08(checkValue(userMgrDTO.getMANAGE_ISO_YN_08())) ;
        userMgrDTO.setMANAGE_ISO_YN_09(checkValue(userMgrDTO.getMANAGE_ISO_YN_09())) ;

        userMgrDTO.setMANAGE_PART_YN(checkValue(userMgrDTO.getMANAGE_PART_YN())) ;

        userMgrDTO.setMANAGE_COA_YN_01(checkValue(userMgrDTO.getMANAGE_COA_YN_01())) ;
        userMgrDTO.setMANAGE_COA_YN_02(checkValue(userMgrDTO.getMANAGE_COA_YN_02())) ;
        userMgrDTO.setMANAGE_COA_YN_03(checkValue(userMgrDTO.getMANAGE_COA_YN_03())) ;
        userMgrDTO.setMANAGE_COA_YN_04(checkValue(userMgrDTO.getMANAGE_COA_YN_04())) ;

        userMgrDTO.setMANAGE_CHANGE_YN_01(checkValue(userMgrDTO.getMANAGE_CHANGE_YN_01())) ;
        userMgrDTO.setMANAGE_CHANGE_YN_02(checkValue(userMgrDTO.getMANAGE_CHANGE_YN_02())) ;
        userMgrDTO.setMANAGE_CHANGE_YN_03(checkValue(userMgrDTO.getMANAGE_CHANGE_YN_03())) ;
        userMgrDTO.setMANAGE_CHANGE_YN_04(checkValue(userMgrDTO.getMANAGE_CHANGE_YN_04())) ;
        userMgrDTO.setMANAGE_CHANGE_YN_05(checkValue(userMgrDTO.getMANAGE_CHANGE_YN_05())) ;

        //endregion

        String  info_flag =  userMgrDTO.getINFO_FLAG();
        if (info_flag.equals("U")) {
            userMgrService.updateUserMgr(userMgrDTO);
        } else {
            userMgrService.insertUserMgr(userMgrDTO);
        }

        model.addAttribute("message", "사용자 정보가 성공적으로 업데이트되었습니다.");
        return "ok";
    }



    @PostMapping("/userMgr/updateUserMgrMyPage")
    @ResponseBody
    public String updateUserMgrMyPage( @ModelAttribute UserMgrDTO userMgrDTO
             ,@RequestParam(value = "checkboxes", required = false) String checkboxesJson
            ,Model model) {

        //동우!!! 체크 박스처리 길이 길다.
        //region
        userMgrDTO.setMANAGE_SYSTEM_YN(checkValue(userMgrDTO.getMANAGE_SYSTEM_YN())) ;

        userMgrDTO.setMANAGE_ISO_YN_01(checkValue(userMgrDTO.getMANAGE_ISO_YN_01())) ;
        userMgrDTO.setMANAGE_ISO_YN_02(checkValue(userMgrDTO.getMANAGE_ISO_YN_02())) ;
        userMgrDTO.setMANAGE_ISO_YN_03(checkValue(userMgrDTO.getMANAGE_ISO_YN_03())) ;
        userMgrDTO.setMANAGE_ISO_YN_04(checkValue(userMgrDTO.getMANAGE_ISO_YN_04())) ;
        userMgrDTO.setMANAGE_ISO_YN_05(checkValue(userMgrDTO.getMANAGE_ISO_YN_05())) ;
        userMgrDTO.setMANAGE_ISO_YN_06(checkValue(userMgrDTO.getMANAGE_ISO_YN_06())) ;
        userMgrDTO.setMANAGE_ISO_YN_07(checkValue(userMgrDTO.getMANAGE_ISO_YN_07())) ;
        userMgrDTO.setMANAGE_ISO_YN_08(checkValue(userMgrDTO.getMANAGE_ISO_YN_08())) ;
        userMgrDTO.setMANAGE_ISO_YN_09(checkValue(userMgrDTO.getMANAGE_ISO_YN_09())) ;

        userMgrDTO.setMANAGE_PART_YN(checkValue(userMgrDTO.getMANAGE_PART_YN())) ;

        userMgrDTO.setMANAGE_COA_YN_01(checkValue(userMgrDTO.getMANAGE_COA_YN_01())) ;
        userMgrDTO.setMANAGE_COA_YN_02(checkValue(userMgrDTO.getMANAGE_COA_YN_02())) ;
        userMgrDTO.setMANAGE_COA_YN_03(checkValue(userMgrDTO.getMANAGE_COA_YN_03())) ;
        userMgrDTO.setMANAGE_COA_YN_04(checkValue(userMgrDTO.getMANAGE_COA_YN_04())) ;

        userMgrDTO.setMANAGE_CHANGE_YN_01(checkValue(userMgrDTO.getMANAGE_CHANGE_YN_01())) ;
        userMgrDTO.setMANAGE_CHANGE_YN_02(checkValue(userMgrDTO.getMANAGE_CHANGE_YN_02())) ;
        userMgrDTO.setMANAGE_CHANGE_YN_03(checkValue(userMgrDTO.getMANAGE_CHANGE_YN_03())) ;
        userMgrDTO.setMANAGE_CHANGE_YN_04(checkValue(userMgrDTO.getMANAGE_CHANGE_YN_04())) ;
        userMgrDTO.setMANAGE_CHANGE_YN_05(checkValue(userMgrDTO.getMANAGE_CHANGE_YN_05())) ;

        userMgrDTO.setALARM_AUDIT_YN(checkValue(userMgrDTO.getALARM_AUDIT_YN())); ;
        userMgrDTO.setALARM_PART_YN(checkValue(userMgrDTO.getALARM_PART_YN())); ;
        userMgrDTO.setALARM_COA_CHANGE_YN(checkValue(userMgrDTO.getALARM_COA_CHANGE_YN())); ;

        //endregion

        if(userMgrDTO.getUSER_GUBN() == 0) {

            userMgrService.updateUserMgrMyPage(userMgrDTO);

        }else {

            //해당 유저 접속 목적 전체 삭제.
            userMgrService.deleteConnectGoal(userMgrDTO);

            // 체크박스 데이터 JSON
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> selectedGoals = objectMapper.readValue(checkboxesJson, List.class);


                for (Map<String, Object> goal : selectedGoals) {
                    String base_code = (String) goal.get("id");
                    Boolean checked = (Boolean) goal.get("checked");
                    //System.out.println("체크박스들: " + "ID: " + base_code + ", Checked: " + checked);

                    if(checked) {
                        userMgrDTO.setBASE_CODE(base_code);
                        userMgrService.insertConnectGoal(userMgrDTO);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }

        }

        model.addAttribute("message", "사용자 정보가 성공적으로 업데이트되었습니다.");  //
        return "ok";
    }

    private final LoginService loginService;

    @PostMapping("/userMgr/updateUserPWS")
    @ResponseBody
    public ResponseEntity<?> updateUserPWS(@ModelAttribute UserMgrDTO userMgrDTO , Authentication authentication) {

        try {
            String loginId = authentication.getName(); // 사용자 이름 = ID  2024.11.04 일단 이걸로.
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUSER_ID(loginId);
            loginDTO.setUSER_PWD(userMgrDTO.getUSER_PWD());

            LoginDTO loginResult = loginService.login(loginDTO);

            if (loginResult != null) {

                userMgrDTO.setUSER_PWD(userMgrDTO.getUSER_PWD_NEW());
                userMgrDTO.setUSER_ID(loginId);
                userMgrService.updateUserPWS(userMgrDTO);
                return ResponseEntity.ok("비밀번호가 변경 되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("기존 비밀번호가 틀립니다.");
            }
        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }

    }

}
