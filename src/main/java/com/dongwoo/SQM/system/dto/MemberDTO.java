package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {

    private String REG_DATE	;
    private String UPDATE	;
    private String DEL_DATE	;

   // private int USER_idx;
    private String USER_ID;
    private String USER_EMAIL;
    private String PASSWORD;
    //private String USER_NAME;
    private String USER_Mobile;

    private int USER_IDX;
    //private String USERID;
    private String USER_PWD;  //비밀번호 (SHA 암호화)
    private String USER_NAME;
    private String USER_GUBN; //(0:동우화인켐, 1:업체)

    private String USER_STATUS; // 사용자 상태 (Y:사용, N:미사용)
    private String REG_DW_USER_IDX;
    private String UP_DW_USER_IDX;
    private String DEL_DW_USER_IDX;

    private String USER_POSITION;
    private String USER_DEPT;
    //private String USER_EMAIL;
    private String USER_PHONE;
    private String USERSELECT;  //선택

    private String COM_CODE;  // 업체코드 (벤더코드) ★
    private int COM_USER_IDX;

    private String ID_PW_ADD_REASON;
    private String USER_OK_DATE;
    private String RETURN_REASON;

    private String COM_NAME	;
    private String COM_NATION;
    private String COM_STATUS	;

    private String VENDOR_WORK_KIND;

    private String COMPANY_NAME	;
    private String FACTORY_NAME	;
    private String BUS_NUMBER	;
    private String COM_ADDRESS	;
    private String COM_CEO_NAME	;
    private String COM_CEO_PHONE	;
    private String COM_CEO_EMAIL	;

    private String COM_MANAGE_STATUS	;
    private String COM_APP_DATE	;
    private String COM_OK_DATE	;
    private String COM_FILE_NAME	;
    private String COM_FILE_PATH	;

    private int COM_AUDIT_POINT	;
    private String COM_AUDIT_LEVEL;

    private String TANTALUM_YN;
    private String TUNGSTEN_YN;
    private String TIN_YN	;
    private String GOLD_YN	;
    private String COBALT_YN	;
    private String MICA_YN	;

}

