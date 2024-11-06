package com.dongwoo.SQM.siteMgr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserMgrDTO {
    private int BOARD_IDX;
    private String INPUT_USER_ID;
    private String BOARD_PASS;
    private String BOARD_TITLE;
    private String BOARD_DESC;
    private int BOARD_HITS;
    private String INPUT_DATE;
    private int ATTACHED_FILE;

    private String INFO_FLAG        ;    //insert update type

    //USER_INFO
    private int USER_IDX        ;    //-- 사용자 IDX
    private String USER_ID      ;    //-- 아이디 ▼
    private String USER_PWD     ;    //-- 비밀번호 (SHA 암호화)
    private String USER_PWD_NEW     ;    //-- 신규 비밀번호 (SHA 암호화)
    private String USER_NAME    ;    //-- 이름 ▼
    private int USER_GUBN       ;    //-- 사용자 구분 (0:동우화인켐, 1:업체)
    private String USER_STATUS  ;    //-- 사용자 상태 (Y:사용, N:미사용)
    private int REG_DW_USER_IDX ;    //-- 등록자 IDX
    private int UP_DW_USER_IDX ;    //-- 수정자 IDX
    private int DEL_DW_USER_IDX ;    //-- 삭제자 IDX
    private String REG_DATE     ;    //-- 등록일
    private String UP_DATE      ;    //-- 수정일
    private String DEL_DATE     ;    //-- 삭제일

   //USER_INFO_DW
   //private int USER_IDX;                     //-- 사용자 IDX
   private String DEPT_CODE;                 //-- 기초코드 INDEX (사업본부는 기초코드로 관리) ▼
   private String EMAIL;                     //-- 이메일 ▼
   private String USER_LEVEL;                //-- (100:관리자, 1:사용자)  ▼
   private String MANAGE_SYSTEM_YN;          //-- SYSTEM (Y:사용, N:미사용) ▼

   private String MANAGE_ISO_YN_01;          //-- ISO/노동환경  □
   private String MANAGE_ISO_YN_02;          //-- ISO/환경안전(평택공장) □
   private String MANAGE_ISO_YN_03;          //-- ISO/환경안전(신흥공장) □
   private String MANAGE_ISO_YN_04;          //-- ISO/환경안전(삼기공장) □
   private String MANAGE_ISO_YN_05;          //-- ISO/품질체계/분쟁.책임광물(반도체) □
   private String MANAGE_ISO_YN_06;          //-- ISO/품질체계/분쟁.책임광물(첨단소재) □
   private String MANAGE_ISO_YN_07;          //-- ISO/품질체계/분쟁.책임광물(첨단-삼기) □
   private String MANAGE_ISO_YN_08;          //-- ISO/품질체계/분쟁.책임광물(옵티칼) □
   private String MANAGE_ISO_YN_09;          //-- ISO/품질체계/분쟁.책임광물(통신디바이스) □

   private String MANAGE_PART_YN;            //-- PART 관리자 □
   private String MANAGE_COA_YN_01;          //-- COA 반도체 □
   private String MANAGE_COA_YN_02;          //-- COA 첨단소재 □
   private String MANAGE_COA_YN_03;          //-- COA 첨단-삼기 □
   private String MANAGE_COA_YN_04;          //-- COA 통신디바이스 □

   private String MANAGE_CHANGE_YN_01;       //-- 변경점관리 반도체 □
   private String MANAGE_CHANGE_YN_02;       //-- 첨단소재 □
   private String MANAGE_CHANGE_YN_03;       //-- 첨단-삼기 □
   private String MANAGE_CHANGE_YN_04;       //-- 옵티칼 □
   private String MANAGE_CHANGE_YN_05;       //-- 통신디바이스 □

   private String ALARM_AUDIT_YN;            //-- 알람설정 AUDIT MANAGEMENT □
   private String ALARM_PART_YN;             //-- 알람설정 PART MANAGEMENT □
   private String ALARM_COA_CHANGE_YN;       //-- COA 관리 / 변경점 관리 □

}
