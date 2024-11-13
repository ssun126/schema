package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.SampleFileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SampleFileRepository {
    private  final SqlSessionTemplate sql;

    public void upload(SampleFileDTO sampleFileDTO){
        sql.insert("sampleFile.upload",sampleFileDTO);
    }

    public void update(SampleFileDTO sampleFileDTO){
        sql.insert("sampleFile.update",sampleFileDTO);
    }
    public void delete(SampleFileDTO sampleFileDTO){
        sql.insert("sampleFile.delete",sampleFileDTO);
    }

    public List<HashMap> plantList(String sLang){
        return sql.selectList("sampleFile.plantList",sLang);
    }

    public List<SampleFileDTO> getFileInfo(Map<String, Object> params){

        return sql.selectList("sampleFile.getFileInfo",params);
    }

    public List<SampleFileDTO> getPlantFileInfo(String sLang) {
        return sql.selectList("sampleFile.getPlantFileInfo",sLang);
    }

    public List<SampleFileDTO> getOtherFileInfo(String sLang){
        return sql.selectList("sampleFile.getOtherFileInfo",sLang);
    }
}
