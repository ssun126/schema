package com.dongwoo.SQM.auditMgmt.dto;

import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class IsoAuthDTO {
    @JsonProperty("COM_CODE")
    private String COM_CODE;
    @JsonProperty("COM_NAME")
    private String COM_NAME;
    @JsonProperty("AUTH_TYPE")
    private String AUTH_TYPE;
    @JsonProperty("APPROVE_STATE")
    private String APPROVE_STATE;
    @JsonProperty("APPROVE_DATE")
    private String APPROVE_DATE;
    @JsonProperty("SEND_DATE")
    private String SEND_DATE;
    private int POINT;
}
