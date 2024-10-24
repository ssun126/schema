package com.dongwoo.SQM.siteMgr.service;

import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.repository.BaseCodeRepository;
import com.dongwoo.SQM.siteMgr.repository.BaseConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseConfigService {
    private final BaseConfigRepository baseConfigRepository;
    public List<BaseConfigDTO> findAll() {
        return baseConfigRepository.findAll();
    }

    public List<BaseConfigDTO> findSearch(String gubun, String key, String textval){
        return baseConfigRepository.findSearch(gubun,key,textval);
    }

}
