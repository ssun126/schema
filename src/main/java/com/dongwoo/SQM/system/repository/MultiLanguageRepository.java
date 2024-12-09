package com.dongwoo.SQM.system.repository;

import com.dongwoo.SQM.system.dto.MultiLanguageDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //로컬스토리지 저장용
    public List<HashMap> getMultiLangsList_HashMap() {
        return sql.selectList("MultiLanguage.getMultiLangsList_HashMap");
    }

    public List<HashMap> getMultiLangs_HashMap(String KOR) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("KOR", KOR);
        return sql.selectList("MultiLanguage.getMultiLangs_HashMap", parameterMap);
    }

    public void saveMultiLanguage(String KOR) {
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("KOR", KOR);
        sql.insert("MultiLanguage.saveMultiLanguage", parameterMap);
    }
}
