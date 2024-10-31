package com.dongwoo.SQM.siteMgr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserMgrParamDTO {

    @JsonProperty("USER_ID")
    private String USER_ID      ;    //-- 아이디 ▼

    @JsonProperty("USER_NAME")
    private String USER_NAME    ;    //-- 이름 ▼

    @JsonProperty("DEPT_CODE")
    private String DEPT_CODE;                 //-- 기초코드 INDEX (사업본부는 기초코드로 관리) ▼

    @JsonProperty("EMAIL")
    private String EMAIL;                     //-- 이메일 ▼

    @JsonProperty("USER_LEVEL")
    private String USER_LEVEL;                //-- (100:관리자, 1:사용자)  ▼

    @JsonProperty("USER_STATUS")
    private String USER_STATUS;          //-- SYSTEM (Y:사용, N:미사용) ▼


}
