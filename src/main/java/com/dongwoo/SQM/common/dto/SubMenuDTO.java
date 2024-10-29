package com.dongwoo.SQM.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubMenuDTO {
    private int PAGE_IDX;
    private String PAGE_NM;
    private String PAGE_URL;
    private int GNB_IDX;
    private String GNB_NM;
    private String AUTH_ROLE_NM;
}
