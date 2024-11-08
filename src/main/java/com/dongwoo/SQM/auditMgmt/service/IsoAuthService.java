package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.repository.IsoAuthRepository;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IsoAuthService {
    private final IsoAuthRepository isoAuthRepository;

    public int save(IsoAuthItemDTO isoAuthItemDTO) {
        return isoAuthRepository.save(isoAuthItemDTO);
    }

    // 여러 조건을 처리하는 검색 메서드
    public List<IsoAuthItemDTO> searchCompanies(String code, Criteria criteria) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("criteria", criteria);
        return isoAuthRepository.findByCriteria(params);
    }

    // 검색 조건에 맞는 총 개수를 반환
    public int getTotalByKeyword(String name, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_NAME", name);
        params.put("COM_CODE", code);
        return isoAuthRepository.countByKeyword(params);
    }

    public List<IsoAuthItemDTO> getList(Criteria cri) {
        return isoAuthRepository.getList(cri);
    }
    public int getTotal() {
        return isoAuthRepository.getTotal();
    }

    public IsoAuthItemDTO findByCompanyId(String id) {
        return isoAuthRepository.findByCompanyId(id);
    }
}
