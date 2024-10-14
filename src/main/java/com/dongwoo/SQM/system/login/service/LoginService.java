package com.dongwoo.SQM.system.login.service;

import com.dongwoo.SQM.system.login.dto.LoginDTO;
import com.dongwoo.SQM.system.login.repository.LoginRepository;
import com.dongwoo.SQM.system.menu.dto.MenuDTO;
import com.dongwoo.SQM.system.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public LoginDTO findById(int id) {
        return loginRepository.findById(id);
    }

    public void delete(int id) {
        loginRepository.delete(id);
    }

    public LoginDTO findByMemberEmail(String loginEmail) {
        return loginRepository.findByMemberEmail(loginEmail);
    }

    public LoginDTO findByMemberId(String loginId) {
        return loginRepository.findByMemberId(loginId);
    }


    public boolean update(LoginDTO loginDTO) {
        int result = loginRepository.update(loginDTO);
        if(result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String emailCheck(String USER_Email) {
        LoginDTO loginDTO = loginRepository.findByMemberEmail(USER_Email);
        if (loginDTO == null) {
            return "ok";
        } else {
            return "no";
        }
    }

    //메뉴 정보 가져오기
    public List<MenuDTO> getMenuList(LoginDTO loginDTO){return loginRepository.findUserMenu(loginDTO);}
}
