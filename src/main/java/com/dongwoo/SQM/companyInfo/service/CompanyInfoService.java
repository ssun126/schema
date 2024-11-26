package com.dongwoo.SQM.companyInfo.service;

import com.dongwoo.SQM.board.dto.BoardDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoParamDTO;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import com.dongwoo.SQM.companyInfo.repository.CompanyInfoRepository;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrParamDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompanyInfoService {
    private final CompanyInfoRepository companyInfoRepository;

    //코드 바인딩
    public List<BaseCodeDTO> GetBaseCode(String group_code) {
        return companyInfoRepository.GetBaseCode(group_code);
    }

    //업체 목록 검색 2024.10.30
    public List<CompanyInfoDTO> findCompanySearch(CompanyInfoParamDTO companyInfoParamDTO ) {
        return companyInfoRepository.findCompanySearch(companyInfoParamDTO);
    }

    public List<CompanyInfoDTO> approvalCompanySearch(CompanyInfoParamDTO companyInfoParamDTO ) {
        return companyInfoRepository.approvalCompanySearch(companyInfoParamDTO);
    }

    //업체 거래사업부
    public List<CompanyInfoDTO> findCompanyCodeWork(CompanyInfoParamDTO companyInfoParamDTO) {
        return companyInfoRepository.findCompanyCodeWork(companyInfoParamDTO);
    }

    //업체 회원 ID 검색 확장
    public List<CompanyInfoDTO> findCompanyCodeWorkEx(CompanyInfoParamDTO companyInfoParamDTO) {
        return companyInfoRepository.findCompanyCodeWorkEx(companyInfoParamDTO);
    }
    //신청정보 조회
    public CompanyInfoDTO findCompanyApprovalID(CompanyInfoParamDTO companyInfoParamDTO) {
        return companyInfoRepository.findCompanyApprovalID(companyInfoParamDTO);
    }

    public int save(CpCodeDTO cpCodeDTO) {
        return companyInfoRepository.save(cpCodeDTO);
    }

    public List<CompanyInfoDTO> findAll() {
        return companyInfoRepository.findAll();
    }

    // 여러 조건을 처리하는 검색 메서드
    public List<CompanyInfoDTO> searchCompanies(String name, String code, String nation, Criteria criteria) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_NAME", name);
        params.put("COM_CODE", code);
        params.put("COM_NATION", nation);
        params.put("criteria", criteria);
        return companyInfoRepository.findByCriteria(params);
    }

    // 검색 조건에 맞는 총 개수를 반환
    public int getTotalByKeyword(String name, String code, String nation) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_NAME", name);
        params.put("COM_CODE", code);
        params.put("COM_NATION", nation);
        return companyInfoRepository.countByKeyword(params);
    }

    public List<CompanyInfoDTO> getList(Criteria cri) {
        return companyInfoRepository.getList(cri);
    }
    public int getTotal() {
        return companyInfoRepository.getTotal();
    }

    public CompanyInfoDTO findByCompanyId(String id) {
        return companyInfoRepository.findByCompanyId(id);
    }

    public void update(CompanyInfoDTO companyInfoDTO) {
        companyInfoRepository.update(companyInfoDTO);
    }

    public void delete(int id) {
        companyInfoRepository.delete(id);
    }

}
