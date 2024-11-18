package com.dongwoo.SQM.system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoCompanyUserDTO {

   //USER_INFO_COMPANY_USER
   @JsonProperty("COM_USER_IDX")
   private int  COM_USER_IDX;    //공동 사용자 idx (메인사용자.)
   @JsonProperty("USER_IDX")
   private int  USER_IDX		;   //로그인 사용자.
   @JsonProperty("USER_ID")
   private String  USER_ID		;   //로그인 사용자.
   @JsonProperty("COM_CODE")
   private String  COM_CODE	;    // 업체코드 (벤더코드)
   @JsonProperty("USER_NAME")
   private String  USER_NAME	;
   @JsonProperty("USER_POSITION")
   private String  USER_POSITION;
   @JsonProperty("USER_DEPT")
   private String  USER_DEPT	;
   @JsonProperty("USER_EMAIL")
   private String  USER_EMAIL	;
   @JsonProperty("USER_PHONE")
   private String  USER_PHONE   ;
   @JsonProperty("MAIN_USER_NAME")
   private String  MAIN_USER_NAME    ;   //업체별 메인 담당자
   @JsonProperty("MAIN_COM_USER_IDX")
   private int  MAIN_COM_USER_IDX    ;   //업체별 메인 담당자
   @JsonProperty("MAIN_USER_YN")
   private String  MAIN_USER_YN   ;
   @JsonProperty("USER_STATUS")
   private String  USER_STATUS    ;  //관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)

}


