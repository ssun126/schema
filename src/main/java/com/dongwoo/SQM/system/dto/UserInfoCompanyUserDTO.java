package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoCompanyUserDTO {

   //USER_INFO_COMPANY_USER
   private int  COM_USER_IDX;    //공동 사용자 idx
   private int  USER_IDX		;   //로그인 사용자.
   private String  USER_ID		;   //로그인 사용자.
   private String  COM_CODE	;    // 업체코드 (벤더코드)
   private String  USER_NAME	;
   private String  USER_POSITION;
   private String  USER_DEPT	;
   private String  USER_EMAIL	;
   private String  USER_PHONE   ;
   private String  MAIN_USER_NAME    ;   //업체별 메인 담당자
   private int  MAIN_COM_USER_IDX    ;   //업체별 메인 담당자

   private String  MAIN_USER_YN   ;
   private String  USER_STATUS    ;  //관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)

}


