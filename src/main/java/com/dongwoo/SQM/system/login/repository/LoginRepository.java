package com.dongwoo.SQM.system.login.repository;

import com.dongwoo.SQM.system.login.dto.LoginDTO;
import com.dongwoo.SQM.system.menu.dto.MenuDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LoginRepository {
    private final SqlSessionTemplate sql;

    public int save(LoginDTO loginDTO) {
        System.out.println("memberDTO = " + loginDTO);
        return sql.insert("Login.save", loginDTO);
    }

    public LoginDTO login(LoginDTO loginDTO) {
        return sql.selectOne("Login.login", loginDTO);
    }

    public List<LoginDTO> findAll() {
        return sql.selectList("Login.findAll");
    }

    public LoginDTO findById(int id) {
        return sql.selectOne("Login.findById", id);
    }

    public void delete(int id) {
        sql.delete("Login.delete", id);
    }

    public LoginDTO findByMemberEmail(String loginEmail) {
        return sql.selectOne("Login.findByMemberEmail", loginEmail);
    }

    public LoginDTO findByMemberId(String loginId) {
        return sql.selectOne("Login.findByMemberId", loginId);
    }

    public int update(LoginDTO loginDTO) {
        return sql.update("Login.update", loginDTO);
    }

    public List<MenuDTO>  findUserMenu(LoginDTO loginDTO) {
        return sql.selectList("Login.findUserMenu", loginDTO);
    }
}
