package com.dongwoo.SQM.common.service;

import com.dongwoo.SQM.common.dto.ExpirationDateDTO;
import com.dongwoo.SQM.common.repository.ExpirationDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpirationDataService {

    private final ExpirationDataRepository ExpirationRepository;

    public ExpirationDateDTO getExpiration(String CODE1, int CODE2, String EXP_KIND) {
        return ExpirationRepository.getExpiration(CODE1, CODE2, EXP_KIND);
    }

    public void setExpiration(String CODE1, int CODE2, String EXP_KIND, int EXP_MONTH, String EXP_BODY, int UP_DW_USER_IDX) {
        ExpirationRepository.setExpiration(CODE1, CODE2, EXP_KIND, EXP_MONTH, EXP_BODY, UP_DW_USER_IDX);
    }
}
