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

    private int USER_IDX;
    private String COM_USER_IDX;
    private String USER_GUBUN;
    private String USER_STATUS;
    private String COM_CODE;
    private String COM_NAME;
    private String USER_NAME;

    public UserCustom(String username, String password
            , boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked
            , Collection<? extends GrantedAuthority> authorities
            , int USER_IDX, String COM_USER_IDX, String USER_GUBUN, String USER_STATUS, String COM_CODE, String COM_NAME, String USER_NAME) {
        super(username, password
                , enabled, accountNonExpired, credentialsNonExpired, accountNonLocked
                , authorities);
        this.USER_IDX = USER_IDX;
        this.COM_USER_IDX = COM_USER_IDX;
        this.USER_GUBUN = USER_GUBUN;
        this.USER_STATUS = USER_STATUS;
        this.COM_CODE = COM_CODE;
        this.COM_NAME = COM_NAME;
        this.USER_NAME = USER_NAME;
    }
}
