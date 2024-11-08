package com.dongwoo.SQM.siteMgr.service;

import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrParamDTO;
import com.dongwoo.SQM.siteMgr.repository.UserMgrRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMgrService {
    private final UserMgrRepository userMgrRepository;

    //코드 바인딩
    public List<BaseCodeDTO> GetBaseCode(String group_code) {
        return userMgrRepository.GetBaseCode(group_code);
    }

    public void save(UserMgrDTO UserMgrDTO) {
        userMgrRepository.save(UserMgrDTO);
    }

    //동우 사용자 검색 2024.10.30
    public List<UserMgrDTO> findUserMgrSearch(UserMgrParamDTO usermgrdto) {
        return userMgrRepository.findUserMgrSearch(usermgrdto);
    }

    //동우 사용자 한명 조회
    public UserMgrDTO findUserMgrById(String USER_ID) {
        return userMgrRepository.findUserMgrById(USER_ID);
    }
    //사용자 업데이트
    public void updateUserMgr(UserMgrDTO userMgrDTO) {
        userMgrRepository.updateUserMgr(userMgrDTO);
    }

    //사용자 업데이트  MyPage
    public void updateUserMgrMyPage(UserMgrDTO userMgrDTO) {
        userMgrRepository.updateUserMgrMyPage(userMgrDTO);
    }

    //사용자  비밀번호 업데이트  MyPage
    public void updateUserPWS(UserMgrDTO userMgrDTO) {
        userMgrRepository.updateUserPWS(userMgrDTO);
    }


    //사용자 추가
    public void insertUserMgr(UserMgrDTO userMgrDTO) {
        userMgrRepository.insertUserMgr(userMgrDTO);
    }


    //업체 MyPage 접속 목적 삭제.
    public void deleteConnectGoal(UserMgrDTO userMgrDTO) {
        userMgrRepository.deleteConnectGoal(userMgrDTO);
    }

    //업체 MyPage 접속 목적 추가.
    public void insertConnectGoal(UserMgrDTO userMgrDTO) {
        userMgrRepository.insertConnectGoal(userMgrDTO);
    }


    public void updateHits(int id) {
        userMgrRepository.updateHits(id);
    }

    public UserMgrDTO findById(int id) {
        return userMgrRepository.findById(id);
    }

    public void update(UserMgrDTO UserMgrDTO) {
        userMgrRepository.update(UserMgrDTO);
    }

    public void delete(int id) {
        userMgrRepository.delete(id);
    }

}
