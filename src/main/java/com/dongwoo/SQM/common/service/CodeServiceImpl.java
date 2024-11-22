package com.dongwoo.SQM.common.service;


import com.dongwoo.SQM.common.repository.CodeRepository;
import com.dongwoo.SQM.companyInfo.repository.CompanyInfoRepository;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeServiceImpl {
    @Autowired
    private final CodeRepository codeRepository;
    private final CompanyInfoRepository companyInfoRepository;

    //코드 바인딩
    public List<BaseCodeDTO> GetBaseCode(String group_code) {
        return companyInfoRepository.GetBaseCode(group_code);
    }


    /**
     * 코드 리스트를 조회합니다.
     *
     * @param codeDto 코드
     * @return List<CodeDto>
     *//*
    @Transactional(readOnly = true)
    @Cacheable(value = "codeCacheInfo", key = "#codeDto.groupCode")
    public List<CodeDto> selectCodeList(CodeDto codeDto) {
        return codeRepository.selectCodeList(codeDto);
    }

    *//**
     * 코드 키 값을 기반으로 코드 정보를 조회합니다
     *
     * @param cd String
     * @return CodeDto
     *//*
    @Transactional(readOnly = true)
    @CachePut(value = "codeCacheInfo", key = "#cd")
    public CodeDto selectCodeByCd(String cd) {
        return codeRepository.selectCodeByCd(cd);
    }

    *//**
     * 코드 삭제
     *
     * @param CodeDto codeDto
     * @return int
     *//*
    @Transactional
    @CacheEvict(value = "codeCacheInfo", key = "#codeDto.groupCode", beforeInvocation = false)
    public int deleteCode(CodeDto codeDto) throws Exception {
        try {
            return codeRepository.deleteCode(codeDto);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }*/
}
