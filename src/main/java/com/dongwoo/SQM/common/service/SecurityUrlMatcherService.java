package com.dongwoo.SQM.common.service;


import com.dongwoo.SQM.common.dto.SecurityUrlMatcherDTO;
import com.dongwoo.SQM.common.repository.SecurityUrlMatcherRepository;
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


    public List<SecurityUrlMatcherDTO> getAllMenu() {
        return securityUrlMatcherRepository.getAllMenu();
    }
}
