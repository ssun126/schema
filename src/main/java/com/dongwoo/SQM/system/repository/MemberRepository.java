package com.dongwoo.SQM.system.repository;

import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.system.dto.*;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public int saveUserinfo(UserInfoDTO userInfoDTO) {
        System.out.println("userInfoDTO = " + userInfoDTO);

        UserInfoDTO findUserDto = sql.selectOne("Member.findByUserId", userInfoDTO);

        if(findUserDto == null) {
            return sql.insert("Member.saveUserinfo", userInfoDTO);
        }else {
            return sql.update("Member.updateUserinfo", userInfoDTO);
        }
    }

    //User_Name만 변경
    public int updateUserName(UserInfoDTO userInfoDTO) {
        return sql.update("Member.updateUserName", userInfoDTO);
    }

    //User 상태 변경
    public int updateUserStatus(UserInfoDTO userInfoDTO) {
        return sql.update("Member.updateUserStatus", userInfoDTO);
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
            userInfoCompanyUserDTO.setCOM_USER_IDX(findCompanyUserDto.getCOM_USER_IDX());
            return sql.update("Member.updateUserinfoCompanyUser", userInfoCompanyUserDTO);
        }
    }

    //User 공동 사용자 정보 생성. 10.23
    public int updateCompanyUser(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {

        // 여기서 키값은  회사번호 COM_CODE , 사용자명 USER_NAME 으로 검색해서.
        UserInfoCompanyUserDTO findCompanyUserDto = sql.selectOne("Member.findByCompanyUserIdx", userInfoCompanyUserDTO );
        if(findCompanyUserDto == null) {
            return sql.insert("Member.saveUserinfoCompanyUser", userInfoCompanyUserDTO);
        }else {
            userInfoCompanyUserDTO.setCOM_USER_IDX(findCompanyUserDto.getCOM_USER_IDX());
            return sql.update("Member.updateUserinfoCompanyUser", userInfoCompanyUserDTO);
        }
    }

    //User 공동 사용자 정보 삭제. 업체 유저 회사 정보 관리 update
    public int deleteCompanyUser(String comCode , List<Integer> companyUseridxList) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", comCode);
        params.put("list", companyUseridxList);
        return sql.delete("Member.deleteUserinfoCompanyUser", params);
    }

    //User history. 10.23
    public int saveUserinfoCompanyHis(UserInfoCompanyDTO userInfoCompanyDTO) {
        //USER_INFO_COMPANY  (0:대기, 1:검토중, 2:승인, 3:반려)  --> 승인된거는 제외 하자.
        MemberDTO finduserInfoCompanyDTO = sql.selectOne("Member.findByUserInfoCompany", userInfoCompanyDTO.getCOM_CODE());

        if(finduserInfoCompanyDTO == null) {
            return sql.insert("Member.saveUserinfoCompanyHis", userInfoCompanyDTO);
        }else {
            return sql.update("Member.updateUserinfoCompanyHis", userInfoCompanyDTO);
        }
    }

    //User history. 10.23
    public int deleteUserinfoCompanyHis(UserInfoCompanyDTO userInfoCompanyDTO) {
        //USER_INFO_COMPANY  (0:대기 삭제 처리 , 1:검토중, 2:승인, 3:반려)
        return sql.update("Member.deleteUserinfoCompanyHis", userInfoCompanyDTO);
    }


    //가입 승인된 회사 정보만 찾는다.
    public List<MemberDTO> findApproveCompany(String COM_CODE) {
        return sql.selectList("Member.findApproveCompany", COM_CODE);
    }


    //회사 사용자 로그인 ID로 정보확인 2024.11.13
    public MemberDTO findCpLoginID(String USER_ID) {
        return sql.selectOne("Member.findCpLoginID", USER_ID);
    }


    //회사정보 업데이트 (가입)
    public int updateCompanyCode(ComPanyCodeDTO comPanyCodeDTO) {
        return sql.insert("Member.updateCompanyCode", comPanyCodeDTO);
    }

    //회사정보 업데이트 (가입후)
    public int updateCpCodeCPUser(ComPanyCodeDTO comPanyCodeDTO) {
        return sql.insert("Member.updateCpCodeCPUser", comPanyCodeDTO);
    }



    //User ID 생성 된 USER_IDX. 가져오기  10.23
    //public UserInfoCompanyUserDTO findByCompanyUSER_IDX(int USER_IDX) { return sql.selectOne("Member.findByCompanyUSER_IDX", USER_IDX); }
    //User ID 생성 된 COM_USER_IDX. 공동 사용자 가져오기  10.24
    public UserInfoCompanyUserDTO findByCompanyUserName(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return sql.selectOne("Member.findByCompanyUserName", userInfoCompanyUserDTO );
    }
    //공동 사용자 ID별 가져오기  10.24
    public List<UserInfoCompanyUserDTO> findByCompanyUserComCode(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return sql.selectList("Member.findByCompanyUserComCode", userInfoCompanyUserDTO ); }

    //공동 사용자 ID 조인후 전체 가져오기
    public List<UserInfoCompanyUserDTO> findByMemberInfoAll(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return sql.selectList("Member.findByMemberInfoAll", userInfoCompanyUserDTO ); }

    //공동 사용자 전체 가져오기  10.24
    public List<UserInfoCompanyUserDTO> findByCompanyUserAll(UserInfoCompanyUserDTO userInfoCompanyUserDTO) {
        return sql.selectList("Member.findByCompanyUserAll", userInfoCompanyUserDTO ); }

    //최초가입 여부 (마스터 코드 등록여부) "COM_CODE" 로 검색
    public MemberDTO findByComPanyCode(String COM_CODE) { return sql.selectOne("Member.findByCompanyCode", COM_CODE); }

    //최초가입 여부 (마스터 코드 등록여부) "BUS_NUMBER" 로 검색
    public MemberDTO findByBUS_NUMBER(String BUS_NUMBER) { return sql.selectOne("Member.findByBUSNUMBER", BUS_NUMBER); }


    //USER_INFO_COMPANY
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
