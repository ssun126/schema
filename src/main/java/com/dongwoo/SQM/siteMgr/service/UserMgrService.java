package com.dongwoo.SQM.siteMgr.service;

import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
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

    public List<UserMgrDTO> findAll() {
        return userMgrRepository.findAll();
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
