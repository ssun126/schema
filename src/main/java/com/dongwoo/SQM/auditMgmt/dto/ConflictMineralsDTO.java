package com.dongwoo.SQM.auditMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConflictMineralsDTO {
    //자재 정보 - PartMgmtDTO
    @JsonProperty("PM_PART_CODE")
    private String PM_PART_CODE;    //자재코드
    @JsonProperty("PM_PART_NAME")
    private String PM_PART_NAME;    //자재명칭
    @JsonProperty("PM_PART_PLANT_CODE")
    private String PM_PART_PLANT_CODE;
    @JsonProperty("PM_APPROVAL_STATUS")
    private String PM_APPROVAL_STATUS;
    @JsonProperty("PM_APPROVAL_DATE")
    private String PM_APPROVAL_DATE;

    //분쟁광물 정보
    @JsonProperty("COM_CODE")
    private String COM_CODE;
    @JsonProperty("PART_CODE")
    private String PART_CODE;
    @JsonProperty("TANTALUM_YN")
    private String TANTALUM_YN;
    @JsonProperty("TUNGSTEN_YN")
    private String TUNGSTEN_YN;
    @JsonProperty("TIN_YN")
    private String TIN_YN;
    @JsonProperty("GOLD_YN")
    private String GOLD_YN;
    @JsonProperty("COBALT_YN")
    private String COBALT_YN;
    @JsonProperty("MICA_YN")
    private String MICA_YN;
    @JsonProperty("INSPECTION_DATE")
    private String INSPECTION_DATE; //조사일자

    //CMRT/EMRT 파일
    @JsonProperty("FILE_TYPE")
    private String FILE_TYPE; //CMRT/EMRT 구분
    @JsonProperty("FILE_NAME")
    private String FILE_NAME;
    @JsonProperty("FILE_PATH")
    private String FILE_PATH;
    @JsonProperty("AUTH_SEQ")
    private int AUTH_SEQ;
    @JsonProperty("AUTH_TYPE")
    private String AUTH_TYPE;
}
