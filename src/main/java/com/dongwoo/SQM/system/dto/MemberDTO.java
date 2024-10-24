package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {

    private String REGDATE	;
    private String UPDATE	;
    private String DELDATE	;

    private int USER_idx;
    private String USER_ID;
    private String USER_EMAIL;
    private String PASSWORD;
    private String USER_NAME;
    private String USER_Mobile;


    private int USERIDX;
    private String USERID;
    private String USERPWD;  //비밀번호 (SHA 암호화)
    private String USERNAME;
    private String USERGUBN; //(0:동우화인켐, 1:업체)

    private String USERSTATUS; // 사용자 상태 (Y:사용, N:미사용)
    private String REGDWUSERIDX;
    private String UPDWUSERIDX;
    private String DELDWUSERIDX;


    private String USERPOSITION;
    private String USERDEPT;
    private String USEREMAIL;
    private String USERPHONE;
    private String USERSELECT;  //선택

    private String COMCODE;  // 업체코드 (벤더코드) ★
    private int COMUSERIDX;

    private String IDPWADDREASON;
    private String USEROKDATE;
    private String RETRUNREASON;

    private String COMNAME	;
    private String COMNATION;
    private String USEYN	;

    private String VENDORWORKKIND;

    private String COMPANYNAME	;
    private String FACTORYNAME	;
    private String BUSNUMBER	;
    private String COMADDRESS	;
    private String COMCEONAME	;
    private String COMCEOPHONE	;
    private String COMCEOEMAIL	;
    private String COMSTATUS	;
    private String COMAPPDATE	;
    private String COMOKDATE	;
    private String COMFILENAME	;
    private String COMFILEPATH	;
    private int COMAUDITPOINT		;
    private String COMAUDITLEVEL;
    private String TANTALUMYN;
    private String TUNGSTENYN;
    private String TINYN	;
    private String GOLDYN	;
    private String COBALTYN	;
    private String MICAYN	;

}

