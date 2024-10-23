package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
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

    public List<UserMgrDTO> findAll() {
        return sql.selectList("UserMgr.findAll");
    }

    public void updateHits(int id) {
        sql.update("UserMgr.updateHits", id);
    }

    public UserMgrDTO findById(int id) {
        return sql.selectOne("UserMgr.findById", id);
    }

    public void update(UserMgrDTO userMgrDTO) {
        sql.update("UserMgr.update", userMgrDTO);
    }

    public void delete(int id) {
        sql.delete("UserMgr.delete", id);
    }
}
