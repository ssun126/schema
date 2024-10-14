package com.dongwoo.SQM.system.login.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginDTO {
    private int USER_idx;
    private String USER_Id;
    private String USER_Email;
    private String USER_Password;
    private String USER_Name;
    private String USER_Mobile;
}
