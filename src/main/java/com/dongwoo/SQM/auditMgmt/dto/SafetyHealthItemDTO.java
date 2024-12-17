package com.dongwoo.SQM.auditMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SafetyHealthItemDTO {
    @JsonProperty("ITEM_IDX")
    private String ITEM_IDX;
    @JsonProperty("AUDIT_ID")
    private String AUDIT_ID;
    @JsonProperty("PROVISION")
    private String PROVISION;
    @JsonProperty("AUDIT_ITEM")
    private String AUDIT_ITEM;
    @JsonProperty("AUDIT_CRITERIA")
    private String AUDIT_CRITERIA;
    @JsonProperty("GIVEN_POINT")
    private String GIVEN_POINT;
    @JsonProperty("POINT_CRITERIA")
    private String POINT_CRITERIA;
    @JsonProperty("DIVISION1")
    private String DIVISION1;
    @JsonProperty("DIVISION2")
    private String DIVISION2;
    @JsonProperty("MANAGER")
    private String MANAGER;
    @JsonProperty("SUPPORT_DATA")
    private String SUPPORT_DATA;
    @JsonProperty("POINT")
    private int POINT;
}
