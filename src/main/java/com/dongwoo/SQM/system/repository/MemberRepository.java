package com.dongwoo.SQM.system.repository;

import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
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

        UserInfoDTO findUserDto = sql.selectOne("Member.findByUserId", userInfoDTO);

        if(findUserDto == null) {
            return sql.insert("Member.saveUserinfo", userInfoDTO);
        }else {
            return sql.update("Member.updateUserinfo", userInfoDTO);
        }
    }

    //User ID 생성 된 USER_IDX. 가져오기  10.23
    public UserInfoDTO findByUserId(String userId) { return sql.selectOne("Member.findByUserId", userId); }

    public UserInfoDTO findByUserIdx(int USER_IDX) { return sql.selectOne("Member.findByUserIdx", USER_IDX); }

    //User 공동 사용자 정보 생성. 10.23
    public int saveCompanyUser(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {

        // 여기서 키값은  회사번호 COM_CODE , 사용자명 USER_NAME 으로 검색해서.
        UserInfoCompanyUserDTO findCompanyUserDto = sql.selectOne("Member.findByCompanyUserName", userInfoCompanyUserDTO );
        if(findCompanyUserDto == null) {
            return sql.insert("Member.saveUserinfoCompanyUser", userInfoCompanyUserDTO);
        }else {
            userInfoCompanyUserDTO.setCOM_USER_IDX(findCompanyUserDto.getCOM_USER_IDX()); //기존 유저 COM_USER_IDX 설정
            return sql.update("Member.updateUserinfoCompanyUser", userInfoCompanyUserDTO);
            //가입-> 수정시 공동 사용자가 지워지는 경우 생각해보자!!
        }
    }

    //User history. 10.23
    public int saveUserinfoCompanyHis(UserInfoCompanyDTO userInfoCompanyDTO) {

        MemberDTO finduserInfoCompanyDTO = sql.selectOne("Member.findByUserInfoCompany", userInfoCompanyDTO.getCOM_CODE());
        if(finduserInfoCompanyDTO == null) {
            return sql.insert("Member.saveUserinfoCompanyHis", userInfoCompanyDTO);
        }else {
            return sql.update("Member.updateUserinfoCompanyHis", userInfoCompanyDTO);
        }

    }

    //User history. 10.23
    public int updateCompanyCode(ComPanyCodeDTO comPanyCodeDTO) {
        System.out.println("comPanyCodeDTO = " + comPanyCodeDTO);
        return sql.insert("Member.updateCompanyCode", comPanyCodeDTO);
    }



    //User ID 생성 된 USER_IDX. 가져오기  10.23
    //public UserInfoCompanyUserDTO findByCompanyUSER_IDX(int USER_IDX) { return sql.selectOne("Member.findByCompanyUSER_IDX", USER_IDX); }
    //User ID 생성 된 COM_USER_IDX. 공동 사용자 가져오기  10.24
    public UserInfoCompanyUserDTO findByCompanyUserName(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return sql.selectOne("Member.findByCompanyUserName", userInfoCompanyUserDTO );
    }
    //공동 사용자 전체 가져오기  10.24
    public List<UserInfoCompanyUserDTO> findByCompanyUserAll(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return sql.selectList("Member.findByCompanyUserAll", userInfoCompanyUserDTO ); }


    //최초가입 여부 (마스터 코드 등록여부) "COM_CODE" 로 검색
    public MemberDTO findByComPanyCode(String COM_CODE) { return sql.selectOne("Member.findByCompanyCode", COM_CODE); }

    //최초가입 여부 (마스터 코드 등록여부) "BUS_NUMBER" 로 검색
    public MemberDTO findByBUS_NUMBER(String BUS_NUMBER) { return sql.selectOne("Member.findByBUSNUMBER", BUS_NUMBER); }


    //USERINFOCOMPANY
    public MemberDTO findByUserInfoCompany(String COM_CODE) { return sql.selectOne("Member.findByUserInfoCompany", COM_CODE); }

    //ID 중복체크
    public MemberDTO findByMemberId(String loginId) { return sql.selectOne("Member.findByMemberId", loginId); }

    //MyPage 동우 사용자 조인해서 가져오기.
    public UserMgrDTO findUserInfoDataById(String  USER_ID) { return sql.selectOne("userMgr.findUserMgrById", USER_ID); }
    //업체용 접속목적  체크박스
    public List<UserMgrDTO> findConnectGoalByUserId(String  USER_ID) { return sql.selectList("userMgr.findConnectGoalByUserId", USER_ID); }

    public MemberDTO findByMemberEmail(String loginEmail) { return sql.selectOne("Member.findByMemberEmail", loginEmail); }



    public int update(MemberDTO memberDTO) {
        return sql.update("Member.update", memberDTO);
    }
}
