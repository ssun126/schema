package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoCompanyDTO {

   //USERINFOCOMPANY
   private int  USERIDX		;	//	--// 사용자 IDX       ▶
   private String COMCODE		;	//	--// 업체코드 (벤더코드) ★
   private int COMUSERIDX	;	//	--// 메인 업무자
   private String IDPWADDREASON;		//	--// ID/PW 추가 사유
   private String USERSTATUS	;	//	--// 관리상태 (0:대기, 1:검토중, 2:승인, 3:반려) ★
   private String USEROKDATE	;	//	--// 회원가입 승일일
   private String RETRUNREASON	;	//	--// 반려 사유 내용

}


