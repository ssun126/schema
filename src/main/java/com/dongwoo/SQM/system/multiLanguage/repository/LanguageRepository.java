package com.dongwoo.SQM.system.multiLanguage.repository;

import com.dongwoo.SQM.system.multiLanguage.dto.LanguageDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LanguageRepository {
    private final SqlSessionTemplate sql;

    public LanguageDTO findByKeyAndLocale(String key, String locale) {
        LanguageDTO languageDTO = new LanguageDTO();
        languageDTO.setLOCALE(locale);
        languageDTO.setMESSAGE_KEY(key);
        return sql.selectOne("Language.findByKeyAndLocale", languageDTO);
    }
}
