package com.dongwoo.SQM.siteMgr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseCodeDTO {
    private int CODEID;
    private String BIZSECTION;
    private String CODEGROUP;
    private String CODE;
    private String CODENAME;
    private String MULTICODENAME;
    private String SIMPLECODENAME;
    private int SORTKEY;
    private String REGISTERCODE;
    private String MODIFIERCODE;
    private String REGISTDATE;
    private String MODIFYDATE;
}
