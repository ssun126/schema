package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MultiLanguageDTO {
    private int ID;
    private String LOCALE;
    private String MESSAGE_KEY;
    private String MESSAGE_CONTENT;
}
