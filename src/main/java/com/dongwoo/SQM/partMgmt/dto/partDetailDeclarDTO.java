package com.dongwoo.SQM.partMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class partDetailDeclarDTO {

    @JsonProperty("DECL_IDX")
    private String DECL_IDX;
    @JsonProperty("PM_IDX")
    private String PM_IDX;
    @JsonProperty("DATA_GUBUN")
    private String DATA_GUBUN;
    @JsonProperty("CONFIRM_DATE")
    private String CONFIRM_DATE;
    @JsonProperty("FILE_PATH")
    private String FILE_PATH;
    @JsonProperty("FILE_NAME")
    private String FILE_NAME;
    @JsonProperty("APPLICABLE_NO")
    private String APPLICABLE_NO;
    @JsonProperty("NONE_APPLICABLE_NO")
    private String NONE_APPLICABLE_NO;
    @JsonProperty("DECL_REMARK")
    private String DECL_REMARK;

    @JsonProperty("WARRANTY_ITEM")
    private String WARRANTY_ITEM;
    @JsonProperty("CONFIRM_CHK")
    private String CONFIRM_CHK;

    @JsonProperty("SEND_FILE_PATH")
    private String SEND_FILE_PATH;
    @JsonProperty("SEND_FILE_NAME")
    private String SEND_FILE_NAME;

    @JsonProperty("INFO_FLAG")
    private String INFO_FLAG;
}
