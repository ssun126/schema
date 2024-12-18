package com.dongwoo.SQM.siteMgr.service;

import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.repository.BaseCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseCodeService {
    private final BaseCodeRepository baseCodeRepository;

    public void save(BaseCodeDTO baseCodeDTO) {
        baseCodeRepository.save(baseCodeDTO);
    }

    public List<BaseCodeDTO> findAll() {
        return baseCodeRepository.findAll();
    }
    public List<BaseCodeDTO> getbaseGubunList() {
        return baseCodeRepository.getbaseGubunList();
    }
    public List<BaseCodeDTO> getbaseGroupCDList() {
        return baseCodeRepository.getbaseGroupCDList();
    }

    public BaseCodeDTO getbaseCodeInfo(int idx) {
        return baseCodeRepository.getbaseCodeInfo(idx);
    }
    public BaseCodeDTO getbaseCodeInfo(String idx) {
        return baseCodeRepository.getbaseCodeInfo(idx);
    }
    public BaseCodeDTO getbaseCodeInfoCode(String BASE_CODE) {
        return baseCodeRepository.getbaseCodeInfoCode(BASE_CODE);
    }

    public List<BaseCodeDTO> findSearch(String sGubun, String sCodeGroup, String sKey, String sTextVal) {
        return baseCodeRepository.findSearch(sGubun,sCodeGroup,sKey,sTextVal);
    }

    public BaseCodeDTO findByCodeName(BaseCodeDTO baseCodeDTO) {
        return baseCodeRepository.findByCodeName(baseCodeDTO);
    }

    public List<BaseCodeDTO> findByCodeGroup(BaseCodeDTO baseCodeDTO) {
        return baseCodeRepository.findByCodeGroup(baseCodeDTO);
    }

    public List<BaseCodeDTO> findByCodeGroupAll() {
        return baseCodeRepository.findByCodeGroupAll();
    }

    public List<BaseCodeDTO> findByCodeGroupUse(String BASE_CODE) {
        return baseCodeRepository.findByCodeGroupUse(BASE_CODE);
    }

    public void update(BaseCodeDTO baseCodeDTO) {
        baseCodeRepository.update(baseCodeDTO);
    }

    public void delete(int CODEID) {
        baseCodeRepository.delete(CODEID);
    }

}
