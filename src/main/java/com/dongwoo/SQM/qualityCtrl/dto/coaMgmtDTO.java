package com.dongwoo.SQM.qualityCtrl.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Data
public class coaMgmtDTO {
//master
    @JsonProperty("COA_ID")
    private String COA_ID ;
    @JsonProperty("VENDOR_ID")
    private String VENDOR_ID;
    @JsonProperty("VENDOR_NAME")
    private String VENDOR_NAME;
    @JsonProperty("MATERIAL_ID")
    private String MATERIAL_ID;
    @JsonProperty("MATERIAL_NAME")
    private String MATERIAL_NAME;

    @JsonProperty("FACTORY_ID")
    private String FACTORY_ID;
    @JsonProperty("FACTORY_NAME")
    private String FACTORY_NAME;
    @JsonProperty("LOT_NO")
    private String LOT_NO;
    @JsonProperty("QUANTITY")
    private String QUANTITY;
    @JsonProperty("VENDOR_COMMENT")
    private String VENDOR_COMMENT;

    @JsonProperty("STOCK_DATE")
    private String STOCK_DATE;  //출하일
    @JsonProperty("MF_DATE")
    private String MF_DATE;  //제조일
    @JsonProperty("E_DATE")
    private String E_DATE;   //만료일

    @JsonProperty("COA_STATUS")
    private String COA_STATUS;
    @JsonProperty("COA_STATUS_STR")
    private String COA_STATUS_STR;

    @JsonProperty("WETPR")
    private String WETPR;

    @JsonProperty("SPEC_IN")
    private String SPEC_IN;

    @JsonProperty("ETC")
    private String ETC;
    @JsonProperty("IF_SEND_DATETIME")
    private String IF_SEND_DATETIME;
    @JsonProperty("RETURN_DESC")
    private String RETURN_DESC;

    @JsonProperty("CREATOR")
    private String CREATOR;
    @JsonProperty("CREATOR_NAME")
    private String CREATOR_NAME;

    @JsonProperty("CREATE_DATETIME")
    private String CREATE_DATETIME;

    @JsonProperty("IF_USER")
    private String IF_USER;
    @JsonProperty("IF_DATETIME")
    private String IF_DATETIME;

    @JsonProperty("MODIFIER")
    private String MODIFIER;
    @JsonProperty("MODIFY_TIME")
    private String MODIFY_TIME;
    @JsonProperty("LANGUAGE_TYPE")
    private String LANGUAGE_TYPE;

//    Detail
    @JsonProperty("SPEC_ID")
    private String SPEC_ID;
    @JsonProperty("SPEC_NAME")
    private String SPEC_NAME;
    @JsonProperty("SPEC_ENG_NAME")
    private String SPEC_ENG_NAME;
    @JsonProperty("SPEC_LSL")
    private String SPEC_LSL;
    @JsonProperty("SPEC_USL")
    private String SPEC_USL;
    @JsonProperty("RESULT_VALUE")
    private String RESULT_VALUE;
    @JsonProperty("IS_SPECIN")
    private String IS_SPECIN;

    @JsonProperty("SPEC_AREA")
    private String SPEC_AREA;
    @JsonProperty("SPEC_AREA2")
    private String SPEC_AREA2;

    @JsonProperty("IS_SPEC_YN")
    private String IS_SPEC_YN;

    @JsonProperty("IS_CONTROL_YN")
    private String IS_CONTROL_YN;

    //Search
    @JsonProperty("fromDate")
    private String fromDate;
    @JsonProperty("toDate")
    private String toDate;
    @JsonProperty("dlvFromDate")
    private String dlvFromDate;
    @JsonProperty("dlvToDate")
    private String dlvToDate;
    @JsonProperty("coaNumber")
    private String coaNumber;
    @JsonProperty("registerName")
    private String registerName;
    @JsonProperty("registerId")
    private String registerId;

    @JsonProperty("vendorName")
    private String vendorName;
    @JsonProperty("vendorNameList")
    private String vendorNameList;

    @JsonProperty("materialName")
    private String materialName;
    @JsonProperty("coaStatus")
    private String coaStatus;
    @JsonProperty("lotNo")
    private String lotNo;
    @JsonProperty("factoryId")
    private String factoryId;


    @JsonProperty("TOKEN_USER_ID")
    private String TOKEN_USER_ID;
    @JsonProperty("TOKEN_USER_TYPE")
    private String TOKEN_USER_TYPE;
    @JsonProperty("TOKEN_USER_LANG")
    private String TOKEN_USER_LANG;
    @JsonProperty("TOKEN_SITE_ID")
    private String TOKEN_SITE_ID;


}
