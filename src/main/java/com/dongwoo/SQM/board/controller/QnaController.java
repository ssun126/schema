package com.dongwoo.SQM.board.controller;

import com.dongwoo.SQM.board.dto.BoardDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.board.service.BoardService;
import com.dongwoo.SQM.config.security.UserCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QnaController {
    private final BoardService boardService;
    @Value("${Upload.path.attach}")
    private String uploadPath;



    //Q&A 리스트
    @GetMapping("/admin/board/qna")
    public String list(Criteria criteria, Model model) {

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user_gubun = user.getUSER_GUBUN();
        model.addAttribute("userGubun", user_gubun);

        return "board/QnAList";
    }
    //Q&A 리스트_user
    @GetMapping("/user/board/qna")
    public String list_user(Criteria criteria, Model model) {

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user_gubun = user.getUSER_GUBUN();
        model.addAttribute("userGubun", user_gubun);

        return "board/QnAList";
    }

    //q&A 상세
    @GetMapping("/admin/board/qna/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);

        if(boardDTO.getFILE_NAME() != null) {
            String filename = removeUuidFromFileName(boardDTO.getFILE_NAME());
            boardDTO.setFILE_SHOTNAME(filename);
        }

        model.addAttribute("board", boardDTO);

        return "board/QnADetail";
    }
    //q&A 상세 _user
    @GetMapping("/user/board/qna/{id}")
    public String findById_user(@PathVariable("id") int id, Model model) {
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);

        if(boardDTO.getFILE_NAME() != null) {
            String filename = removeUuidFromFileName(boardDTO.getFILE_NAME());
            boardDTO.setFILE_SHOTNAME(filename);
        }

        model.addAttribute("board", boardDTO);

        return "board/QnADetail";
    }

    //수정 조회 관리자
    @GetMapping("/admin/board/adminUpdate/{id}")
    public String update(@PathVariable("id") int id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);

        if(boardDTO.getFILE_NAME() != null) {
            String filename = removeUuidFromFileName(boardDTO.getFILE_NAME());
            boardDTO.setFILE_SHOTNAME(filename);
        }

        model.addAttribute("board", boardDTO);

        return "/board/adminUpdate";
    }

    private String removeUuidFromFileName(String fileName) {
        if (fileName != null && fileName.contains("_")) {
            String[] parts = fileName.split("_", 2);
            return parts[1];
        }
        return fileName;
    }


    // 파일 다운로드 처리
    @GetMapping("/admin/board/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName) {


        File file = new File(uploadPath +"\\"+  fileName);

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();

        try {
            // 파일 이름을 UTF-8로 인코딩
            String encodedFileName = URLEncoder.encode( removeUuidFromFileName(fileName), "UTF-8");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }


     // Q&A 등록 화면
    @GetMapping("/admin/board/qna/save")
    public String save() {
        return "/board/adminSave";
    }


     //Q&A 등록,수정 처리
    @PostMapping("/admin/board/qna/save")
    public String save(BoardDTO boardDTO, @RequestParam("file") MultipartFile file,@AuthenticationPrincipal UserCustom user) throws IOException {
        log.info("boardDTO = " + boardDTO);
        log.info("user.getUSER_IDX() ============= " + user.getUSER_IDX());

        boardDTO.setBOARD_TYPE("QNA");
        BoardDTO getBoardDTO = boardService.findById(boardDTO.getBOARD_IDX());

        if (!file.isEmpty()) {
            try {
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename();

                File saveFile = new File(uploadPath + File.separator + fileName);
                file.transferTo(saveFile);

                boardDTO.setFILE_NAME(fileName);  //파일명
                boardDTO.setFILE_PATH(saveFile.getAbsolutePath()); //경로
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(getBoardDTO == null) {
            boardDTO.setINPUT_USER_ID(user.getUsername());
            boardService.save(boardDTO);
        }else {
            //수정
            boardDTO.setMODIFY_USER_ID(user.getUsername());
            boardService.update(boardDTO);
        }

        return "redirect:/admin/board/qna";
    }





    //삭제 ok
    @GetMapping("/admin/board/qna/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        boardService.delete(id);
        return "redirect:/admin/board/qna";
    }


}
