package com.dongwoo.SQM.system.service;

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


    // 저장후 만들어진 USERIDX 받아오자.
    public UserInfoDTO findByUserId(String loginId) {
        return memberRepository.findByUserId(loginId);
    }

    public UserInfoDTO findByUserIdx(int loginIdx) {
        return memberRepository.findByUserIdx(loginIdx);
    }


    // 저장후 공동 사용자 만들어진 COMUSERIDX 받아오자.
    public UserInfoCompanyUserDTO findByCompanyUserName(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return memberRepository.findByCompanyName(userInfoCompanyUserDTO);
    }
    // 공동 사용자 전체
    public List<UserInfoCompanyUserDTO> findByCompanyUserAll(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return memberRepository.findByCompanyUserAll(userInfoCompanyUserDTO);
    }

    public MemberDTO findByMemberId(String loginId) {
        return memberRepository.findByMemberId(loginId);
    }

    //사용자 저장
    public int saveUserInfo(UserInfoDTO userInfoDTO) {
        return memberRepository.save(userInfoDTO);
    }

    //공동 사용자 저장 company
    public int saveUserInfoCompany(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return memberRepository.companysave(userInfoCompanyUserDTO);
    }

    //사용자 추가정보 관리상태 (0:대기, 1:검토중, 2:승인, 3:반려)  정보 저장. company
    public int saveUserInfoCompanyHis(UserInfoCompanyDTO userInfoCompanyDTO) {
        return memberRepository.saveUserinfoCompanyHis(userInfoCompanyDTO);
    }

    //COMPANYCODE 정보 업데이트
    public int updateCompanyCode(ComPanyCodeDTO comPanyCodeDTO) {
        return memberRepository.updateCompanyCode(comPanyCodeDTO);
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


    //00.최초 가입여부 (COMPANYCODE)검색 IF 받아서 기초정보만 있다.
    //없으면  - > 업체코드 내역이 없어 가입이 불가합니다.
    public MemberDTO basicvendorNumCheck(String searchType ,String searchCode ) {

        MemberDTO memberDTO  = null;
        if(Objects.equals(searchType, "VendorNum")) {
            memberDTO = memberRepository.findByComPanyCode(searchCode);
        }else {
            memberDTO = memberRepository.findByBusNumber(searchCode);
        }
        return memberDTO ;
    }

    //01.코드 검색 COMPANYCODE
    public MemberDTO vendorNumCheck(String vendorCode) {
        MemberDTO memberDTO = memberRepository.findByUserInfoCompany(vendorCode);
        return memberDTO ;
    }

}
