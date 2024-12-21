package com.dongwoo.SQM.board.controller;

import com.dongwoo.SQM.board.dto.BoardDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.board.service.BoardService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class QnaController {
    private final BoardService boardService;
    @Value("${Upload.path.attach}")
    private String uploadPath;

    public static final String[] ALLOWED_EXTENSIONS = {
            "jpg", "jpeg", "png", "gif", "bmp", "tiff", "svg",  // 그림 파일
            "xls", "xlsx", "pdf",                               // 엑셀 파일,pdf
            "ppt", "pptx",                                      // 파워포인트 파일
            "txt", "csv", "md", "log" ,                          // 텍스트 파일
            "doc", "docx"  ,                                     // 워드 파일
            "zip", "rar", "tar", "gz", "7z"                      // 압축 파일
    };

    public static final long maxSize = 10 * 1024 * 1024; // 10MB


    public boolean isValidFileExtension(String fileName) {
        // 파일의 확장자를 추출
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        // 상수 배열에서 확장자 유효성 체크
        for (String allowedExtension : ALLOWED_EXTENSIONS) {
            if (allowedExtension.equals(extension)) {
                return true;
            }
        }
        return false;
    }

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

        if(boardDTO.getANSWER_FILE_NAME() != null) {
            String answerfilename = removeUuidFromFileName(boardDTO.getANSWER_FILE_NAME());
            boardDTO.setANSWER_FILE_SHOTNAME(answerfilename);
        }

        model.addAttribute("board", boardDTO);

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user_gubun = user.getUSER_GUBUN();
        String user_id = user.getUsername();
        model.addAttribute("userGubun", user_gubun);
        model.addAttribute("userid",user_id);

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

        if(boardDTO.getANSWER_FILE_NAME() != null) {
            String answerfilename = removeUuidFromFileName(boardDTO.getANSWER_FILE_NAME());
            boardDTO.setANSWER_FILE_SHOTNAME(answerfilename);
        }

        model.addAttribute("board", boardDTO);

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user_gubun = user.getUSER_GUBUN();
        String user_id = user.getUsername();
        model.addAttribute("userGubun", user_gubun);
        model.addAttribute("userid",user_id);

        return "board/QnADetail";
    }

    //답변팝업
    @PostMapping("/admin/board/answer")
//    public ResponseEntity<BoardDTO> getAnswer(@PathVariable("id") int id) {
    public ResponseEntity<BoardDTO> getUserInfo(@RequestBody Map<String, Integer> params) {
        int idx = params.get("BOARD_IDX");
        BoardDTO boardDTO = boardService.findById(idx);


        if(boardDTO.getANSWER_FILE_NAME() != null) {
            String answerfilename = removeUuidFromFileName(boardDTO.getANSWER_FILE_NAME());
            boardDTO.setANSWER_FILE_SHOTNAME(answerfilename);
        }

        if (boardDTO != null) {
            return ResponseEntity.ok(boardDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    //수정 조회 관리자
    @GetMapping("/admin/board/QnAUpdate/{id}")
    public String update(@PathVariable("id") int id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);

        if(boardDTO.getFILE_NAME() != null) {
            String filename = removeUuidFromFileName(boardDTO.getFILE_NAME());
            boardDTO.setFILE_SHOTNAME(filename);
        }

        model.addAttribute("board", boardDTO);

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user_gubun = user.getUSER_GUBUN();
        model.addAttribute("userGubun", user_gubun);

        return "/board/QnAUpdate";
    }

    //수정 조회 user
    @GetMapping("/user/board/QnAUpdate/{id}")
    public String update_user(@PathVariable("id") int id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);

        if(boardDTO.getFILE_NAME() != null) {
            String filename = removeUuidFromFileName(boardDTO.getFILE_NAME());
            boardDTO.setFILE_SHOTNAME(filename);
        }

        model.addAttribute("board", boardDTO);

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user_gubun = user.getUSER_GUBUN();
        model.addAttribute("userGubun", user_gubun);

        return "/board/QnAUpdate";
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

    // 파일 다운로드 처리 _user
    @GetMapping("/user/board/download")
    public ResponseEntity<Resource> downloadFile_user(@RequestParam("fileName") String fileName) {


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
    public String save(Model model) {
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user_gubun = user.getUSER_GUBUN();
        model.addAttribute("userGubun", user_gubun);
        return "/board/QnASave";
    }

    // Q&A 등록 화면 _user
    @GetMapping("/user/board/qna/save")
    public String save_user(Model model) {
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user_gubun = user.getUSER_GUBUN();
        model.addAttribute("userGubun", user_gubun);
        return "/board/QnASave";
    }

     //Q&A 등록,수정 처리
    @PostMapping("/admin/board/qna/save")
    public  ResponseEntity<Map<String, Object>>  save(BoardDTO boardDTO, @RequestParam("file") MultipartFile file,@AuthenticationPrincipal UserCustom user) throws IOException {
        log.info("boardDTO = " + boardDTO);
        log.info("user.getUSER_IDX() ============= " + user.getUSER_IDX());
        Map<String, Object> response = new HashMap<>();

        boardDTO.setBOARD_TYPE("QNA");
        BoardDTO getBoardDTO = boardService.findById(boardDTO.getBOARD_IDX());

        if (!file.isEmpty()) {
            try {
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename();

                if(isValidFileExtension(fileName)) {
                    File saveFile = new File(uploadPath + File.separator + fileName);
                    file.transferTo(saveFile);

                    boardDTO.setFILE_NAME(fileName);  //파일명
                    boardDTO.setFILE_PATH(saveFile.getAbsolutePath()); //경로
                }else {

                    response.put("status", "error");
                    response.put("message", "허용되지 않는 파일 형식입니다.");
                    return ResponseEntity.ok(response);
                }

                if (file.getSize() > maxSize) {
                    response.put("status", "error");
                    response.put("message", "파일 용량이 10MB를 초과합니다.");
                    return ResponseEntity.ok(response);
                }

            } catch (IOException e) {
                response.put("status", "error");
                response.put("message", "오류가 발생했습니다.");
                e.printStackTrace();
                return ResponseEntity.ok(response);
            }
        }else{
            if(Objects.equals(boardDTO.getFILE_NAME(), "")) {
                if (getBoardDTO.getFILE_NAME() != null) {
                    boardDTO.setFILE_NAME(null);  //파일명
                    boardDTO.setFILE_PATH(null); //경로
                }
            }
        }

        if(getBoardDTO == null) {
            boardDTO.setINPUT_USER_ID(user.getUsername());
            boardService.save(boardDTO);
            response.put("message", "등록 되었습니다.");
        }else {
            //수정
            boardDTO.setMODIFY_USER_ID(user.getUsername());
            boardService.update(boardDTO);
            response.put("message", "수정 되었습니다.");
        }

        response.put("status", "success");

        return ResponseEntity.ok(response);
        
    }


    //Q&A 등록,수정 처리 _user
    @PostMapping("/user/board/qna/save")
    public  ResponseEntity<Map<String, Object>>  save_user(BoardDTO boardDTO, @RequestParam("file") MultipartFile file,@AuthenticationPrincipal UserCustom user) throws IOException {
        log.info("boardDTO = " + boardDTO);
        log.info("user.getUSER_IDX() ============= " + user.getUSER_IDX());
        Map<String, Object> response = new HashMap<>();

        boardDTO.setBOARD_TYPE("QNA");
        BoardDTO getBoardDTO = boardService.findById(boardDTO.getBOARD_IDX());

        if (!file.isEmpty()) {
            try {
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename();

                if(isValidFileExtension(fileName)) {
                    File saveFile = new File(uploadPath + File.separator + fileName);
                    file.transferTo(saveFile);

                    boardDTO.setFILE_NAME(fileName);  //파일명
                    boardDTO.setFILE_PATH(saveFile.getAbsolutePath()); //경로
                }else {

                    response.put("status", "error");
                    response.put("message", "허용되지 않는 파일 형식입니다.");
                    return ResponseEntity.ok(response);
                }

                if (file.getSize() > maxSize) {
                    response.put("status", "error");
                    response.put("message", "파일 용량이 10MB를 초과합니다.");
                    return ResponseEntity.ok(response);
                }

            } catch (IOException e) {
                response.put("status", "error");
                response.put("message", "오류가 발생했습니다.");
                e.printStackTrace();
                return ResponseEntity.ok(response);
            }
        }else{
            if(Objects.equals(boardDTO.getFILE_NAME(), "")) {
                if (getBoardDTO.getFILE_NAME() != null) {
                    boardDTO.setFILE_NAME(null);  //파일명
                    boardDTO.setFILE_PATH(null); //경로
                }
            }
        }

        if(getBoardDTO == null) {
            boardDTO.setINPUT_USER_ID(user.getUsername());
            boardService.save(boardDTO);
            response.put("message", "등록 되었습니다.");
        }else {
            //수정
            boardDTO.setMODIFY_USER_ID(user.getUsername());
            boardService.update(boardDTO);
            response.put("message", "수정 되었습니다.");
        }

        response.put("status", "success");

        return ResponseEntity.ok(response);

    }



    //Q&A 답변 처리
    @PostMapping("/admin/board/qna/answer")
    public ResponseEntity<Map<String, Object>>  save_answer(BoardDTO boardDTO, @RequestParam("ansfile") MultipartFile file, @AuthenticationPrincipal UserCustom user) throws IOException {
        log.info("boardDTO = " + boardDTO);
        log.info("user.getUSER_IDX() ============= " + user.getUSER_IDX());
        Map<String, Object> response = new HashMap<>();
        boardDTO.setBOARD_TYPE("QNA");
        BoardDTO getBoardDTO = boardService.findById(boardDTO.getBOARD_IDX());

        if (!file.isEmpty()) {
            try {
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + file.getOriginalFilename();

                if(isValidFileExtension(fileName)) {
                    File saveFile = new File(uploadPath + File.separator + fileName);
                    file.transferTo(saveFile);

                    boardDTO.setANSWER_FILE_NAME(fileName);  //파일명
                    boardDTO.setANSWER_FILE_PATH(saveFile.getAbsolutePath()); //경로
                }else {

                    response.put("status", "error");
                    response.put("message", "허용되지 않는 파일 형식입니다.");
                    return ResponseEntity.ok(response);
                }

                if (file.getSize() > maxSize) {
                    response.put("status", "error");
                    response.put("message", "파일 용량이 10MB를 초과합니다.");
                    return ResponseEntity.ok(response);
                }

            } catch (IOException e) {
                e.printStackTrace();
                response.put("status", "error");
                response.put("message", "답변 등록 중 오류가 발생했습니다.");

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }else{
            if(Objects.equals(boardDTO.getANSWER_FILE_NAME(), "")) {
                if (getBoardDTO.getANSWER_FILE_NAME() != null) {
                    boardDTO.setANSWER_FILE_NAME(null);  //파일명
                    boardDTO.setANSWER_FILE_PATH(null); //경로
                }
            }
        }

        if(getBoardDTO != null) {
            boardDTO.setANSWER_ID(user.getUsername());
            boardService.updateAnswer(boardDTO);
        }

        response.put("status", "success");
        response.put("message", "답변이 성공적으로 등록되었습니다.");

        return ResponseEntity.ok(response);  // 200 OK 응답

    }



    //삭제
    @PostMapping("/admin/board/qna/delete/{id}")
    public ResponseEntity<Map<String, Object>>  delete(@PathVariable("id") int id , RedirectAttributes redirectAttributes) {
        Map<String, Object> response = new HashMap<>();
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user_id = user.getUsername();

        BoardDTO getBoardDTO = boardService.findById(id);
        if(user_id.equals(getBoardDTO.getINPUT_USER_ID())) {
            boardService.delete(id);
            response.put("status", "success");
            response.put("message", "게시물이 삭제되었습니다.");

        }else {
            response.put("status", "fail");
            response.put("message", "작성자만 삭제할 수 있습니다.");
        }
        return ResponseEntity.ok(response);
    }

    //삭제 _user
    @PostMapping("/user/board/qna/delete/{id}")
    public ResponseEntity<Map<String, Object>>  delete_user(@PathVariable("id") int id , RedirectAttributes redirectAttributes) {
        Map<String, Object> response = new HashMap<>();
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String user_id = user.getUsername();

        BoardDTO getBoardDTO = boardService.findById(id);
        if(user_id.equals(getBoardDTO.getINPUT_USER_ID())) {
            boardService.delete(id);
            response.put("status", "success");
            response.put("message", "게시물이 삭제되었습니다.");

        }else {
            response.put("status", "fail");
            response.put("message", "작성자만 삭제할 수 있습니다.");
        }
        return ResponseEntity.ok(response);
    }


}
