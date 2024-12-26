package com.dongwoo.SQM.auditMgmt.repository;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AuditCommonRepository {
	private final SqlSessionTemplate sql;

	public Map<String, String> getUserInfo(Map<String, Object> parameterMap) {
		return sql.selectOne("auditCommonMapper.getUserInfo", parameterMap);
	}
}
