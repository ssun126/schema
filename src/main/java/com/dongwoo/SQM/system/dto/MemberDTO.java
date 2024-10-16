package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
    private int USER_idx;
    private String USER_ID;
    private String USER_EMAIL;
    private String PASSWORD;
    private String USER_NAME;
    private String USER_Mobile;
}
