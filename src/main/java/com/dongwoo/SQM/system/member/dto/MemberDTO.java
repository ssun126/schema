package com.dongwoo.SQM.system.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
    private int USER_idx;
    private String USER_Id;
    private String USER_Email;
    private String USER_Password;
    private String USER_Name;
    private String USER_Mobile;
}
