package com.dongwoo.SQM.system.repository;

import com.dongwoo.SQM.system.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
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

    public MemberDTO findByMemberEmail(String loginEmail) { return sql.selectOne("Member.findByMemberEmail", loginEmail); }

    public MemberDTO findByMemberId(String loginId) { return sql.selectOne("Member.findByMemberId", loginId); }

    public int update(MemberDTO memberDTO) {
        return sql.update("Member.update", memberDTO);
    }
}
