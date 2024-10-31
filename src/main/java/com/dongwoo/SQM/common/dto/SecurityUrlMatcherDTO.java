package com.dongwoo.SQM.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUrlMatcherDTO {
    private int PAGE_IDX;
    private String PAGE_NM;
    private String PAGE_URL;
    private int GNB_IDX;
    private String GNB_NM;
    private String AUTH_ROLE_NM;


}
