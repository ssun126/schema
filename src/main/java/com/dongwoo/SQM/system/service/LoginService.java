package com.dongwoo.SQM.system.service;

import com.dongwoo.SQM.system.dto.LoginDTO;
import com.dongwoo.SQM.system.repository.LoginRepository;
import com.dongwoo.SQM.system.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final LoginRepository loginRepository;
    private final MenuRepository menuRepository;

    public int save(LoginDTO loginDTO) {
        return loginRepository.save(loginDTO);
    }

    public LoginDTO login(LoginDTO loginDTO) {
        // 조회 결과가 있다(해당 정보를 가진 회원 정보가 있다)
        LoginDTO loginMember = loginRepository.login(loginDTO);
        if (loginMember != null) {
            // 비밀번호 일치
            return loginMember;
        } else {
            // 비밀번호 불일치(로그인실패)
            return null;
        }
    }

    public List<LoginDTO> findAll() {
        return loginRepository.findAll();
    }

    public LoginDTO findById(String USER_ID) {
        return loginRepository.findById(USER_ID);
    }

    public void delete(String USER_ID) {
        loginRepository.delete(USER_ID);
    }

    public LoginDTO findByLoginEmail(String loginEmail) {
        return loginRepository.findByLoginEmail(loginEmail);
    }

    public LoginDTO findByLoginId(String loginId) {
        return loginRepository.findByLoginId(loginId);
    }

    public LoginDTO findByLoginName(String loginName) { return loginRepository.findByLoginName(loginName);  }

    public boolean update(LoginDTO loginDTO) {
        int result = loginRepository.update(loginDTO);
        if(result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String emailCheck(String USER_EMAIL) {
        LoginDTO loginDTO = loginRepository.findByLoginEmail(USER_EMAIL);
        if (loginDTO == null) {
            return "ok";
        } else {
            return "no";
        }
    }



}
