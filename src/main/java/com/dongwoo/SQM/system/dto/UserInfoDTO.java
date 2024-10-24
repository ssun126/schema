package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoDTO {

   //USERINFO
   private int USERIDX;
   private String USERID;
   private String USERPWD;  //비밀번호 (SHA 암호화)
   private String USERNAME;
   private String USERGUBN; //(0:동우화인켐, 1:업체)

   private String USERSTATUS; // 사용자 상태 (Y:사용, N:미사용)
   private String REGDWUSERIDX;
   private String UPDWUSERIDX;
   private String DELDWUSERIDX;
   private String REGDATE		;
   private String UPDATE;
   private String DELDATE;
}


