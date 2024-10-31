package com.dongwoo.SQM.siteMgr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseCodeDTO {
    private int BASE_CODE_IDX;
    private String GUBN;  //구분
    private String GROUP_CODE;   //그룹
    private String BASE_CODE;        //코드
    private String BASE_NAME;    //코드명
    private String BASE_VALUE;   //코드값
    private String BASE_SUMMARY;     //설명
    private String MULTI_CODENAME;
    private String SIMPLE_CODENAME;
    private int BASE_SORT;        //정렬
    private int REG_DW_USER_IDX;   //등록자ID
    private String REGISTER_NAME;   //등록자명
    private int UP_DW_USER_IDX;   //수정자
    private String REG_DATE;  //등록일
    private String UP_DATE;  //수정일
    private String BASE_STATUS;      //사용유무
    private String BASE_OPTION;      //옵션

    private String INFO_FLAG;



}
