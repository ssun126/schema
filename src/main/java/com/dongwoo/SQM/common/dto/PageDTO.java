package com.dongwoo.SQM.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageDTO {
    private String pageIdx                  = null;
    private String pageNm                   = null;
    private String pageUrl                  = null;

    private String gnbIdx                   = null;
    private String gnbNm                    = null;

    private String authRoleNm               = null;
}
