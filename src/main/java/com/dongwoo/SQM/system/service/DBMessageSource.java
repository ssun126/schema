package com.dongwoo.SQM.system.service;


import com.dongwoo.SQM.system.repository.MultiLanguageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Slf4j
@Component("messageSource")
public class DBMessageSource extends AbstractMessageSource {
    @Autowired
    private MultiLanguageRepository multiLanguageRepository;
    private static final String DEFAULT_LOCALE_CODE = "kr";


    @Override
    protected MessageFormat resolveCode(String key, Locale locale) {
       /* MultiLanguageDTO message = multiLanguageRepository.findByKeyAndLocale(key, locale.getLanguage());

        if (locale.getLanguage() == null) {
            message = multiLanguageRepository.findByKeyAndLocale(key, DEFAULT_LOCALE_CODE);
        }
        log.info("key??? "+key);
        log.info("locale.getLanguage()??? "+locale.getLanguage());
        log.info("message??? "+message);
        if (message == null) {
            throw new IllegalArgumentException("Message is null");
        }
        return new MessageFormat(message.getMESSAGE_CONTENT(), locale);*/
        return null;
    }
}
