package com.dongwoo.SQM.common.service;


import com.dongwoo.SQM.common.dto.CodeCacheDTO;
import com.dongwoo.SQM.common.repository.CodeCacheRepository;
import com.dongwoo.SQM.common.repository.SubMenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.ltgfmt.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeCacheService {
    @Autowired
    private final CodeCacheRepository codeCacheRepository;
    // 하위 코드 전체 return
    @Cacheable(value = "commonCodes", key = "'genderCodes'")
    public List<String> getGenderCodes() {
        // 실제 DB에서 성별 코드 리스트를 가져오는 메서드
        return Arrays.asList("M", "F");
    }

    @Cacheable(value = "commonCodes", key = "'countryCodes'")
    public List<String> getCountryCodes() {
        // 실제 DB에서 국가 코드 리스트를 가져오는 메서드
        return Arrays.asList("US", "KR", "JP");
    }
}
