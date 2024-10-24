package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

        return sql.selectList("BasConfig.findSearch",data);
    }
}
