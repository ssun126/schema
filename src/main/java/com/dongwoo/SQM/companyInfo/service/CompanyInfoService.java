package com.dongwoo.SQM.companyInfo.service;

import com.dongwoo.SQM.board.dto.BoardDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.repository.CompanyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyInfoService {
    private final CompanyInfoRepository companyInfoRepository;

    public void save(CompanyInfoDTO companyInfoDTO) {
        companyInfoRepository.save(companyInfoDTO);
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

    public void updateHits(int id) {
        companyInfoRepository.updateHits(id);
    }

    public CompanyInfoDTO findById(int id) {
        return companyInfoRepository.findById(id);
    }

    public void update(CompanyInfoDTO companyInfoDTO) {
        companyInfoRepository.update(companyInfoDTO);
    }

    public void delete(int id) {
        companyInfoRepository.delete(id);
    }

}
