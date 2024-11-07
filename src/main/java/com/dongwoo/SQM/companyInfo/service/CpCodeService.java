package com.dongwoo.SQM.companyInfo.service;

import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import com.dongwoo.SQM.companyInfo.repository.CompanyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CpCodeService {
    private final CompanyInfoRepository companyInfoRepository;

    public void save(CpCodeDTO cpCodeDTO) {
        companyInfoRepository.save(cpCodeDTO);
    }

    public List<CompanyInfoDTO> findAll() {
        return companyInfoRepository.findAll();
    }

    public List<CompanyInfoDTO> getList(Criteria cri) {
        return companyInfoRepository.getList(cri);
    }
    public int getTotal() {
        return companyInfoRepository.getTotal();
    }

    public CompanyInfoDTO findById(String id) {
        return companyInfoRepository.findByCompanyId(id);
    }

    public void update(CompanyInfoDTO companyInfoDTO) {
        companyInfoRepository.update(companyInfoDTO);
    }

    public void delete(int id) {
        companyInfoRepository.delete(id);
    }



}
