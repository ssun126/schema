package com.dongwoo.SQM.siteMgr.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class BaseConfigDTO {
    private int IDX;
    private String GUBN;
    private String CONFIGCODE;
    private String CONFIGVALUE;
    private String CONFIGSUMMARY;
    private String CONFIGSTATUS;
    private String REGISTERCODE;
    private String MODIFIERCODE;
    private String REGDATE;
    private String MODIFIDATE;
    private String USERNAME;

}
