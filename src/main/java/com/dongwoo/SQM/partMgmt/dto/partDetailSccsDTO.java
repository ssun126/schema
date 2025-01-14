package com.dongwoo.SQM.partMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class partDetailSccsDTO {

    @JsonProperty("SCCS_IDX")
    private String SCCS_IDX;
    @JsonProperty("PM_IDX")
    private String PM_IDX;
    @JsonProperty("SCCS_CONFIRM_DATE")
    private String SCCS_CONFIRM_DATE;
    @JsonProperty("SCCS_CHAR")
    private String SCCS_CHAR;       //SCCS 구분
    @JsonProperty("SCCS_FILE_NAME")
    private String SCCS_FILE_NAME;
    @JsonProperty("SCCS_FILE_PATH")
    private String SCCS_FILE_PATH;

    @JsonProperty("FILE_STATUS")
    private String FILE_STATUS;

    @JsonProperty("SCCS_CONFIRM_CHK")
    private String SCCS_CONFIRM_CHK;

    @JsonProperty("INFO_FLAG")
    private String INFO_FLAG;
}
