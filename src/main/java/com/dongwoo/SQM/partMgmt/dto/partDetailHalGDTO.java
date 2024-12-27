package com.dongwoo.SQM.partMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class partDetailHalGDTO {

    @JsonProperty("HALOGEN_IDX")
    private String HALOGEN_IDX;
    @JsonProperty("PM_IDX")
    private String PM_IDX;
    @JsonProperty("HALOGEN_CONFIRM_DATE")
    private String HALOGEN_CONFIRM_DATE;
    @JsonProperty("HALOGEN_CL")
    private String HALOGEN_CL;
    @JsonProperty("HALOGEN_BR")
    private String HALOGEN_BR;
    @JsonProperty("HALOGEN_CLBR")
    private String HALOGEN_CLBR;
    @JsonProperty("HALOGEN_F")
    private String HALOGEN_F;
    @JsonProperty("HALOGEN_I")
    private String HALOGEN_I;
    @JsonProperty("HALOGEN_FILE_NAME")
    private String HALOGEN_FILE_NAME;
    @JsonProperty("HALOGEN_FILE_PATH")
    private String HALOGEN_FILE_PATH;

    @JsonProperty("HALOGEN_FILE_STATUS")
    private String HALOGEN_FILE_STATUS;
}
