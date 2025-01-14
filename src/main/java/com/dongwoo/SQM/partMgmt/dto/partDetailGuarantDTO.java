package com.dongwoo.SQM.partMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class partDetailGuarantDTO {

    @JsonProperty("GUARANT_IDX")
    private String GUARANT_IDX;
    @JsonProperty("PM_IDX")
    private String PM_IDX;
    @JsonProperty("GUARANT_CONFIRM_DATE")
    private String GUARANT_CONFIRM_DATE;
    @JsonProperty("GUARANT_TYPE")
    private String GUARANT_TYPE;
    @JsonProperty("GUARANT_FILE_NAME")
    private String GUARANT_FILE_NAME;
    @JsonProperty("GUARANT_FILE_PATH")
    private String GUARANT_FILE_PATH;

    @JsonProperty("GUARANT_REMARK")
    private String GUARANT_REMARK;

    @JsonProperty("FILE_STATUS")
    private String FILE_STATUS;
}
