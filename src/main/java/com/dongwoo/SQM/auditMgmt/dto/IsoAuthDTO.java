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
    @JsonProperty("REG_DW_USER_IDX")
    private int REG_DW_USER_IDX;
    @JsonProperty("UP_DW_USER_IDX")
    private int UP_DW_USER_IDX;
    @JsonProperty("DEL_DW_USER_IDX")
    private int DEL_DW_USER_IDX;
    @JsonProperty("SEND_DATE")
    private String SEND_DATE;
    @JsonProperty("UP_DATE")
    private String UP_DATE;
    @JsonProperty("DEL_DATE")
    private String DEL_DATE;
    private int POINT;
}
