package com.dongwoo.SQM.siteMgr.service;

import com.dongwoo.SQM.siteMgr.dto.SampleFileDTO;
import com.dongwoo.SQM.siteMgr.repository.SampleFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SampleFileService {

    private final SampleFileRepository sampleFileRepository;

    public void upload(List<SampleFileDTO> sampleFileDTOList){
        sampleFileRepository.upload(sampleFileDTOList);
    }

    public List<HashMap> plantList(String sLang){
        return sampleFileRepository.plantList(sLang);
    }

    public List<SampleFileDTO> getFileInfo(String baseCode,String fileNum,String sLang,String GUBN){
        Map<String, Object> params = new HashMap<>();
        params.put("baseCode", baseCode);
        params.put("fileNum", fileNum);
        params.put("sLang", sLang);
        params.put("case",GUBN);
        return sampleFileRepository.getFileInfo(params);
    }

    public List<SampleFileDTO> getPlantFileInfo(String sLang) {
        return sampleFileRepository.getPlantFileInfo(sLang);
    }

    public List<SampleFileDTO> getOtherFileInfo(String sLang){
        return  sampleFileRepository.getOtherFileInfo(sLang);
    }
}
