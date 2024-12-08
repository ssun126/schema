package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.repository.IsoAuthRepository;
import com.dongwoo.SQM.auditMgmt.repository.SafetyHealthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SafetyHealthService {
    private SafetyHealthRepository safetyHealthRepository;

    //업체별 인증서 정보
    public AuditMgmtDTO getCompanyAuth(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return safetyHealthRepository.getCompanyAuth(params);
    }
}
