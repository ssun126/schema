package com.dongwoo.SQM.companyInfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Data;

import java.util.List;

@Data
public class CpCodeDTO {
    private Integer RN;
    @JsonProperty("COM_CODE")
    private String COM_CODE;
    @JsonProperty("COM_NAME")
    private String COM_NAME;
    @JsonProperty("COM_NATION")
    private String COM_NATION;
    @JsonProperty("COM_STATUS")
    private String COM_STATUS;
    @JsonProperty("DEPT_CODES")
    private String DEPT_CODES; // 설정된 사업본부
    @JsonProperty("DEPT_CODE")
    private List<String> DEPT_CODE; // 설정된 사업본부
    @JsonProperty("MODE")
    private String MODE;
    @JsonProperty("REG_DW_USER_IDX")
    private int REG_DW_USER_IDX;
    @JsonProperty("UP_DW_USER_IDX")
    private int UP_DW_USER_IDX;
}
