package com.dongwoo.SQM.auditMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuditMgmtDTO {
    @JsonProperty("COM_CODE")
    private String COM_CODE;
    @JsonProperty("COM_NAME")
    private String COM_NAME;
    @JsonProperty("AUTH_TYPE")
    private String AUTH_TYPE;
    @JsonProperty("AUTH_SEQ")
    private String AUTH_SEQ;
    @JsonProperty("APPROVE_STATE")
    private String APPROVE_STATE;
    @JsonProperty("APPROVE_DATE")
    private String APPROVE_DATE;
    @JsonProperty("REG_DW_USER_IDX")
    private int REG_DW_USER_IDX;
    @JsonProperty("UP_DW_USER_IDX")
    private int UP_DW_USER_IDX;
    @JsonProperty("FILE_NAME")
    private String FILE_NAME;
    @JsonProperty("FILE_PATH")
    private String FILE_PATH;
    @JsonProperty("FILE_TYPE")
    private String FILE_TYPE;
    @JsonProperty("POINT")
    private int POINT;
}
