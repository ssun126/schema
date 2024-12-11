package com.dongwoo.SQM.companyInfo.service;

import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoParamDTO;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import com.dongwoo.SQM.companyInfo.repository.CompanyInfoRepository;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
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

    public List<CompanyInfoDTO> cpMaterialSearch(CompanyInfoParamDTO companyInfoParamDTO ) {
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
        //세션 정보 가져오기
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginIdx = user.getUSER_IDX();
        cpCodeDTO.setREG_DW_USER_IDX(loginIdx);
        cpCodeDTO.setUP_DW_USER_IDX(loginIdx);

        int retCntCp = 0;
        //업체코드로 데이터 가져오기 ??? factory_id도 비교해야 하나
        CompanyInfoDTO companyInfoDTO = companyInfoRepository.findByCompanyId(cpCodeDTO.getCOM_CODE());

        if(companyInfoDTO != null){ //기존 업체정보가 있다면
            if(saveDeptCode(cpCodeDTO) > 0){ //사업본부 정보 입력 후 update
                retCntCp = companyInfoRepository.updateComCode(cpCodeDTO);
            }
        }else{
            if(saveDeptCode(cpCodeDTO) > 0){ //사업본부 정보 입력 insert
                retCntCp = companyInfoRepository.insertComCode(cpCodeDTO);
            }
        }
        return retCntCp;
    }

    // 사업본부 정보 저장
    private int saveDeptCode(CpCodeDTO cpCodeDTO) {
        int retCnt = 0;
        //사업본부는 삭제 후 저장
        companyInfoRepository.deleteComCodeWork(cpCodeDTO);
        //사업본부 데이터는 여러개 입력 가능
        for (String deptCode : cpCodeDTO.getDEPT_CODE()) {
            cpCodeDTO.setDEPT_CODES(deptCode);
            retCnt = companyInfoRepository.insertComCodeWork(cpCodeDTO);
        }
        return retCnt;
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

    public List<CompanyInfoDTO> listSearchCompanies(String name, String code, String nation, String dept ) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_NAME", name);
        params.put("COM_CODE", code);
        params.put("COM_NATION", nation);
        params.put("DEPT_CODES", dept);
        return companyInfoRepository.findSearch(params);
    }

    public List<HashMap> companyApiList(String name, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_NAME", name);
        params.put("COM_CODE", code);
        return companyInfoRepository.getApiList(params);
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
