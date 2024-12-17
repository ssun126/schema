package com.dongwoo.SQM.siteMgr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class QualityItemDTO {
    @JsonProperty("MAIN_ITEM")
    private String MAIN_ITEM;
    @JsonProperty("AUDIT_ID")
    private int AUDIT_ID;
    @JsonProperty("AUDIT_ITEM")
    private String AUDIT_ITEM;
}
