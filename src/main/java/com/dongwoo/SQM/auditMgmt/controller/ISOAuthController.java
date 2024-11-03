package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ISOAuthController {
    private final BoardService boardService;

    @GetMapping("/user/auditMgmt/isoAuth")
    public String isoAuthMain(Model model) {
        return "isoAuth/main";
    }

}
