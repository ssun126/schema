package com.dongwoo.SQM.auditMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SafetyHealthDTO {
    @JsonProperty("AUTH_SEQ")
    private String AUTH_SEQ;
    @JsonProperty("COM_CODE")
    private String COM_CODE;
    @JsonProperty("REG_INPUT_DATE")
    private String REG_INPUT_DATE;
    @JsonProperty("REG_DATE")
    private String REG_DATE;
    @JsonProperty("UP_DATE")
    private String UP_DATE;
    @JsonProperty("DEL_DATE")
    private String DEL_DATE;
    @JsonProperty("REG_DW_USER_IDX")
    private int REG_DW_USER_IDX;
    @JsonProperty("UP_DW_USER_IDX")
    private int UP_DW_USER_IDX;
    @JsonProperty("DEL_DW_USER_IDX")
    private int DEL_DW_USER_IDX;
    @JsonProperty("FILE_NAME")
    private String FILE_NAME; // 파일 이름
    @JsonProperty("FILE_PATH")
    private String FILE_PATH; // 파일이 저장된 경로
}
