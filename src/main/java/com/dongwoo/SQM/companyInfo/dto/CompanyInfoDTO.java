package com.dongwoo.SQM.companyInfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
public class CompanyInfoDTO {
    private Integer RN;
    private String COM_CODE;
    private String COM_NAME;
    private String COM_NATION;
    private String COM_STATUS;
}
