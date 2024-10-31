package com.dongwoo.SQM.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
public class BoardDTO {
    private int BOARD_IDX;
    private String INPUT_USER_ID;
    private String BOARD_PASS;
    private String BOARD_TITLE;
    private String BOARD_DESC;
    private int BOARD_HITS;
    private String INPUT_DATE;
    private String FILE_NAME; // 파일 이름
    private String FILE_PATH; // 파일이 저장된 경로

}
