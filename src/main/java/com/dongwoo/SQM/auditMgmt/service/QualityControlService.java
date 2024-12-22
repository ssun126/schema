package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.AuditItemPointDTO;
import com.dongwoo.SQM.auditMgmt.repository.QualityControlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QualityControlService {
    private final QualityControlRepository qualityControlRepository;

    //업체별 평가항목/점수 정보
    public List<AuditItemPointDTO> getCompanyAuthItemPoint(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return qualityControlRepository.getCompanyAuthItemPoint(params);
    }
}
