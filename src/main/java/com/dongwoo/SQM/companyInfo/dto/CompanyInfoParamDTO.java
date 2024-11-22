package com.dongwoo.SQM.companyInfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CompanyInfoParamDTO {
    private Integer RN;

    @JsonProperty("COM_CODE")
    private String COM_CODE			    ; //  업체코드 (벤더코드) ★
    @JsonProperty("COM_NAME")
    private String  COM_NAME		    ; //  업체명 --검색필드 1

    @JsonProperty("COM_MANAGE_STATUS")
    private String  COM_MANAGE_STATUS		; //  관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)


    @JsonProperty("COM_AUDIT_POINT_FROM")
    private int COM_AUDIT_POINT_FROM;     //갬색
    @JsonProperty("COM_AUDIT_POINT_TO")
    private int COM_AUDIT_POINT_TO;      //검색

    @JsonProperty("MINERAL")
    private String  MINERAL	;         //   검색 광물 유무  아래 6종.

    private String TANTALUM_YN		; // Tantalum Y/N
    private String  TUNGSTEN_YN		; // Tungsten Y/N
    private String  TIN_YN			; //  Tin Y/N
    private String  GOLD_YN			; // Gold Y/N
    private String  COBALT_YN	    ; //  Cobalt Y/N
    private String  MICA_YN			; // Mica Y/N

    @JsonProperty("DEPT_CODE")
    private String  DEPT_CODE		; //검색 사업부
    
    private String  CP_CODE_WORK_CD1 ; //업체 사업부 코드1
    private String  CP_CODE_WORK_CD2 ; //업체 사업부 코드2
    private String  CP_CODE_WORK_CD3 ; //업체 사업부 코드3
    private String  CP_CODE_WORK_CD4 ; //업체 사업부 코드4
    private String  CP_CODE_WORK_CD5 ; //업체 사업부 코드5

    @JsonProperty("USER_STATUS")
    private String  USER_STATUS;  //관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)   //  처리상태  ★.

    @JsonProperty("BUS_NUMBER")
    private String  BUS_NUMBER;  //사업자등록번호
    @JsonProperty("ID_ADD_TYPE")
    private String  ID_ADD_TYPE; //ID 추가 정보 (0:신규, 1:추가)

    @JsonProperty("USER_IDX")
    private int USER_IDX;
}
