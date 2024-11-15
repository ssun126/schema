package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.IsoAuthDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.repository.IsoAuthRepository;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
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

    public int save(IsoAuthDTO isoAuthDTO) {
        return isoAuthRepository.save(isoAuthDTO);
    }

    public int saveItem(IsoAuthItemDTO isoAuthItemDTO) {
        return isoAuthRepository.saveItem(isoAuthItemDTO);
    }

    // ISO 인증 업체 리스트- 조건을 처리하는 검색 메서드
    public List<IsoAuthDTO> searchCompanies(String code, String name, String state, Criteria criteria) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("COM_STATUS", state);
        params.put("criteria", criteria);
        return isoAuthRepository.searchCompanies(params);
    }

    // ISO 인증 업체 리스트-검색 조건에 맞는 총 개수를 반환
    public int getTotalByKeyword(String code, String name, String state) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("COM_STATUS", state);
        return isoAuthRepository.countByKeyword(params);
    }

    //업체별 ISO 인증서 정보 리스트
    public List<IsoAuthItemDTO> getList(Criteria cri) {
        return isoAuthRepository.getList(cri);
    }

    //업체별 ISO 인증서 만료일 정보 리스트
    public List<IsoAuthItemDTO> getExpDateList(String code, String name, String expDate, Criteria criteria) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("EXP_DATE", expDate);
        params.put("criteria", criteria);
        return isoAuthRepository.getExpDateList(params);
    }
    public int getTotal() {
        return isoAuthRepository.getTotal();
    }

    //업체별 ISO 인증서 정보-상세보기
    public IsoAuthItemDTO findByIsoAuthItem(String auth_id,String com_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_CODE", auth_id);
        params.put("COM_CODE", com_id);
        return isoAuthRepository.findByIsoAuthItem(params);
    }

    //업체별 ISO 인증서 리스트-상세보기
    public List<IsoAuthItemDTO> findByCompanyId(String com_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", com_id);
        return isoAuthRepository.findByCompanyId(params);
    }

    //상태업데이트
    public void updateStatus(IsoAuthItemDTO isoAuthItemDTO) {
        isoAuthRepository.updateStatus(isoAuthItemDTO);
    }
}
