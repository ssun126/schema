package com.dongwoo.SQM.system.multiLanguage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LanguageDTO {
    private int ID;
    private String LOCALE;
    private String MESSAGE_KEY;
    private String MESSAGE_CONTENT;
}
