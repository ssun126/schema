package com.dongwoo.SQM.auditMgmt.service;

import java.util.Map;

public interface AuditCommonService {
    public Map<String, String> getUserInfo(Map<String,Object> parameterMap);
    public Map<String, String> commonSendMail(Map<String, Object> parameterMap);
}
