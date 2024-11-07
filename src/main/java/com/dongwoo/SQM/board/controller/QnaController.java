package com.dongwoo.SQM.board.controller;

import com.dongwoo.SQM.board.dto.BoardDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /**
     * Q&A 리스트
     * @param criteria
     * @param model
     * @return
     */
    @GetMapping("/admin/board/qa")
    public String list(Criteria criteria, Model model) {
        /*log.info("criteria============================================="+criteria);
        List<BoardDTO> boardDTOList = boardService.getList(criteria);
        model.addAttribute("boardList", boardDTOList);
        model.addAttribute("pageMaker", new PageDTO(boardService.getTotal(), 10, criteria));
        log.info("boardDTOList = " + boardDTOList);*/
        return "board/adminList";
    }

    /**
     * q&A 상세
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/admin/board/qa/{id}")
    public String findById(@PathVariable("id") int id, Model model) {
        // 조회수 처리.
        boardService.updateHits(id);
        // 상세내용 가져옴
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);

        return "board/Detail";
    }

    @GetMapping("/admin/board/download/{filename}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable String filename) {
        try {
            log.info("filename???????"+filename);
            // 파일 경로 설정
            File file = new File("D:/devp/" +  filename);
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 파일 리소스 생성
            FileSystemResource resource = new FileSystemResource(file);

            // 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Q&A 등록 화면
     * @return
     */
    @GetMapping("/admin/board/qa/save")
    public String save() {
        return "/Board/adminSave";
    }

    /**
     * Q&A 등록 처리
     * @param boardDTO
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/admin/board/qa/save")
    public String save(BoardDTO boardDTO, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("boardDTO = " + boardDTO);
        // 파일 업로드 처리 시작
        UUID uuid = UUID.randomUUID(); // 랜덤으로 식별자를 생성

        String directory = "D:/devp/";
        String fileName = uuid + "_" + file.getOriginalFilename(); // UUID와 파일이름을 포함된 파일 이름으로 저장

        File saveFile = new File(directory, URLEncoder.encode(fileName, StandardCharsets.UTF_8)); // projectPath는 위에서 작성한 경로, name은 전달받을 이름

        file.transferTo(saveFile);

        boardDTO.setFILE_NAME(fileName);
        boardDTO.setFILE_PATH(directory+fileName); // static 아래부분의 파일 경로로만으로도 접근이 가능
        // 파일 업로드 처리 끝

        boardService.save(boardDTO);
        return "redirect:/admin/board/qa/list";
    }

    @GetMapping("/admin/board/qa/update/{id}")
    public String update(@PathVariable("id") int id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("board", boardDTO);
        return "/board/adminUpdate";
    }

    @PostMapping("/admin/board/qa/update/{id}")
    public String update(BoardDTO boardDTO, Model model) {
        boardService.update(boardDTO);
        BoardDTO dto = boardService.findById(boardDTO.getBOARD_IDX());
        model.addAttribute("board", dto);
        return "/board/adminDetail";
    }

    @GetMapping("/admin/board/qa/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        boardService.delete(id);
        return "redirect:/board/list";
    }
}
