package com.dongwoo.SQM.config.security;

import com.dongwoo.SQM.system.dto.LoginDTO;
import com.dongwoo.SQM.system.repository.LoginRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
public class AccountService  implements UserDetailsService {

    @Autowired
    private LoginRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;

    // 시큐리티의 내용 외 파라미터를 추가하고 싶을 때, 아래 사용
    //  제약조건: Controller 에서 Auth를 점검할 때, UserCustom 으로 받아야 함.
    //  예) (변경 전) @AuthenticationPrincipal User user => (변경 후) @AuthenticationPrincipal UserCustom user

    boolean enabled = true;
    boolean accountNonExpired = true;
    boolean credentialsNonExpired = true;
    boolean accountNonLocked = true;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username======="+username);
        LoginDTO loginDTO = userRepository.findByLoginId(username);
        log.info("loginDTO======="+loginDTO);
        // 조회가 되지않는 고객은 에러발생.
        if(loginDTO == null){
            throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다.:"+username);
        }

        // 조회한 정보를 userCustom에 담는다.
        // 만약 파라미터를 추가해야한다면 UserCustom 을 먼저 수정한다.
        UserCustom userCustom = new UserCustom(loginDTO.getUsername()
                , loginDTO.getUSER_PWD()
                , enabled, accountNonExpired, credentialsNonExpired, accountNonLocked
                , authorities(loginDTO)
                , loginDTO.getUSER_GUBN()
                , loginDTO.getUSER_STATUS()
                , loginDTO.getCOM_CODE()
                , loginDTO.getUSER_NAME() // 이름
        );

        return userCustom;
    }

    private static Collection<? extends GrantedAuthority> authorities(LoginDTO loginDTO){
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // DB에 저장한 USER_ROLE 이 0이면 ADMIN 권한, 아니면 ROLE_USER 로 준다.
        if(loginDTO.getUSER_GUBN().equals("0")){
            authorities.add(new SimpleGrantedAuthority("ADMIN"));
        }else{
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }
}
