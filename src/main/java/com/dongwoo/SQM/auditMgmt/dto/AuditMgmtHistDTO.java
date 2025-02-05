package com.dongwoo.SQM.auditMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuditMgmtHistDTO {
    @JsonProperty("COM_CODE")
    private String COM_CODE;
    @JsonProperty("COM_NAME")
    private String COM_NAME;
    @JsonProperty("AUTH_TYPE")
    private String AUTH_TYPE;
    @JsonProperty("AUDIT_WAY")
    private String AUDIT_WAY;
    @JsonProperty("AUTH_CODE")
    private String AUTH_CODE;
    @JsonProperty("AUTH_SEQ")
    private int AUTH_SEQ;
    @JsonProperty("APPROVE_STATE")
    private String APPROVE_STATE;
    @JsonProperty("APPROVE_DATE")
    private String APPROVE_DATE;
    @JsonProperty("SEND_DATE")
    private String SEND_DATE;
    @JsonProperty("REG_DW_USER_IDX")
    private int REG_DW_USER_IDX;
    @JsonProperty("UP_DW_USER_IDX")
    private int UP_DW_USER_IDX;
    @JsonProperty("SEND_USER_IDX")
    private int SEND_USER_IDX;
    @JsonProperty("FILE_NAME")
    private String FILE_NAME;
    @JsonProperty("FILE_PATH")
    private String FILE_PATH;
    @JsonProperty("FILE_TYPE")
    private String FILE_TYPE;
    @JsonProperty("POINT")
    private double POINT;
    @JsonProperty("FULL_POINT")
    private double FULL_POINT;
    @JsonProperty("REASON")
    private String REASON;
    @JsonProperty("WORK_USER_NAME")
    private String WORK_USER_NAME;
    @JsonProperty("EMRT")
    private String EMRT;
    @JsonProperty("CMRT")
    private String CMRT;
}
