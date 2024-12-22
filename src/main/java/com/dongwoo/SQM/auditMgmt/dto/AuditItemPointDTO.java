package com.dongwoo.SQM.auditMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuditItemPointDTO {
    @JsonProperty("AUDIT_ID")
    private String AUDIT_ID;
    @JsonProperty("PROVISION")
    private String PROVISION;
    @JsonProperty("AUDIT_ITEM")
    private String AUDIT_ITEM;
    @JsonProperty("AUDIT_DESC")
    private String AUDIT_DESC;
    @JsonProperty("AUDIT_CRITERIA")
    private String AUDIT_CRITERIA;
    @JsonProperty("POINT_CRITERIA")
    private String POINT_CRITERIA;
    @JsonProperty("COM_CODE")
    private String COM_CODE;
    @JsonProperty("AUTH_TYPE")
    private String AUTH_TYPE;
    @JsonProperty("AUTH_SEQ")
    private int AUTH_SEQ;
    @JsonProperty("POINT")
    private int POINT;
    @JsonProperty("AUDIT_COMMENT")
    private String AUDIT_COMMENT;
    @JsonProperty("REG_COM_USER_IDX")
    private int REG_COM_USER_IDX;
    @JsonProperty("REG_DATE")
    private String REG_DATE;
}
