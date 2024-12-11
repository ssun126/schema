package com.dongwoo.SQM.siteMgr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeclarationDTO {

    @JsonProperty("DECL_IDX")
    private int DECL_IDX;
    @JsonProperty("DECL_NUM")
    private String DECL_NUM;
    @JsonProperty("DECL_SUB_NUM")
    private String DECL_SUB_NUM;
    @JsonProperty("DECL_NAME")
    private String DECL_NAME;
    @JsonProperty("DECL_CASNUM")
    private String DECL_CASNUM;
    @JsonProperty("DECL_WEIGHT")
    private String DECL_WEIGHT;
    @JsonProperty("DECL_CLASS")
    private String DECL_CLASS;
    @JsonProperty("DECL_GROUND")
    private String DECL_GROUND;


}
