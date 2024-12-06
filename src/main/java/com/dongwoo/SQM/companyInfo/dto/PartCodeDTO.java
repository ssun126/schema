package com.dongwoo.SQM.companyInfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PartCodeDTO {
    private Integer RN;
    @JsonProperty("COM_CODE")
    private String COM_CODE; // -- 업체코드 (벤더코드) ■
    @JsonProperty("COM_NAME")
    private String COM_NAME;
    @JsonProperty("COM_NATION")
    private String COM_NATION;     // --국가 ■
    @JsonProperty("COM_STATUS")
    private String COM_STATUS;


    @JsonProperty("DEPT_CODE")
    private String  DEPT_CODE		; //검색 사업부 CpWorkCode base_option 3100,4100,5100 ....
    //COMPANY_CODE_WORK
    @JsonProperty("BASE_CODE")
    private String BASE_CODE       ;// -- 기초코드 코드 (사업본부는 기초코드로 관리)  // --사업본부 ■

    @JsonProperty("PART_CODE")
    private String PART_CODE            ;// -- 자재코드  ■
    @JsonProperty("PART_NAME")
    private String PART_NAME            ;//  -- 자재명 ■
    @JsonProperty("PLANT_CODE")
    private String PLANT_CODE           ;// -- PLANT ■
    @JsonProperty("PART_STATUS")
    private String PART_STATUS          ;//  -- 사용 (D:신청, Y:사용, R:반려)
    private int REG_DW_USER_IDX      ;//-- 등록자 IDX
    private int UP_DW_USER_IDX      ;// -- 수정자 IDX
    private String REG_DATE             ;// -- 등록일
    private String UP_DATE              ;//  -- 수정일

    @JsonProperty("MAT_ID")
     private String MAT_ID ;//      -- 자재 코드
    @JsonProperty("MAT_NAME")
     private String MAT_NAME ;//     -- 자재명
    @JsonProperty("PLAND_ID")
     private String PLAND_ID ;//     -- 플랜트 코드
    @JsonProperty("PLAND_NAME")
     private String PLAND_NAME ;//,   -- 플랜트 명

    @JsonProperty("savetype")
    private String savetype ;//,   -- insert / update 구분
    
     private String IF_DATETIME ;//   -- 인터페이스 일시
     private String IF_SYSTEM ;// -- 인터페이스 시스템
    
    private Integer SEQ   ;// 'Sequence';
    private String FACTORY ;//  '공장코드';
    private String VENDOR_CODE ;//  '업체코드';
    private String INSP_ID ;//  '스펙 ID';
    private String INSP_NAME ;//  '스펙이름';
    private String INSP_ENG_NAME  ;// '스펙영문이름';
    private String UNIT_NAME ;//  '스펙유닛이름';
    private String LSL ;// '스펙 LSL';
    private String USL ;// '스펙 USL';
    private String INTERFACE_DATE ;//  '인터페이스 날짜';
    private String UPDATE_YN ;// '인터페이스 상태';
    private String COMPLETE_DATETIME ;//  '인터페이스 완료일시';
    private Integer ORDER_NO ;// '정렬순서';
    private String LCL ;// '관리선 LCL';
    private String UCL ;// '관리선 UCL';
    
}
