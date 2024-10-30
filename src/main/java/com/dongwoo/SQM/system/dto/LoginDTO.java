package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
public class LoginDTO implements UserDetails {
    private int USER_idx;
    private String USER_ID;
    private String USER_EMAIL;
    private String PASSWORD;
    private String USER_NAME;
    private String USER_Mobile;
    private String ROLE;

   //USERINFO
   private int USERIDX;
   private String USERID;
   private String USER_PWD;  //비밀번호 (SHA 암호화)
   private String USERNAME;
   private String USERGUBN; //(0:동우화인켐, 1:업체)

   private String USERSTATUS; // 사용자 상태 (Y:사용, N:미사용)
   private String REGDWUSERIDX;
   private String UPDWUSERIDX;
   private String DELDWUSERIDX;
   private String REGDATE		;
   private String UPDATE;
   private String DELDATE;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.PASSWORD;
    }

    @Override
    public String getUsername() {
        return this.USER_ID;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}


