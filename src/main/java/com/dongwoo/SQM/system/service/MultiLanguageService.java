package com.dongwoo.SQM.system.service;

import com.dongwoo.SQM.system.dto.MultiLanguageDTO;
import com.dongwoo.SQM.system.repository.MultiLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class MultiLanguageService {
    @Autowired
    private MultiLanguageRepository multiLanguageRepository;

    public List<MultiLanguageDTO> findAll() {
        return multiLanguageRepository.findAll();
    }

    //다국어 가져오기
    public List<HashMap> getMultiLanguageList_HashMap(){
        return multiLanguageRepository.getMultiLanguageList_HashMap();
    }

    //다국어 가져오기
    public List<HashMap> getMultiLangsList_HashMap(){
        return multiLanguageRepository.getMultiLangsList_HashMap();
    }

    public List<HashMap> getMultiLangs_HashMap(String KOR){
        return multiLanguageRepository.getMultiLangs_HashMap(KOR);
    }

    public void saveMultiLanguage(String KOR){
        multiLanguageRepository.saveMultiLanguage(KOR);
    }
}
