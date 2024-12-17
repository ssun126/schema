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

    ////(CODE1, CODE2, --CODE3, MES_KIND,GUBN,KIND,SEND_TYPE ,SEND_FROM ,SEND_TO, SEND_TITLE , SEND_BODY , SEND_DATE ,REG_DW_USER_IDX , REG_COM_USER_IDX  )
    public void sendExpAlert(String CODE1, int CODE2, String MES_KIND,String GUBN,String KIND,String SEND_TYPE,String SEND_FROM,String SEND_TO, String SEND_TITLE, String SEND_BODY, int REG_DW_USER_IDX,int  REG_COM_USER_IDX){
        ExpirationRepository.sendExpAlert(CODE1, CODE2, MES_KIND,GUBN,KIND,SEND_TYPE ,SEND_FROM ,SEND_TO, SEND_TITLE , SEND_BODY ,REG_DW_USER_IDX , REG_COM_USER_IDX);
    }
}
