package com.dongwoo.SQM.common.service.impl;

import com.dongwoo.SQM.common.dto.CommonDTO;
import com.dongwoo.SQM.common.repository.CommonRepository;
import com.dongwoo.SQM.common.service.CommonService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("commonService")
public class CommonServiceImpl implements CommonService {

    private CommonRepository commonRepository;


    public Map<String, Object> jsonDataMap(String json) throws Exception {
        Map<String, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        if (null != json && !"".equals(json)) {
            map = mapper.readValue(json, new TypeReference<HashMap<String, Object>>() {
            });
        }

        return map;
    }

	public List<HashMap<String, Object>> jsonDataList(String json) throws Exception {
		return new ObjectMapper().readValue(json, new TypeReference<List<HashMap<String, Object>>>() {
		});
	}

    public CommonDTO getStoredQuery(Map<String, Object> parameterMap) {
    	if ("STOREDQUERYSEARCH".equals(parameterMap.get("COMMANDTYPE"))) {
    		CommonDTO sq = commonRepository.getStoredQueryObject(parameterMap);
    		sq.setQueryString("SELECT * FROM (" + sq.getQueryString() + ") WHERE " + parameterMap.get("p_columnname") + " LIKE '" + parameterMap.get("p_columnvalue") + "%'");
    		return sq;
    	} else {
    		return commonRepository.getStoredQuery(parameterMap);
    	}
    }

    public CommonDTO getStoredQueryObject(Map<String, Object> parameterMap) {
        return commonRepository.getStoredQuery(parameterMap);
    }

    public List<Map<String, Object>> getStoredQueryExecuteObject(String query, Map<String, Object> parameterMap) {
    	List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
    	List<Map<String, Object>> list = commonRepository.getStoredQueryExecuteObject(query, parameterMap);

    	Map<String, Object> objectMap = new HashMap<String, Object>();

    	String compareId = null;

    	ArrayList list2 = new ArrayList();
    	for (int i = 0; i < list.size(); i++) {
    		if (compareId == null) {
    			// 처음엔 Data가 없으므로 무조건 list에 담는다
    			list2.add(list.get(i));
    			compareId = (String) list.get(i).get("MENUID");
    		} else {
    			if (compareId.equals(list.get(i).get("MENUID"))) {
    				// 이전 Data의 MENUID와 같으면 list에 계속 추가
    				list2.add(list.get(i));
    			}
    		}
			if ((i + 1) < list.size()) {
				// 다음 컬럼이 있는지 확인
				if (!compareId.equals(list.get(i + 1).get("MENUID"))) {
					// 다음 컬럼의 MENUID와 기존 Data의 MENUID를 비교해서 다르면 objectMap에 등록 및 compareId / list2 초기화
					objectMap.put(compareId, list2);
					compareId = null;
					list2 = new ArrayList();
				}
			} else {
				// objectMap에 등록 및 compareId / list2 초기화
				objectMap.put(compareId, list2);
				compareId = null;
				list2 = new ArrayList();
			}
    	}
    	rtnList.add(objectMap);

        return rtnList;
    }


	public List<LinkedHashMap<?, ?>> getSimpleList(String queryId) {
		return commonRepository.getSimpleList(queryId);
	}

    @Override
    public Map<String, Object> executeUpdateQuery(String query, Map<String, Object> parameterMap) {
        int result = commonRepository.executeUpdateQuery(query, parameterMap);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("ROWS", result);
        return resultMap;
    }

    //LCS Common Code 조회
    public List<Map<String, Object>> getCommonCodeList(Map<String, Object> parameterMap) {
    	return commonRepository.getCommonCodeList(parameterMap);
    }

    @Override
    public int insertHistoryTable(Map map) throws Exception {

        int resultInt = 0;

        // 삭제 처리
        if(!"".equals(map.get("deleteActivity"))) {
			commonRepository.updateActivity(map);
        }

        resultInt = commonRepository.insertHistoryTable(map);

        return resultInt;
    }

    @Override
    public List<LinkedHashMap<String, Object>> getVendorSimpleList(Map<String, Object> parameterMap) {
		return commonRepository.getVendorSimpleList(parameterMap);
	}

    @Override
    public List<LinkedHashMap<String, Object>> getVendorList(Map<String, Object> parameterMap) {
		return commonRepository.getVendorList(parameterMap);
	}

    @Override
    public List<LinkedHashMap<String, Object>> getMaterialList(Map<String, Object> parameterMap) {
		return commonRepository.getMaterialList(parameterMap);
	}

    @Override
    public List<LinkedHashMap<String, Object>> getFactoryList(Map<String, Object> parameterMap) {
		return commonRepository.getFactoryList(parameterMap);
	}

    @Override
    public List<LinkedHashMap<String, Object>> getYnList(Map<String, Object> parameterMap) {
		return commonRepository.getYnList(parameterMap);
	}

    @Override
    public List<LinkedHashMap<String, Object>> getMaterialSimpleList(Map<String, Object> parameterMap) {
		return commonRepository.getMaterialSimpleList(parameterMap);
	}

    @Override
    public List<LinkedHashMap<String, Object>> getMaterialFactoryList(Map<String, Object> parameterMap) {
		return commonRepository.getMaterialFactoryList(parameterMap);
	}

    @Override
    public List<Object> searchCodeData(Map<String, Object> jsonMap) {
    	return commonRepository.searchCodeData(jsonMap);
    }

    @Override
    public List<LinkedHashMap<String, Object>> getUserSimpleList(Map<String, Object> parameterMap) {
    	return commonRepository.getUserSimpleList(parameterMap);
    }

    @Override
    public LinkedHashMap<String, Object> getUserVendorString(Map<String, Object> parameterMap) {
		return commonRepository.getUserVendorString(parameterMap);
	}

    @Override
	public Object insertUserAction(Map<String, Object> parameterMap) {
		return commonRepository.insertUserAction(parameterMap);
	}

    @Override
	public int updateUserAction(Map<String, Object> parameterMap) {
    	return commonRepository.updateUserAction(parameterMap);
	}

    @Override
    public List<String> getMailContents(String mailType) {
    	ArrayList<String> mailContents = new ArrayList<String>();

    	if (mailType == "MEMBER_REG") {
    		mailContents.add(commonRepository.getMailTitle("T_MailMemRegTitle"));
    		mailContents.add(commonRepository.getMailContents("T_MailMemRegMsg"));
    	} else if (mailType == "MEMBER_CERT") {
    		mailContents.add(commonRepository.getMailTitle("T_MailMemCertTitle"));
    		mailContents.add(commonRepository.getMailContents("T_MailMemCertMsg"));
    	} else if (mailType == "COA_REG") {
    		mailContents.add(commonRepository.getMailTitle("T_MailCOARegTitle"));
    		mailContents.add(commonRepository.getMailContents("T_MailCOARegMsg"));
    	} else if (mailType == "CERT_NUMBER") {
    		mailContents.add(commonRepository.getMailTitle("T_MailCertTitle"));
    		mailContents.add(commonRepository.getMailContents("T_MailCertMsg"));
    	}

    	return mailContents;
    }

	@Override
	public boolean authorityMenuMoveCheck(
			Map<String, Object> parameterMap) {
		Map <String,Object>map = commonRepository.authorityUserInfo(parameterMap);
		if("AU".equals(map.get("USER_TYPE"))){
			return true;
		}
		List list = commonRepository.authorityMenuMoveCheck(parameterMap);
		if(list.size()>0){
			return true;
		}
		return false;
	}

	@Override
    public List<LinkedHashMap<String, Object>> getAuthUserMenuFactoryList(Map<String, Object> parameterMap) {
		Map <String,Object>map = commonRepository.authorityUserInfo(parameterMap);
		List<LinkedHashMap<String, Object>> resultList = null;
		try{
			if("AU".equals(map.get("USER_TYPE"))){
				resultList = commonRepository.getAuthMenuFactoryList(parameterMap);
			}else{
				resultList = commonRepository.getAuthUserMenuFactoryList(parameterMap);
			}
		}catch(Exception e){
			resultList = commonRepository.getFactoryList(parameterMap);
		}
		return resultList;
	}

	@Override
    public List<LinkedHashMap<String, Object>> getAuthUserClassList(Map<String, Object> parameterMap) {
		Map <String,Object>map = commonRepository.authorityUserInfo(parameterMap);
		if("AU".equals(map.get("USER_TYPE"))){
			return commonRepository.getAuthUserClassList(parameterMap);
		}
		return commonRepository.getAuthUserClassList(parameterMap);
	}
}
