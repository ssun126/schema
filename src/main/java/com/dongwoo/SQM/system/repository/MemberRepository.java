package com.dongwoo.SQM.system.repository;

import com.dongwoo.SQM.system.dto.*;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    private final SqlSessionTemplate sql;

    public int save(MemberDTO memberDTO) {
        System.out.println("memberDTO = " + memberDTO);
        return sql.insert("Member.save", memberDTO);
    }

    public List<MemberDTO> findAll() {
        return sql.selectList("Member.findAll");
    }

    public MemberDTO findById(int id) {
        return sql.selectOne("Member.findById", id);
    }

    public void delete(int id) {
        sql.delete("Member.delete", id);
    }



    //User ID 생성. 10.23
    public int save(UserInfoDTO userInfoDTO) {
        System.out.println("userInfoDTO = " + userInfoDTO);
        return sql.insert("Member.saveUserinfo", userInfoDTO);
    }

    //User ID 생성 된 USERIDX. 가져오기  10.23
    public UserInfoDTO findByUserId(String userId) { return sql.selectOne("Member.findByUserId", userId); }


    //User 공동 사용자 정보 생성. 10.23
    public int companysave(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        System.out.println("userInfoCompanyUserDTO = " + userInfoCompanyUserDTO);
        return sql.insert("Member.saveUserinfoCompanyUser", userInfoCompanyUserDTO);
    }

    //User history. 10.23
    public int saveUserinfoCompanyHis(UserInfoCompanyDTO userInfoCompanyDTO) {
        System.out.println("userInfoCompanyUserDTO = " + userInfoCompanyDTO);
        return sql.insert("Member.saveUserinfoCompanyHis", userInfoCompanyDTO);
    }

    //User history. 10.23
    public int updateCompanyCode(ComPanyCodeDTO comPanyCodeDTO) {
        System.out.println("comPanyCodeDTO = " + comPanyCodeDTO);
        return sql.insert("Member.updateCompanyCode", comPanyCodeDTO);
    }



    //User ID 생성 된 USERIDX. 가져오기  10.23
    //public UserInfoCompanyUserDTO findByCompanyUserIdx(int userIdx) { return sql.selectOne("Member.findByCompanyUserIdx", userIdx); }
    //User ID 생성 된 COMUSERIDX. 공동 사용자 가져오기  10.24
    public UserInfoCompanyUserDTO findByCompanyName(UserInfoCompanyUserDTO userInfoCompanyUserDTO) { return sql.selectOne("Member.findByCompanyUserName", userInfoCompanyUserDTO ); }


    //최초가입 여부 (마스터 코드 등록여부) "COMCODE" 로 검색
    public MemberDTO findByComPanyCode(String comCode) { return sql.selectOne("Member.findByComPanyCode", comCode); }

    //최초가입 여부 (마스터 코드 등록여부) "BUSNUMBER" 로 검색
    public MemberDTO findByBusNumber(String busNumber) { return sql.selectOne("Member.findByBusNumber", busNumber); }


    //USERINFOCOMPANY
    public MemberDTO findByUserInfoCompany(String comCode) { return sql.selectOne("Member.findByUserInfoCompany", comCode); }

    //ID 중복체크
    public MemberDTO findByMemberId(String loginId) { return sql.selectOne("Member.findByMemberId", loginId); }


    public MemberDTO findByMemberEmail(String loginEmail) { return sql.selectOne("Member.findByMemberEmail", loginEmail); }



    public int update(MemberDTO memberDTO) {
        return sql.update("Member.update", memberDTO);
    }
}
