package com.dongwoo.SQM.common.repository;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MailReciveSettingRepository{
	private final SqlSessionTemplate sql;
	
	public List<Object> searchUser(Map<String, Object> jsonMap) {
		return sql.selectList("mailReciveSettingMapper.searchUser",jsonMap);
	}
	public List<Object> setComboBoxMenu(Map<String, Object> jsonMap) {
		return sql.selectList("mailReciveSettingMapper.setComboBoxMenu", jsonMap);
	}
	public int insertMailReciveUser(Map<String, Object> jsonMap) {
		return sql.insert("mailReciveSettingMapper.insertMailReciveUser", jsonMap);
	}
	public List<Object> selectMailReciveList(Map<String, Object> jsonMap) {
		return sql.selectList("mailReciveSettingMapper.selectMailReciveList", jsonMap);
	}
	public int deleteMailReciveUser(Map<String, Object> jsonMap) {
		return sql.delete("mailReciveSettingMapper.deleteMailReciveUser", jsonMap);
	}
	public List<LinkedHashMap<String,Object>> getEmailTargetUser(Map<String, Object> jsonMap) {
		return sql.selectList("mailReciveSettingMapper.getEmailTargetUser", jsonMap);
	}
	public List<LinkedHashMap<String,Object>> getJobCombo(Map<String, Object> jsonMap) {
		return sql.selectList("mailReciveSettingMapper.getJobCombo", jsonMap);
	}
}