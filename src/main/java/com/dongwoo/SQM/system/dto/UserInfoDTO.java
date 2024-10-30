package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoDTO {

   //USERINFO
   private int USER_IDX;
   private String USER_ID;
   private String USER_PWD;  //비밀번호 (SHA 암호화)
   private String USER_NAME;
   private String USER_GUBN; //(0:동우화인켐, 1:업체)

   private String USER_STATUS; // 사용자 상태 (Y:사용, N:미사용)
   private String REG_DW_USER_IDX;
   private String UP_DW_USER_IDX;
   private String DEL_DW_USER_IDX;
   private String REG_DATE		;
   private String UP_DATE;
   private String DEL_DATE;
}


