package com.dongwoo.SQM.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardFileDTO {
    private Long FILE_IDX;
    private Long BOARD_IDX;
    private String ORG_FILE_NAME;
    private String FILE_NAME;
}
