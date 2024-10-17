package com.dongwoo.SQM.system.service;

import com.dongwoo.SQM.system.dto.MemberDTO;
import com.dongwoo.SQM.system.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public MemberDTO findById(int id) {
        return memberRepository.findById(id);
    }

    public void delete(int id) {
        memberRepository.delete(id);
    }

    public MemberDTO findByMemberEmail(String loginEmail) {
        return memberRepository.findByMemberEmail(loginEmail);
    }

    public MemberDTO findByMemberId(String loginId) {
        return memberRepository.findByMemberId(loginId);
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

    public String vendorCheck(String vendorCode) {
//        MemberDTO memberDTO = memberRepository.findByMemberEmail(vendorCode);
//        if (memberDTO == null) {
//            return "ok";
//        } else {
//            return "no";
//        }
        return "ok";
    }

}
