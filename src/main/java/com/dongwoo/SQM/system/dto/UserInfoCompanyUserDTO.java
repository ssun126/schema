package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoCompanyUserDTO {

   //USERINFOCOMPANYUSER
   private int  COMUSERIDX;    //공동 사용자 idx
   private int  USERIDX		;   //로그인 사용자.
   private String  COMCODE	;    // 업체코드 (벤더코드)
   private String  USERNAME	;
   private String  USERPOSITION;
   private String  USERDEPT		;
   private String  USEREMAIL	;
   private String  USERPHONE;

}


