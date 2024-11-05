package com.dongwoo.SQM.common.component;

import com.dongwoo.SQM.common.dto.MenuStaticValue;
import com.dongwoo.SQM.common.dto.SecurityUrlMatcherDTO;
import com.dongwoo.SQM.common.dto.SubMenuDTO;
import com.dongwoo.SQM.common.service.SecurityUrlMatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DemoInitializer implements ApplicationRunner {
    @Autowired
    private SecurityUrlMatcherService securityUrlMatcherService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // 전체 메뉴-권한 정보 리스트 조회/저장
       /* List<SecurityUrlMatcherDTO> menuList =  securityUrlMatcherService.getAllMenu(); // ... DB에서 메뉴 정보 조회하는 로직 실행
        MenuStaticValue.menuList = Collections.singletonList(menuList);

        List<SubMenuDTO> subMenuList =  securityUrlMatcherService.getAllSubMenu(); // ... DB에서 메뉴 정보 조회하는 로직 실행
        MenuStaticValue.subMenuList = Collections.singletonList(subMenuList);*/

        //관리자
        List<SecurityUrlMatcherDTO> adminMenuList =  securityUrlMatcherService.getAllAdminMenu(); // ... DB에서 메뉴 정보 조회하는 로직 실행
        MenuStaticValue.adminMenuList = Collections.singletonList(adminMenuList);

        List<SubMenuDTO> adminSubMenuList =  securityUrlMatcherService.getAllAdminSubMenu(); // ... DB에서 메뉴 정보 조회하는 로직 실행
        MenuStaticValue.adminSubMenuList = Collections.singletonList(adminSubMenuList);

        //사용자
        List<SecurityUrlMatcherDTO> userMenuList =  securityUrlMatcherService.getAllUserMenu(); // ... DB에서 메뉴 정보 조회하는 로직 실행
        MenuStaticValue.userMenuList = Collections.singletonList(userMenuList);

        List<SubMenuDTO> userSubMenuList =  securityUrlMatcherService.getAllUserSubMenu(); // ... DB에서 메뉴 정보 조회하는 로직 실행
        MenuStaticValue.userSubMenuList = Collections.singletonList(userSubMenuList);
    }
}
