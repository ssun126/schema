package com.dongwoo.SQM.common.service;


import com.dongwoo.SQM.common.dto.SecurityUrlMatcherDTO;
import com.dongwoo.SQM.common.dto.SubMenuDTO;
import com.dongwoo.SQM.common.repository.SecurityUrlMatcherRepository;
import com.dongwoo.SQM.common.repository.SubMenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityUrlMatcherService {
    @Autowired
    private final SecurityUrlMatcherRepository securityUrlMatcherRepository;

    @Autowired
    private final SubMenuRepository subMenuRepository;


    public List<SecurityUrlMatcherDTO> getAllAdminMenu() {
        return securityUrlMatcherRepository.getAllAdminMenu();
    }

    public List<SecurityUrlMatcherDTO> getAllUserMenu() {
        return securityUrlMatcherRepository.getAllUserMenu();
    }

    public List<SecurityUrlMatcherDTO> getAllAdminSubMenu() {
        return subMenuRepository.getAllAdminSubMenu();
    }

    public List<SecurityUrlMatcherDTO> getAllUserSubMenu() {
        return subMenuRepository.getAllUserSubMenu();
    }

    public List<SecurityUrlMatcherDTO> getAllAdminThirdMenu() {
        return subMenuRepository.getAllAdminThirdMenu();
    }

    public List<SecurityUrlMatcherDTO> getAllUserThirdMenu() {
        return subMenuRepository.getAllUserThirdMenu();
    }
}
