package com.dongwoo.SQM.siteMgr.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class BaseConfigDTO {
    @JsonProperty("IDX")
    private int IDX;
    @JsonProperty("GUBN")
    private String GUBN;
    @JsonProperty("CONFIG_CODE")
    private String CONFIG_CODE;
    @JsonProperty("CONFIG_VALUE")
    private String CONFIG_VALUE;
    @JsonProperty("CONFIG_SUMMARY")
    private String CONFIG_SUMMARY;
    @JsonProperty("CONFIG_OPTION")
    private String CONFIG_OPTION;
    @JsonProperty("CONFIG_OPTION2")
    private String CONFIG_OPTION2;
    @JsonProperty("CONFIG_OPTION3")
    private String CONFIG_OPTION3;
    @JsonProperty("CONFIG_STATUS")
    private String CONFIG_STATUS;
    @JsonProperty("REGISTER_CODE")
    private String REGISTER_CODE;
    @JsonProperty("MODIFIER_CODE")
    private String MODIFIER_CODE;
    @JsonProperty("REG_DATE")
    private String REG_DATE;
    @JsonProperty("MODIFI_DATE")
    private String MODIFI_DATE;
    @JsonProperty("USER_NAME")
    private String USER_NAME;
    @JsonProperty("USER_ID")
    private String USER_ID;
    @JsonProperty("USER_IDX")
    private int USER_IDX;
    @JsonProperty("INFO_FLAG")
    private String INFO_FLAG;

}
