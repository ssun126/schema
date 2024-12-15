package com.dongwoo.SQM.siteMgr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class BaseCodeDTO {
    @JsonProperty("BASE_CODE_IDX")
    private int BASE_CODE_IDX;
    @JsonProperty("GUBN")
    private String GUBN;  //구분
    @JsonProperty("GROUP_CODE")
    private String GROUP_CODE;   //그룹
    @JsonProperty("GROUP_NAME")
    private String GROUP_NAME;   //그룹
    @JsonProperty("BASE_CODE")
    private String BASE_CODE;        //코드
    @JsonProperty("BASE_NAME")
    private String BASE_NAME;    //코드명
    @JsonProperty("BASE_VALUE")
    private String BASE_VALUE;   //코드값
    @JsonProperty("BASE_SUMMARY")
    private String BASE_SUMMARY;     //설명
    @JsonProperty("MULTI_CODENAME")
    private String MULTI_CODENAME;
    @JsonProperty("SIMPLE_CODENAME")
    private String SIMPLE_CODENAME;
    @JsonProperty("BASE_SORT")
    private int BASE_SORT;        //정렬
    @JsonProperty("REG_DW_USER_IDX")
    private int REG_DW_USER_IDX;   //등록자ID
    @JsonProperty("REGISTER_NAME")
    private String REGISTER_NAME;   //등록자명
    @JsonProperty("UP_DW_USER_IDX")
    private int UP_DW_USER_IDX;   //수정자
    @JsonProperty("UPDATE_NAME")
    private String UPDATE_NAME;   //수정자명
    @JsonProperty("REG_DATE")
    private String REG_DATE;  //등록일
    @JsonProperty("UP_DATE")
    private String UP_DATE;  //수정일
    @JsonProperty("BASE_STATUS")
    private String BASE_STATUS;      //사용유무
    @JsonProperty("BASE_OPTION")
    private String BASE_OPTION;      //옵션
    @JsonProperty("BASE_OPTION2")
    private String BASE_OPTION2;      //옵션
    @JsonProperty("BASE_OPTION3")
    private String BASE_OPTION3;      //옵션
    @JsonProperty("INFO_FLAG")
    private String INFO_FLAG;

}
