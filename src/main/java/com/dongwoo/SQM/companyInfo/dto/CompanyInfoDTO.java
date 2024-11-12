package com.dongwoo.SQM.companyInfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
public class CompanyInfoDTO {
    private Integer RN;

    private String COM_CODE			    ; //  업체코드 (벤더코드) ★
    private String  COM_NAME		    ; //  업체명 --검색필드 1

    private String  COM_NATION		    ; // 국가 (KOR...)     ★
    private String  COM_STATUS			; // 구분 (Y:사용, N:미거래)
    private int  REG_DW_USER_IDX	    ; // 등록자 IDX   IF 받고 최초는 대기 0  일때 IF
    private int  UP_DW_USER_IDX		    ; // 수정자 IDX   IF 받고 최초는 대기 0 일때 IF
    private String  REG_DATE		    ; // 등록일
    private String  UP_DATE			    ; // 수정일

    private String  VENDOR_WORK_KIND	; // VENDOR 업종 형태 (D:제조사, L:물류사)       ★
    private int  COM_USER_IDX		    ; // 메인 업무자
    private String  COMPANY_NAME	    ; // 회사명 (한글 또는 해당국가 언어)            ★
    private String  FACTORY_NAME	    ; // 제조 공장명
    private String  BUS_NUMBER		    ; // 사업자등록번호                            ★
    private String  COM_ADDRESS		    ; // 회사주소 (영문)
    private String  COM_CEO_NAME		; // CEO 성명 (영문)
    private String  COM_CEO_PHONE	    ; // CEO 연락처 (영문)
    private String  COM_CEO_EMAIL	    ; // CEO 이메일

    private String  COM_MANAGE_STATUS		; //  관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)

    private String  COM_APP_DATE		; // 워런티 협약상태 제출일
    private String  COM_OK_DATE		    ; // 워런티 협약상태 승일일 또는 반려일
    private String  COM_FILE_NAME	    ; // 워런티 파일 이름
    private String  COM_FILE_PATH	    ; // 워런티 파일 Path

    private int  COM_AUDIT_POINT		; // Audit 점수
    private int COM_AUDIT_POINT_FROM;     //갬색
    private int COM_AUDIT_POINT_TO;      //검색

    private String  COM_AUDIT_LEVEL	;     //  Audit 등급

    private String  MINERAL	;         //   검색 광물 유무  아래 6종.

    private String TANTALUM_YN		; // Tantalum Y/N
    private String  TUNGSTEN_YN		; // Tungsten Y/N
    private String  TIN_YN			; //  Tin Y/N
    private String  GOLD_YN			; // Gold Y/N
    private String  COBALT_YN	    ; //  Cobalt Y/N
    private String  MICA_YN			; // Mica Y/N

    private String  DEPT_CODE		; //검색 사업부
    
    private String  CP_CODE_WORK_CD1 ; //업체 사업부 코드1
    private String  CP_CODE_WORK_CD2 ; //업체 사업부 코드2
    private String  CP_CODE_WORK_CD3 ; //업체 사업부 코드3
    private String  CP_CODE_WORK_CD4 ; //업체 사업부 코드4
    private String  CP_CODE_WORK_CD5 ; //업체 사업부 코드5

  private String  ID_PW_ADD_REASON;
  private String  USER_STATUS;  //관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)   //  처리상태  ★.
  private String  USER_OK_DATE;

}
