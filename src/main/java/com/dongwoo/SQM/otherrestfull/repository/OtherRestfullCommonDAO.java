package com.dongwoo.SQM.otherrestfull.repository;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class OtherRestfullCommonDAO{
	private final SqlSessionTemplate sql;

	public List<Object> selectVendorList(Map<String, Object> parameterMap) {
		return sql.selectList("otherRestfullCommonMapper.getVendorList", parameterMap);
	}
	public List<Object> selectUserList(Map<String, Object> parameterMap) {
		return sql.selectList("otherRestfullCommonMapper.getUserList", parameterMap);
	}
}
