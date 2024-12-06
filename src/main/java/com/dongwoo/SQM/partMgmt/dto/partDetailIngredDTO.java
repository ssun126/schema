package com.dongwoo.SQM.partMgmt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class partDetailIngredDTO {

    @JsonProperty("INGRED_IDX")
    private String INGRED_IDX;
    @JsonProperty("PM_IDX")
    private String PM_IDX;
    @JsonProperty("INGRED_CONFIRM_DATE")
    private String INGRED_CONFIRM_DATE;
    @JsonProperty("INGRED_FILE_NAME")
    private String INGRED_FILE_NAME;
    @JsonProperty("INGRED_FILE_PATH")
    private String INGRED_FILE_PATH;

    @JsonProperty("FILE_STATUS")
    private String FILE_STATUS;
}
