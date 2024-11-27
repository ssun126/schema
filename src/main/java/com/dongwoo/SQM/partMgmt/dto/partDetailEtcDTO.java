package com.dongwoo.SQM.partMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class partDetailEtcDTO {


    @JsonProperty("ETC_IDX")
    private String ETC_IDX;
    @JsonProperty("PM_IDX")
    private String PM_IDX;
    @JsonProperty("ETC_CONFIRM_DATE")
    private String ETC_CONFIRM_DATE;
    @JsonProperty("ETC_ANALYSE_ENTRY")
    private String ETC_ANALYSE_ENTRY;
    @JsonProperty("ETC_ANALYSE_RESULT")
    private String ETC_ANALYSE_RESULT;
    @JsonProperty("ETC_FILE_NAME")
    private String ETC_FILE_NAME;
    @JsonProperty("ETC_FILE_PATH")
    private String ETC_FILE_PATH;

    @JsonProperty("FILE_STATUS")
    private String FILE_STATUS;

}
