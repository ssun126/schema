package com.dongwoo.SQM.partMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class partDetailRohsDTO {

    @JsonProperty("ROHS_IDX")
    private String ROHS_IDX;
    @JsonProperty("PM_IDX")
    private String PM_IDX;
    @JsonProperty("ROHS_CONFIRM_DATE")
    private String ROHS_CONFIRM_DATE;
    @JsonProperty("ROHS_CD")
    private String ROHS_CD;
    @JsonProperty("ROHS_HG")
    private String ROHS_HG;
    @JsonProperty("ROHS_PB")
    private String ROHS_PB;
    @JsonProperty("ROHS_CR6")
    private String ROHS_CR6;
    @JsonProperty("ROHS_PBBS")
    private String ROHS_PBBS;
    @JsonProperty("ROHS_PBDES")
    private String ROHS_PBDES;
    @JsonProperty("ROHS_DEHP")
    private String ROHS_DEHP;
    @JsonProperty("ROHS_BBP")
    private String ROHS_BBP;
    @JsonProperty("ROHS_DBP")
    private String ROHS_DBP;
    @JsonProperty("ROHS_DIBP")
    private String ROHS_DIBP;
    @JsonProperty("ROHS_FILE_NAME")
    private String ROHS_FILE_NAME;
    @JsonProperty("ROHS_FILE_PATH")
    private String ROHS_FILE_PATH;

    @JsonProperty("ROHS_FILE_STATUS")
    private String ROHS_FILE_STATUS;

    @JsonProperty("ROHS_CONFIRM_CHK")
    private String ROHS_CONFIRM_CHK;


}
