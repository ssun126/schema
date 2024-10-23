package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.siteMgr.service.UserMgrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserMgrController {
    private final UserMgrService userMgrService;

    @GetMapping("/siteMgr")
    public String userMgr() {
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
        List<UserMgrDTO> userMgrDTOList = userMgrService.findAll();
        model.addAttribute("userMgrList", userMgrDTOList);
        System.out.println("userMgrDTOList = " + userMgrDTOList);
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
}
