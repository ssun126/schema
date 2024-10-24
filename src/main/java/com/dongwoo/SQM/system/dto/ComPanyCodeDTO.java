package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ComPanyCodeDTO {

   private String COMCODE			    ; //  업체코드 (벤더코드) ★   --검색필드 1
   private String  COMNAME		    ; //  업체명
   private String  COMNATION		; //  국가 (KOR...)     ★
   private String  USEYN			; // 구분 (Y:사용, N:미거래)	!!
   private int  REGDWUSERIDX	    ; // 등록자 IDX   IF 받고 최초는 대기 0  일때 IF
   private int  UPDWUSERIDX		    ; // 수정자 IDX   IF 받고 최초는 대기 0 일때 IF
   private String  REGDATE		    ; //  등록일
   private String  UPDATE			; // 수정일

   private String  VENDORWORKKIND	; // VENDOR 업종 형태 (D:제조사, L:물류사)       ★
   private int  COMUSERIDX		    ; // 메인 업무자
   private String  COMPANYNAME	    ; //  회사명 (한글 또는 해당국가 언어)             ★
   private String  FACTORYNAME	    ; //  제조 공장명
   private String  BUSNUMBER		; //  사업자등록번호                             ★  검색필드 1
   private String  COMADDRESS		; // 회사주소 (영문)
   private String  COMCEONAME		; // CEO 성명 (영문)
   private String  COMCEOPHONE	    ; //  CEO 연락처 (영문)
   private String  COMCEOEMAIL	    ; //  CEO 이메일
   private String  COMSTATUS		; //  관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)   ?? IF 받고 최초는 대기 0 인가 ?

   private String  COMAPPDATE		; // 워런티 협약상태 제출일
   private String  COMOKDATE		; //  워런티 협약상태 승일일 또는 반려일
   private String  COMFILENAME	    ; //  워런티 파일 이름
   private String  COMFILEPATH	    ; //  워런티 파일 Path

   private int  COMAUDITPOINT		; // Audit 점수
   private String  COMAUDITLEVEL	; //  Audit 등급

   private String TANTALUMYN		; // Tantalum Y/N
   private String  TUNGSTENYN		; // Tungsten Y/N
   private String  TINYN			; //  Tin Y/N
   private String  GOLDYN			; // Gold Y/N
   private String  COBALTYN		    ; //  Cobalt Y/N
   private String  MICAYN			; // Mica Y/N

}


