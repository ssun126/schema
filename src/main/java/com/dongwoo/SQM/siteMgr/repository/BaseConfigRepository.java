package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BaseConfigRepository {
    private final SqlSessionTemplate sql;

    public List<BaseConfigDTO> findAll() {
        return sql.selectList("BaseConfig.findAll");
    }

    public List<BaseConfigDTO> findSearch(String gubun, String key, String textval ) {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("gubun",gubun);
        data.put("key",key);
        data.put("textval",textval);

        return sql.selectList("BaseConfig.findSearch",data);
    }

    public BaseConfigDTO getBaseConfig_Info(String idx){
        return sql.selectOne("BaseConfig.getBaseConfig_Info",idx);
    }

    public int save(BaseConfigDTO baseConfigDTO) {
        return sql.insert("BaseConfig.save", baseConfigDTO);
    }

    public void update(BaseConfigDTO baseConfigDTO) {
        sql.update("BaseConfig.update", baseConfigDTO);
    }

    public void delete(int CODEID) {
        sql.delete("BaseConfig.delete", CODEID);
    }

}
