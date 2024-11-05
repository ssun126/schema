package com.dongwoo.SQM.siteMgr.service;

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


    //사용자 추가
    public void insertUserMgr(UserMgrDTO userMgrDTO) {
        userMgrRepository.insertUserMgr(userMgrDTO);
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
