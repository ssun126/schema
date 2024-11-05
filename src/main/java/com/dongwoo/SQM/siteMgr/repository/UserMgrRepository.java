package com.dongwoo.SQM.siteMgr.repository;

import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrParamDTO;
import com.dongwoo.SQM.system.dto.UserInfoDTO;
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

    //동우 사용자 한명 검색.POP 에서
    public UserMgrDTO findUserMgrById(String USER_ID) {
        return sql.selectOne("userMgr.findUserMgrById", USER_ID);
    }

    //동우 사용자 업데이트
    public void updateUserMgr(UserMgrDTO userMgrDTO) {
        sql.update("userMgr.updateUserInfoMgr", userMgrDTO);
        sql.update("userMgr.updateUserMgr", userMgrDTO);
    }

    //동우 사용자 추가
    public void insertUserMgr(UserMgrDTO userMgrDTO) {

        //ID 생성
        String defultPassWord = "123456t" ;  //기본password
        userMgrDTO.setUSER_PWD(defultPassWord);
        userMgrDTO.setUSER_GUBN(0);  //사용자 구분 (0:동우화인켐, 1:업체)
        userMgrDTO.setREG_DW_USER_IDX(0);  //등록자 로그인 사용자 처리
        userMgrDTO.setUP_DW_USER_IDX(0);  //수정자  로그인 사용자 처리
        sql.insert("userMgr.insertUserInfoMgr", userMgrDTO);

        //get IDX
        UserMgrDTO findUserDto = sql.selectOne("userMgr.findByUserId", userMgrDTO);
        userMgrDTO.setUSER_IDX(findUserDto.getUSER_IDX());
        userMgrDTO.setMANAGE_SYSTEM_YN("N");  //(Y:사용, N:미사용)  확인필요

        sql.insert("userMgr.insertUserinfoDwMgr", userMgrDTO);

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
