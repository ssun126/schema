package com.dongwoo.SQM.auditMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IsoAuthItemDTO {
    private Integer RN;
    @JsonProperty("COM_CODE")
    private String COM_CODE;
    @JsonProperty("COM_NAME")
    private String COM_NAME;
    @JsonProperty("AUTH_TYPE")
    private String AUTH_TYPE;
    @JsonProperty("AUTH_CODE")
    private String AUTH_CODE;
    @JsonProperty("AUTH_DATE")
    private String AUTH_DATE;
    @JsonProperty("EXP_DATE")
    private String EXP_DATE;
    @JsonProperty("REG_DATE")
    private String REG_DATE;
    @JsonProperty("ITEM_STATE")
    private String ITEM_STATE;
}
