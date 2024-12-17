package com.dongwoo.SQM.partMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PartMgmtDTO {

    @JsonProperty("PM_IDX")
    private int PM_IDX;
    @JsonProperty("PM_PART_CODE")
    private String PM_PART_CODE;    //자재코드
    @JsonProperty("PART_NAME")
    private String PART_NAME;
    @JsonProperty("PM_PART_PLANT_CODE")
    private String PM_PART_PLANT_CODE;    //자재코드
    @JsonProperty("PM_QUALITY")
    private String PM_QUALITY;          //성상
    @JsonProperty("PM_STATUS")
    private String PM_STATUS;           //혼합/단일
    @JsonProperty("PM_CHEMICAL_YN")
    private String PM_CHEMICAL_YN;      //Chemical 여부
    @JsonProperty("PM_ACTIVE_YN")
    private String PM_ACTIVE_YN;        //사용여부
    @JsonProperty("PM_REG_DATE")
    private String PM_REG_DATE;         //등록날짜
    @JsonProperty("PM_REG_USER")
    private int PM_REG_USER;         //등록자
    @JsonProperty("PM_REG_USER_NAME")
    private String PM_REG_USER_NAME;         //등록자
    @JsonProperty("PM_REG_USER_ID")
    private String PM_REG_USER_ID;         //등록자
    @JsonProperty("PM_APPROVAL_STATUS")
    private String PM_APPROVAL_STATUS;      //승일현황
    @JsonProperty("PM_APPROVAL_DATE")
    private String PM_APPROVAL_DATE;        //승인일자
    @JsonProperty("PM_MODIFY_USER")
    private int PM_MODIFY_USER;          //수정자
    @JsonProperty("PM_MODIFY_USER_NAME")
    private String PM_MODIFY_USER_NAME;          //수정자
    @JsonProperty("PM_MODIFY_USER_ID")
    private String PM_MODIFY_USER_ID;          //수정자
    @JsonProperty("PM_REQUEST_APPROVAL_DATE")
    private String PM_REQUEST_APPROVAL_DATE;          //승인요청날짜
    @JsonProperty("PM_MODIFY_DATE")
    private String PM_MODIFY_DATE;         //수정날짜

    @JsonProperty("COM_CODE")
    private String COM_CODE;
    @JsonProperty("COM_NAME")
    private String COM_NAME;
    @JsonProperty("COM_NATION")
    private String COM_NATION;    //판매국가

    @JsonProperty("SEARCH_PM_SDATE")
    private String SEARCH_PM_SDATE;    //검색 시작날짜
    @JsonProperty("SEARCH_PM_EDATE")
    private String SEARCH_PM_EDATE;    //검색 마지막날짜
    @JsonProperty("SEARCH_REG_USER")
    private String SEARCH_REG_USER;    //검색 등록자

}
