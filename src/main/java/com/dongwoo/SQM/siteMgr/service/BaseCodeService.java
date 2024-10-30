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

    public BaseCodeDTO getBaseCode_Info(String idx) {
        return baseCodeRepository.getBaseCode_Info(idx);
    }

    public BaseCodeDTO findByCodeName(BaseCodeDTO baseCodeDTO) {
        return baseCodeRepository.findByCodeName(baseCodeDTO);
    }

    public List<BaseCodeDTO> findByCodeGroup(BaseCodeDTO baseCodeDTO) {
        return baseCodeRepository.findByCodeGroup(baseCodeDTO);
    }

    public void update(BaseCodeDTO baseCodeDTO) {
        baseCodeRepository.update(baseCodeDTO);
    }

    public void delete(int CODEID) {
        baseCodeRepository.delete(CODEID);
    }

}
