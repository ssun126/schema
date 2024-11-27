package com.dongwoo.SQM.partMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class partDetailMsdsDTO {

    @JsonProperty("MSDS_IDX")
    private String MSDS_IDX;
    @JsonProperty("PM_IDX")
    private String PM_IDX;
    @JsonProperty("MSDS_REG_DATE")
    private String MSDS_REG_DATE;
    @JsonProperty("MSDS_LANG")
    private String MSDS_LANG;
    @JsonProperty("MSDS_APPROVAL_NUM")
    private String MSDS_APPROVAL_NUM;
    @JsonProperty("MSDS_FILE_NAME")
    private String MSDS_FILE_NAME;
    @JsonProperty("MSDS_FILE_PATH")
    private String MSDS_FILE_PATH;
    @JsonProperty("FILE_STATUS")
    private String FILE_STATUS;

    @JsonProperty("MSDS_PART_CODE")
    private String MSDS_PART_CODE;
    @JsonProperty("MSDS_PART_NAME")
    private String MSDS_PART_NAME;

    //저장 svhc 갈지 flag
    @JsonProperty("INFO_FLAG")
    private String INFO_FLAG;


}
