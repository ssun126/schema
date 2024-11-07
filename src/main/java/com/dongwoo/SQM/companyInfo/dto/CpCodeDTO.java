package com.dongwoo.SQM.companyInfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.Data;

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
}
