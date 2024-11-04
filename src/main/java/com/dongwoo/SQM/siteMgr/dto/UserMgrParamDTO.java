package com.dongwoo.SQM.siteMgr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserMgrParamDTO {

    @JsonProperty("USER_IDX")
    private int USER_IDX      ;    //-- 아이디x  ㅁ

    @JsonProperty("USER_ID")
    private String USER_ID      ;    //-- 아이디  ㅁ

    @JsonProperty("USER_NAME")
    private String USER_NAME    ;    //-- 이름 ㅁ

    @JsonProperty("DEPT_CODE")
    private String DEPT_CODE;                 //-- 기초코드 INDEX (사업본부는 기초코드로 관리) ▼

    @JsonProperty("EMAIL")
    private String EMAIL;                     //-- 이메일 ㅁ

    @JsonProperty("USER_LEVEL")
    private String USER_LEVEL;                //-- (100:관리자, 1:사용자)  ▼

    @JsonProperty("USER_STATUS")
    private String USER_STATUS;                //사용자 상태 (Y:사용, N:미사용)   ▼ USER_INFO

    @JsonProperty("INFO_FLAG")
    private String INFO_FLAG        ;    //insert update type

    @JsonProperty("MANAGE_SYSTEM_YN")
    private String MANAGE_SYSTEM_YN;

    @JsonProperty("MANAGE_ISO_YN_01")
    private String MANAGE_ISO_YN_01;

    @JsonProperty("MANAGE_ISO_YN_02")
    private String MANAGE_ISO_YN_02;

    @JsonProperty("MANAGE_ISO_YN_03")
    private String MANAGE_ISO_YN_03;

    @JsonProperty("MANAGE_ISO_YN_04")
    private String MANAGE_ISO_YN_04;

    @JsonProperty("MANAGE_ISO_YN_05")
    private String MANAGE_ISO_YN_05;

    @JsonProperty("MANAGE_ISO_YN_06")
    private String MANAGE_ISO_YN_06;

    @JsonProperty("MANAGE_ISO_YN_07")
    private String MANAGE_ISO_YN_07;

    @JsonProperty("MANAGE_ISO_YN_08")
    private String MANAGE_ISO_YN_08;

    @JsonProperty("MANAGE_ISO_YN_09")
    private String MANAGE_ISO_YN_09;

    @JsonProperty("MANAGE_PART_YN")
    private String MANAGE_PART_YN;

    @JsonProperty("MANAGE_COA_YN_01")
    private String MANAGE_COA_YN_01;

    @JsonProperty("MANAGE_COA_YN_02")
    private String MANAGE_COA_YN_02;

    @JsonProperty("MANAGE_COA_YN_03")
    private String MANAGE_COA_YN_03;

    @JsonProperty("MANAGE_COA_YN_04")
    private String MANAGE_COA_YN_04;

    @JsonProperty("MANAGE_CHANGE_YN_01")
    private String MANAGE_CHANGE_YN_01;

    @JsonProperty("MANAGE_CHANGE_YN_02")
    private String MANAGE_CHANGE_YN_02;

    @JsonProperty("MANAGE_CHANGE_YN_03")
    private String MANAGE_CHANGE_YN_03;

    @JsonProperty("MANAGE_CHANGE_YN_04")
    private String MANAGE_CHANGE_YN_04;

    @JsonProperty("MANAGE_CHANGE_YN_05")
    private String MANAGE_CHANGE_YN_05;

    @JsonProperty("ALARM_AUDIT_YN")
    private String ALARM_AUDIT_YN;

    @JsonProperty("ALARM_PART_YN")
    private String ALARM_PART_YN;

    @JsonProperty("ALARM_COA_CHANGE_YN")
    private String ALARM_COA_CHANGE_YN;



}
