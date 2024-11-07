package com.dongwoo.SQM.board.controller;

import com.dongwoo.SQM.board.dto.BoardDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.board.service.BoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class BoardRestController {
    @Autowired
    private BoardService boardService;


    @GetMapping("/boardList")
    public List<BoardDTO> getBoardList(Criteria criteria) {
        // 회사 정보 리스트를 가져옵니다
        List<BoardDTO> boardList = boardService.getList(criteria);
        log.info("boardList>>>>>>>>>>"+boardList);
        // 반환되는 데이터가 JSON 형식으로 자동 변환됩니다
        return boardList;
    }

    @GetMapping("/boardList/pageMaker")
    public PageDTO getBoardListPage(Criteria criteria) {
        // pageMaker 객체를 생성하고 반환
        int total = boardService.getTotal();  // 전체 데이터 개수
        PageDTO pageMaker = new PageDTO(total, 10, criteria);  // 10은 한 페이지당 보여줄 항목 수
        log.info("pageMaker>>>>>>>>>>"+pageMaker);
        return pageMaker;
    }
}
