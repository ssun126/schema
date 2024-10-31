package com.dongwoo.SQM.common.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoginUtil {

    /**
     * 로그인한 사용자인지 여부
     * @return
     */
    public static boolean isLogin(){
        boolean result = true;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof String){
            result = false;
        }

        return result;
    }

    /**
     * Spring Security Context에서 로그인한 사용자의 id 조회
     *
     * @return 로그인한 사용자의 id
     * @throws AccessDeniedException AccessDeniedException
     */
    public static long getLoginId() throws AccessDeniedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 정상적으로 로그인한 사용자 정보인지 체크
        checkAuth(authentication);

        return (long) authentication.getPrincipal();
    }

    /**
     * 정상적으로 로그인한 사용자 정보인지 체크
     *
     * @param authentication Authentication
     * @throws AccessDeniedException AccessDeniedException
     */
    private static void checkAuth(Authentication authentication) throws AccessDeniedException {
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("로그인 정보가 존재하지 않습니다.");
        }
    }
}
