package com.dongwoo.SQM.system.service;

import com.dongwoo.SQM.system.dto.LoginDTO;
import com.dongwoo.SQM.system.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService  implements UserDetailsService {

    private final LoginRepository userMapper;
    private final BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("username======="+username);
        LoginDTO account = new LoginDTO();
        account.setUSER_ID(username);
        account = userMapper.findByLoginId(username);
        log.info("account======="+account);
        if(account != null){
            List<GrantedAuthority> authorities = new ArrayList();
            //패스워드 암호화 저장이 되지 않아 여기서 암호화하여 넘겨줌(임시)
            return new User(account.getUSER_ID(), encoder.encode(account.getPassword()), authorities);
        }
        return null;
    }

    @Transactional
    public boolean join(String userId, String userPwd) {
        log.info("userId======="+userId+"///userPwd======="+userPwd);
        LoginDTO checkUser = new LoginDTO();
        checkUser.setUSER_ID(userId);

        if (userMapper.login(checkUser) != null){
            return false;
        }
        LoginDTO newUser = new LoginDTO();
        newUser.setUSER_ID(userId);
        newUser.setPASSWORD(encoder.encode(userPwd));
        userMapper.save(newUser);
        return true;
    }

}
