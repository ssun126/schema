package com.dongwoo.SQM.siteMgr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QualityItemDTO {
    @JsonProperty("PROVISION")
    private String PROVISION;
    @JsonProperty("AUDIT_ID")
    private String AUDIT_ID;
    @JsonProperty("AUDIT_ITEM")
    private String AUDIT_ITEM;
    @JsonProperty("AUDIT_CRITERIA")
    private String AUDIT_CRITERIA;
    @JsonProperty("POINT_CRITERIA")
    private String POINT_CRITERIA;
    @JsonProperty("DIVISION1")
    private String DIVISION1;
    @JsonProperty("DIVISION2")
    private String DIVISION2;
}
