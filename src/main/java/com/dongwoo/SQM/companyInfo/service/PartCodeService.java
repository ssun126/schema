package com.dongwoo.SQM.companyInfo.service;

import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoParamDTO;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import com.dongwoo.SQM.companyInfo.dto.PartCodeDTO;
import com.dongwoo.SQM.companyInfo.repository.CompanyInfoRepository;
import com.dongwoo.SQM.companyInfo.repository.PartCodeRepository;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PartCodeService {
    private final PartCodeRepository partCodeRepository;

    //코드 바인딩
    public List<BaseCodeDTO> GetBaseCode(String group_code) {
        return partCodeRepository.GetBaseCode(group_code);
    }

    //업체 목록 검색 2024.10.30
    public List<PartCodeDTO> partCodeList(PartCodeDTO partCodeDTO ) {
        return partCodeRepository.partCodeList(partCodeDTO);
    }


}
