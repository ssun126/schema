package com.dongwoo.SQM.companyInfo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CompanyInfoDTO {
    private int BOARD_IDX;
    private String INPUT_USER_ID;
    private String BOARD_PASS;
    private String BOARD_TITLE;
    private String BOARD_DESC;
    private int BOARD_HITS;
    private String INPUT_DATE;

}
