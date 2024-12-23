package com.dongwoo.SQM.otherrestfull.service.impl;

import com.dongwoo.SQM.otherrestfull.repository.OtherRestfullCommonDAO;
import com.dongwoo.SQM.otherrestfull.service.OtherRestfullService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("otherRestfullService")
public class OtherRestfullServiceImpl implements OtherRestfullService {
	//DAO
    @Resource(name = "otherRestfullCommonDAO")
    private OtherRestfullCommonDAO otherRestfullCommonDAO;

	@Override
	public List<Object> getVendorList(Map<String, Object> parameterMap)
			throws Exception {
		return otherRestfullCommonDAO.selectVendorList(parameterMap);
	}

	@Override
	public List<Object> getUserList(Map<String, Object> parameterMap)
			throws Exception {
		return otherRestfullCommonDAO.selectUserList(parameterMap);
	}


}
