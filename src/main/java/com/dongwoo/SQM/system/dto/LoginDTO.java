package com.dongwoo.SQM.system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@ToString
public class LoginDTO implements UserDetails {

    private int USER_idx;
    private String PASSWORD;
    private String USER_Mobile;

   //SC_USER_INFO
   private int USER_IDX;
   private int COM_USER_IDX;
   private String USER_ID;
   private String USER_PWD;  //비밀번호 (SHA 암호화)
   private String USER_NAME;
   private String USER_GUBN; //(0:동우화인켐, 1:업체)

   private String USER_STATUS; // 사용자 상태 (Y:사용, N:미사용)
   private int REG_DW_USER_IDX;
   private int UP_DW_USER_IDX;
   private int DEL_DW_USER_IDX;
   private String USER_EMAIL;
   private String REG_DATE		;
   private String UP_DATE;
   private String DEL_DATE;
   private String ROLE;
   private Collection<? extends GrantedAuthority> authorities;

   private String COM_CODE;
   private String COM_NAME;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collectors = new ArrayList<>();
        collectors.add(()->{return "ROLE_"+this.getROLE();}); //add에 들어올 파라미터는 GrantedAuthority밖에 없으니

        return collectors;
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


