package com.dongwoo.SQM.siteMgr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LabourItemDTO {
    @JsonProperty("ITEM_IDX")
    private int ITEM_IDX;
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

}
