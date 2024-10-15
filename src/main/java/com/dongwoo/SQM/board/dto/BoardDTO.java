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
    private int ATTACHED_FILE;
    private List<MultipartFile> BOARD_FILE;

}
