package com.dongwoo.SQM.siteMgr.service;

import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.repository.BaseConfigRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import groovy.util.logging.Slf4j;
import java.util.List;

@Slf4j
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

    public BaseConfigDTO getBaseConfig_Info(String idx) {
        return baseConfigRepository.getBaseConfig_Info(idx);
    }

    public void save(BaseConfigDTO baseConfigDTO) {
         baseConfigRepository.save(baseConfigDTO);
    }

    public void update(BaseConfigDTO baseConfigDTO) {
        baseConfigRepository.update(baseConfigDTO);
    }

    public void delete(int CODEID) {
        baseConfigRepository.delete(CODEID);
    }


}
