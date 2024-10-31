package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrParamDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserMgrRepository {
    private final SqlSessionTemplate sql;

    public int save(UserMgrDTO userMgrDTO) {
        return sql.insert("UserMgr.save", userMgrDTO);
    }

    //동우 사용자 검색 2024.10.30
    public List<UserMgrDTO> findUserMgrSearch(UserMgrParamDTO userMgrDTO) {
        return sql.selectList("userMgr.findUserMgrSearch",userMgrDTO );
    }

    //사용자 한명 검색.
    public UserMgrDTO findUserMgrById(String USER_ID) {
        return sql.selectOne("userMgr.findUserMgrById", USER_ID);
    }

    //사용자 한명 검색.XXXXXXXXXXXXXXX as is
    public UserMgrDTO findById(int USER_ID) {
        return sql.selectOne("userMgr.findUserMgrById", USER_ID);
    }

    public void updateHits(int id) {
        sql.update("userMgr.updateHits", id);
    }



    public void update(UserMgrDTO userMgrDTO) {
        sql.update("userMgr.update", userMgrDTO);
    }

    public void delete(int id) {
        sql.delete("userMgr.delete", id);
    }
}
