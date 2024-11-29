package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserInfoCompanyDTO {

   //SC_USER_INFO_COMPANY
   private int  USER_IDX		;	//	--// 사용자 IDX       ▶
   private String COM_CODE		;	//	--// 업체코드 (벤더코드) ★
   private int COM_USER_IDX	;	//	--// 메인 업무자
   private String ID_PW_ADD_REASON;		//	--// ID/PW 추가 사유
   private String USER_STATUS	;	//	--// 관리상태 (0:대기, 1:검토중, 2:승인, 3:반려) ★
   private String USER_OK_DATE	;	//	--// 회원가입 승일일
   private String RETURN_REASON	;	//	--// 반려 사유 내용
   private String ID_ADD_TYPE	;	//	--//ID 추가 정보 (0:신규, 1:추가)

}


