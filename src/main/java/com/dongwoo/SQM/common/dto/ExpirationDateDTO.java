package com.dongwoo.SQM.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ExpirationDateDTO {
    @JsonProperty("IDX")
    private int IDX;
    @JsonProperty("CODE1")
    private String CODE1;
    @JsonProperty("CODE2")
    private int CODE2;
    @JsonProperty("EXP_KIND")
    private String EXP_KIND;
    @JsonProperty("EXP_MONTH")
    private int EXP_MONTH;
    @JsonProperty("EXP_BODY")
    private String EXP_BODY;
    @JsonProperty("REG_DATE")
    private String REG_DATE;
    @JsonProperty("UP_DATE")
    private String UP_DATE;
    @JsonProperty("REG_DW_USER_IDX")
    private int REG_DW_USER_IDX;
    @JsonProperty("UP_DW_USER_IDX")
    private int UP_DW_USER_IDX;
    @JsonProperty("REG_DW_USER_NAME")
    private String REG_DW_USER_NAME;
    @JsonProperty("UP_DW_USER_NAME")
    private String UP_DW_USER_NAME;
}
