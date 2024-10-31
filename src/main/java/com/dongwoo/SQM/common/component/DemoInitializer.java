package com.dongwoo.SQM.common.component;

import com.dongwoo.SQM.common.dto.MenuStaticValue;
import com.dongwoo.SQM.common.dto.SecurityUrlMatcherDTO;
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
        List<SecurityUrlMatcherDTO> menuList =  securityUrlMatcherService.getAllMenu(); // ... DB에서 메뉴 정보 조회하는 로직 실행
        MenuStaticValue.menuList = Collections.singletonList(menuList);
    }
}
