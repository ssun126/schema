package com.dongwoo.SQM.common.service.impl;


import com.dongwoo.SQM.common.dto.CodeDTO;
import com.dongwoo.SQM.common.repository.CodeRepository;
import com.dongwoo.SQM.common.repository.CommonRepository;
import com.dongwoo.SQM.common.service.CodeService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CodeServiceImpl implements CodeService {
    private CodeRepository codeRepository;

    /**
     * 코드 리스트를 조회합니다.
     *
     * @param codeDto CodeDto
     * @return List<CodeDto>
     */
    @Cacheable(value = "codeCacheInfo", key="#codeDto.grpCd")
    public List<BaseCodeDTO> selectCodeList(BaseCodeDTO codeDto) {
        return codeRepository.selectCodeList(codeDto);
    }

    /**
     * 코드 키 값을 기반으로 코드 정보를 조회합니다
     *
     * @param cd String
     * @return CodeDto
     */
    @Transactional(readOnly = true)
    @CachePut(value = "codeCacheInfo", key = "#cd")
    public BaseCodeDTO selectCodeByCd(String cd) {
        return codeRepository.selectCodeByCd(cd);
    }

    /**
     * 코드 리스트 조회
     *
     * @param codeDto 코드
     * @return CodeDto
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "codeCacheInfo", key = "#codeDto.cd")
    public BaseCodeDTO selectCode(BaseCodeDTO codeDto) {
        return codeRepository.selectCode(codeDto);
    }
}
