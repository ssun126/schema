package com.dongwoo.SQM.system.service;

import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.system.dto.*;
import com.dongwoo.SQM.system.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public int save(MemberDTO memberDTO) {
        return memberRepository.save(memberDTO);
    }

    public List<MemberDTO> findAll() {
        return memberRepository.findAll();
    }


    public void delete(int id) {
        memberRepository.delete(id);
    }

    public MemberDTO findByMemberEmail(String loginEmail) {
        return memberRepository.findByMemberEmail(loginEmail);
    }



    public boolean update(MemberDTO memberDTO) {
        int result = memberRepository.update(memberDTO);
        if(result > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String emailCheck(String USER_EMAIL) {
        MemberDTO memberDTO = memberRepository.findByMemberEmail(USER_EMAIL);
        if (memberDTO == null) {
            return "ok";
        } else {
            return "no";
        }
    }


    // 저장후 만들어진 USER_IDX 받아오자.
    public UserInfoDTO findByUserId(String loginId) {
        return memberRepository.findByUserId(loginId);
    }

    public UserInfoDTO findByUserIdx(int loginIdx) {
        return memberRepository.findByUserIdx(loginIdx);
    }


    // 저장후 공동 사용자 만들어진 COM_USER_IDX 받아오자.
    public UserInfoCompanyUserDTO findByCompanyUserName(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return memberRepository.findByCompanyUserName(userInfoCompanyUserDTO);
    }
    // 공동 사용자 com_code, user_idx
    public List<UserInfoCompanyUserDTO> findByCompanyUserComCode(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return memberRepository.findByCompanyUserComCode(userInfoCompanyUserDTO);
    }

    // 공동 사용자 com_code, user_idx
    public List<UserInfoCompanyUserDTO> findByMemberInfoAll(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return memberRepository.findByMemberInfoAll(userInfoCompanyUserDTO);
    }


    // 공동 사용자 전체 com_code
    public List<UserInfoCompanyUserDTO> findByCompanyUserAll(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return memberRepository.findByCompanyUserAll(userInfoCompanyUserDTO);
    }

    //My page 정보조회
    public UserMgrDTO findByMemberId(String loginId) {
        return memberRepository.findUserInfoDataById(loginId);
    }

    //My page 업체용 접속목적  체크박스
    public List<UserMgrDTO> findConnectGoalByUserId(String loginId) {
        return memberRepository.findConnectGoalByUserId(loginId);
    }



    //사용자 저장
    public int saveUserInfo(UserInfoDTO userInfoDTO) {
        return memberRepository.save(userInfoDTO);
    }

    //공동 사용자 저장 USER_INFO_COMPANY_USER
    public int saveUserInfoCompany(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return memberRepository.saveCompanyUser(userInfoCompanyUserDTO);
    }

    //사용자 추가정보 관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)  정보 저장. company
    public int saveUserInfoCompanyHis(UserInfoCompanyDTO userInfoCompanyDTO) {
        return memberRepository.saveUserinfoCompanyHis(userInfoCompanyDTO);
    }

    //COMPANYCODE 정보 업데이트 (가입)
    public int updateCompanyCode(ComPanyCodeDTO comPanyCodeDTO) {
        return memberRepository.updateCompanyCode(comPanyCodeDTO);
    }

    //COMPANYCODE 정보 업데이트(가입후)
    public int updateCpCodeCPUser(ComPanyCodeDTO comPanyCodeDTO) {
        return memberRepository.updateCpCodeCPUser(comPanyCodeDTO);
    }



    //사용자 ID 중복체크
    public String idCheck(String USER_ID) {
        MemberDTO memberDTO = memberRepository.findByMemberId(USER_ID);
        if (memberDTO == null) {
            return "ok";
        } else {
            return "no";
        }
    }


    //ID 상세정보.
    public MemberDTO findById(int id) {
        return memberRepository.findById(id);
    }

    //warranty 조회
    public MemberDTO getCOMPANYCODE(String searchCode ) {
        MemberDTO memberDTO = memberRepository.findByComPanyCode(searchCode);
        return memberDTO ;
    }


    //00.최초 가입여부 (COMPANY_CODE)검색 IF 받아서 기초정보만 있다.
    //없으면  - > 업체코드 내역이 없어 가입이 불가합니다.
    public MemberDTO basicvendorNumCheck(String searchType ,String searchCode ) {

        MemberDTO memberDTO  = null;
        if(Objects.equals(searchType, "VendorNum")) {
            memberDTO = memberRepository.findByComPanyCode(searchCode);
        }else {
            memberDTO = memberRepository.findByBUS_NUMBER(searchCode);
        }
        return memberDTO ;
    }

    //01.코드 검색 USER_INFO_COMPANY 진행중인 업체코드 COM_CODE 등록 여부
    public MemberDTO vendorNumCheck(String vendorCode) {
        MemberDTO memberDTO = memberRepository.findByUserInfoCompany(vendorCode);
        return memberDTO ;
    }

    //가입 승인된 회사 정보만 찾는다.
    public List<MemberDTO> findApproveCompany(String vendorCode) {
        return memberRepository.findApproveCompany(vendorCode);
    }

    //회사 사용자 로그인 ID로 정보확인
    public MemberDTO findCpLoginID(String vendorCode) {
        MemberDTO memberDTO = memberRepository.findCpLoginID(vendorCode);
        return memberDTO ;
    }


}
