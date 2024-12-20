package com.dongwoo.SQM.auditMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuditItemPointDTO {
    @JsonProperty("AUDIT_ID")
    private String AUDIT_ID;
    @JsonProperty("COM_CODE")
    private String COM_CODE;
    @JsonProperty("AUTH_TYPE")
    private String AUTH_TYPE;
    @JsonProperty("AUTH_SEQ")
    private int AUTH_SEQ;
    @JsonProperty("POINT")
    private int POINT;
    @JsonProperty("COMMENT")
    private int COMMENT;
    @JsonProperty("REG_COM_USER_IDX")
    private int REG_COM_USER_IDX;
    @JsonProperty("REG_DATE")
    private int REG_DATE;
}
