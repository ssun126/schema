package com.dongwoo.SQM.siteMgr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SvhcListDTO {
    @JsonProperty("SVHC_NUM")
    private String SVHC_NUM;
    @JsonProperty("SVHC_NAME")
    private String SVHC_NAME;
    @JsonProperty("SVHC_CASNUM")
    private String SVHC_CASNUM;
    @JsonProperty("SVHC_EUNUM")
    private String SVHC_EUNUM;

    @JsonProperty("SVHC_YN")
    private String SVHC_YN;



}
