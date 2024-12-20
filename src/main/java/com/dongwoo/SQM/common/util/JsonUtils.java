package com.dongwoo.SQM.common.util;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {

	public Map<String, Object> getJsonToMap(String jsonString) {
		Map<String, Object> map = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();

		if (null != jsonString && !"".equals(jsonString)) {
			try{
				map = mapper.readValue(jsonString, new TypeReference<HashMap<String, Object>>(){});
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return map;
	}
}
