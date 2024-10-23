package com.dongwoo.SQM.system.repository;

import com.dongwoo.SQM.system.dto.MultiLanguageDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MultiLanguageRepository {
    private final SqlSessionTemplate sql;

    public MultiLanguageDTO findByKeyAndLocale(String key, String locale) {
        MultiLanguageDTO multiLanguageDTO = new MultiLanguageDTO();
        multiLanguageDTO.setLOCALE(locale);
        multiLanguageDTO.setMESSAGE_KEY(key);
        return sql.selectOne("MultiLanguage.findByKeyAndLocale", multiLanguageDTO);
    }
    //전체조회
    public List<MultiLanguageDTO> findAll() {
        return sql.selectList("MultiLanguage.findAll");
    }

    //로컬스토리지 저장용
    public List<HashMap> getMultiLanguageList_HashMap() {
        return sql.selectList("MultiLanguage.getMultiLanguageList_HashMap");
    }


}
