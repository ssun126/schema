package com.dongwoo.SQM.otherrestfull.service;

import java.util.List;
import java.util.Map;

public interface OtherRestfullService {
	 public List<Object> getVendorList(Map<String,Object> parameterMap) throws Exception ;
	 public List<Object> getUserList(Map<String,Object> parameterMap) throws Exception ;
}
