package com.dongwoo.SQM.config.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
@ToString
public class UserCustom extends User {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private String USER_GUBUN;
    private String USER_STASTUS;
    private String USER_NAME;

    public UserCustom(String username, String password
            , boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked
            , Collection<? extends GrantedAuthority> authorities
            , String USER_GUBUN,String USER_STASTUS, String USER_NAME) {
        super(username, password
                , enabled, accountNonExpired, credentialsNonExpired, accountNonLocked
                , authorities);
        this.USER_GUBUN = USER_GUBUN;
        this.USER_STASTUS = USER_STASTUS;
        this.USER_NAME = USER_NAME;
    }
}
