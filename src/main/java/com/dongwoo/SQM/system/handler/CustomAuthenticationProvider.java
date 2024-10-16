package com.dongwoo.SQM.system.handler;

import com.dongwoo.SQM.system.dto.LoginDTO;
import com.dongwoo.SQM.system.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.security.auth.login.LoginException;
import java.util.Collections;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final LoginService loginService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // 사용자 정보 조회
        LoginDTO loginDTO = loginService.findByLoginId(username);

        // password 일치 여부 체크
        if(!bCryptPasswordEncoder.matches(password, loginDTO.getPASSWORD()))
            try {
                throw new LoginException("error");
            } catch (LoginException e) {
                throw new RuntimeException(e);
            }

        // return UsernamePasswordAuthenticationToken
        return new UsernamePasswordAuthenticationToken(
                loginDTO.getUSER_ID(),
                loginDTO.getPASSWORD(),
                Collections.singleton(new SimpleGrantedAuthority(loginDTO.getROLE()))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}