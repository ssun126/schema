package com.dongwoo.SQM.common.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface CommonService {

    //json 데이터를 Map<String, Object>으로 변환
    public Map<String, Object> jsonDataMap(String json) throws Exception ;

    //json 데이터를 List<Map<String, String>>으로 변환
    public List<HashMap<String, Object>> jsonDataList(String json) throws Exception;

    Map<String, Object> executeUpdateQuery(String query, Map<String, Object> parameterMap);

    int insertHistoryTable(Map map) throws Exception;

    List<LinkedHashMap<String, Object>> getVendorSimpleList(Map<String, Object> parameterMap);

    List<LinkedHashMap<String, Object>> getVendorList(Map<String, Object> parameterMap);

    List<LinkedHashMap<String, Object>> getMaterialList(Map<String, Object> parameterMap);

    List<LinkedHashMap<String, Object>> getFactoryList(Map<String, Object> parameterMap);

    List<LinkedHashMap<String, Object>> getYnList(Map<String, Object> parameterMap);

    List<LinkedHashMap<String, Object>> getMaterialSimpleList(Map<String, Object> parameterMap);

    List<LinkedHashMap<String, Object>> getMaterialFactoryList(Map<String, Object> parameterMap);

    List<Object> searchCodeData(Map<String, Object> jsonMap);

    List<LinkedHashMap<String, Object>> getUserSimpleList(Map<String, Object> parameterMap);

    LinkedHashMap<String, Object> getUserVendorString(Map<String, Object> parameterMap);

    Object insertUserAction(Map<String, Object> parameterMap);

    int updateUserAction(Map<String, Object> parameterMap);

    List<String> getMailContents(String mailType);

    boolean authorityMenuMoveCheck(
            Map<String, Object> parameterMap);

    List<LinkedHashMap<String, Object>> getAuthUserMenuFactoryList(Map<String, Object> parameterMap);

    List<LinkedHashMap<String, Object>> getAuthUserClassList(Map<String, Object> parameterMap);
}
