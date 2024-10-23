package com.dongwoo.SQM.siteMgr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserMgrDTO {
    private int BOARD_IDX;
    private String INPUT_USER_ID;
    private String BOARD_PASS;
    private String BOARD_TITLE;
    private String BOARD_DESC;
    private int BOARD_HITS;
    private String INPUT_DATE;
    private int ATTACHED_FILE;

}
