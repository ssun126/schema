package com.dongwoo.SQM.board.controller;

import com.dongwoo.SQM.board.dto.BoardDTO;
import com.dongwoo.SQM.board.service.BoardService;
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
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/board/save")
    public String save() {
        return "/Board/save";
    }

    @PostMapping("/board/save")
    public String save(BoardDTO boardDTO) throws IOException {
        System.out.println("boardDTO = " + boardDTO);
        boardService.save(boardDTO);
        return "redirect:/board/list";
    }

    @GetMapping("/board/list")
    public String findAll(Model model) {
        List<BoardDTO> boardDTOList = boardService.findAll();
        model.addAttribute("boardList", boardDTOList);
        System.out.println("boardDTOList = " + boardDTOList);
        return "/board/list";
    }

    @GetMapping("/board/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        // 조회수 처리
        boardService.updateHits(id);
        // 상세내용 가져옴
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        System.out.println("boardDTO = " + boardDTO);
//        if (boardDTO.getATTACHED_FILE().equals(1)) {
//            List<BoardFileDTO> boardFileDTOList = boardService.findFile(id);
//            model.addAttribute("boardFileList", boardFileDTOList);
//        }
        return "/board/detail";
    }

    @GetMapping("/board/update/{id}")
    public String update(@PathVariable("id") int id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "/board/update";
    }

    @PostMapping("/board/update/{id}")
    public String update(BoardDTO boardDTO, Model model) {
        boardService.update(boardDTO);
        BoardDTO dto = boardService.findById(boardDTO.getBOARD_IDX());
        model.addAttribute("board", dto);
        return "/board/detail";
    }

    @GetMapping("/board/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        boardService.delete(id);
        return "redirect:/board/list";
    }
}
