package com.dongwoo.SQM.system.multiLanguage.service;

import com.dongwoo.SQM.system.multiLanguage.repository.LanguageRepository;
import com.dongwoo.SQM.system.multiLanguage.dto.LanguageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component("messageSource")
public class DBMessageSource extends AbstractMessageSource {
    @Autowired
    private LanguageRepository languageRepository;
    private static final String DEFAULT_LOCALE_CODE = "kr";


    @Override
    protected MessageFormat resolveCode(String key, Locale locale) {
        LanguageDTO message = languageRepository.findByKeyAndLocale(key, locale.getLanguage());

        if (message == null) {
            message = languageRepository.findByKeyAndLocale(key,DEFAULT_LOCALE_CODE);
        }
        return new MessageFormat(message.getMESSAGE_CONTENT(), locale);
    }
}
