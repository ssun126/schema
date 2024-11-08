package com.dongwoo.SQM.auditMgmt.dto;

import lombok.Data;

@Data
public class IsoAuthDTO {
    private String COM_CODE;
    private String APPROVE_STATE;
    private String APPROVE_DATE;
    private String SEND_DATE;
    private int SCORE;
}
