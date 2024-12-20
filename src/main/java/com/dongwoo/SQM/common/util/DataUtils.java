package com.dongwoo.SQM.common.util;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

public class DataUtils {

	public static Map<String, Object> getJsonDataSet(List<?> dataList) {
		if(dataList.size() == 0) {
			return null;
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> localDataList = (List<Map<String, Object>>) dataList;

		int columnCnt = 0;
    	Map<String, Object> rtnMap = new LinkedHashMap<String, Object>();
    	Map<String, Object> colMap = new LinkedHashMap<String, Object>();
    	Map<String, Object> colInfoMap = null;

    	Map<String, Object> data = localDataList.get(0);
    	Iterator<String> keys = data.keySet().iterator();
        while(keys.hasNext()){
            String key = keys.next();
    		colInfoMap = new LinkedHashMap<String, Object>();
    		colInfoMap.put("ColumnName", key);
    		colInfoMap.put("DataType", data.get(key) instanceof String ? "String" : data.get(key) instanceof Integer ? "Int16 " : "DateTime");
    		colInfoMap.put("Alias", columnCnt++);

    		colMap.put(key, colInfoMap);
        }

        List<LinkedHashMap<String, Object>> dataConvertList = new ArrayList<LinkedHashMap<String, Object>>();
        for(Map<String, Object> row : localDataList){
        	columnCnt = 0;
        	Map<String, Object> rowData = new LinkedHashMap<String, Object>();;
        	for(String key : data.keySet()){
        		rowData.put(String.valueOf(columnCnt++), (row.get(key)==null)?"":row.get(key));
        	}
        	dataConvertList.add((LinkedHashMap<String, Object>) rowData);
        }

        rtnMap.put("Columns", colMap);
        rtnMap.put("Records", dataList.size());
    	rtnMap.put("Rows", dataConvertList);

    	return rtnMap;
    }

	public static Map<String, Object> getJsonDataSet(Map<String, Object> data) throws SQLException {
		if(data == null) {
			return null;
		}

		int columnCnt = 0;
    	Map<String, Object> rtnMap = new LinkedHashMap<String, Object>();
    	Map<String, Object> colMap = new LinkedHashMap<String, Object>();
    	Map<String, Object> colInfoMap = null;

    	Iterator<String> keys = data.keySet().iterator();
        while(keys.hasNext()){
            String key = keys.next();
    		colInfoMap = new LinkedHashMap<String, Object>();
    		colInfoMap.put("ColumnName", key);
    		colInfoMap.put("DataType", data.get(key) instanceof String ? "String" : data.get(key) instanceof Integer ? "Int16 " : "DateTime");
    		colInfoMap.put("Alias", columnCnt++);

    		colMap.put(key, colInfoMap);
        }

        List<LinkedHashMap<String, Object>> dataConvertList = new ArrayList<LinkedHashMap<String, Object>>();
    	columnCnt = 0;
    	Map<String, Object> rowData = new LinkedHashMap<String, Object>();;
    	for(String key : data.keySet()){
    		rowData.put(String.valueOf(columnCnt++), data.get(key));
    	}
    	dataConvertList.add((LinkedHashMap<String, Object>) rowData);

        rtnMap.put("Columns", colMap);
        rtnMap.put("Records", 1);
    	rtnMap.put("Rows", dataConvertList);

    	return rtnMap;
    }

	public static void copy(final Object from, final Object to) {
        Map<String, Field> fromFields = analyze(from);
        Map<String, Field> toFields = analyze(to);
        fromFields.keySet().retainAll(toFields.keySet());
        for (Entry<String, Field> fromFieldEntry : fromFields.entrySet()) {
            final String name = fromFieldEntry.getKey();
            final Field sourceField = fromFieldEntry.getValue();
            final Field targetField = toFields.get(name);
            if(targetField.getType().isAssignableFrom(sourceField.getType())) {
                sourceField.setAccessible(true);
                if(Modifier.isFinal(targetField.getModifiers())) continue;
                targetField.setAccessible(true);
                try {
                    targetField.set(to, sourceField.get(from));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Can't access field!");
                }
            }
        }
    }

	private static Map<String, Field> analyze(Object object) {
        if(object == null) {
        	throw new NullPointerException();
        }

        Map<String, Field> map = new TreeMap<String, Field>();

        Class<?> current = object.getClass();
        for(Field field : current.getDeclaredFields()) {
            if(!Modifier.isStatic(field.getModifiers())) {
                if(!map.containsKey(field.getName())) {
                    map.put(field.getName(), field);
                }
            }
        }

        return map;
    }

	@SuppressWarnings("deprecation")
	public static <T> List<?> getMappingVO(Class<T> VO, List<Map<String, Object>> dataList){
		List<T> resultList = new ArrayList<T>();
		try {
			for(Map<String, Object> row : dataList) {
				T clazz = VO.newInstance();
				for(String key : row.keySet()) {
					for(Method method : clazz.getClass().getDeclaredMethods()) {
						if(method.getName().toUpperCase().equals("SET" + key.toUpperCase())) {
							if(method.getParameterTypes()[0].getName().equals("java.lang.String")) {
								method.invoke(clazz, (String) row.get(key));
							} else if(method.getParameterTypes()[0].getName().equals("java.util.Date")) {
								if(row.get(key) == null || String.valueOf(row.get(key)).equals("")) {
									method.invoke(clazz, new Date());
								} else {
									method.invoke(clazz, new Date(String.valueOf(row.get(key))));
								}

							} else if(method.getParameterTypes()[0].getName().equals("int")) {
								if(row.get(key) == null || String.valueOf(row.get(key)).equals("")) {
									method.invoke(clazz, 0);
								} else {
									method.invoke(clazz, (Integer) row.get(key));
								}
							}
						}
					}
				}
				resultList.add(clazz);
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return resultList;
	}
/*
	public static ModelAndView getParseMsgVO(ModelAndView mav, MessageDataVO msgVO) {
		mav.addObject("CODE", msgVO.getCode());
		mav.addObject("COMMAND", msgVO.getCommand());
		mav.addObject("COMMANDTYPE", msgVO.getCommandType());
		mav.addObject("DATADIC", msgVO.getDataDic());
		mav.addObject("DATALIST", msgVO.getDataList());
		mav.addObject("DATASET", msgVO.getDataSet());
		mav.addObject("DATATABLE", msgVO.getDataTable());
		mav.addObject("EXCEPTIONMESSAGE", msgVO.getExceptionMessage());
		mav.addObject("HASHLIST", msgVO.getHashList());
		mav.addObject("IPADDRESS", msgVO.getIpAddress());
		mav.addObject("ISSUCCESS", msgVO.getIsSuccess());
		mav.addObject("LANGUAGE", msgVO.getLanguage());
		mav.addObject("MESSAGE", msgVO.getMessage());
		mav.addObject("OBJECT", msgVO.getObject());
		mav.addObject("QUERYID", msgVO.getQueryId());
		mav.addObject("SITEID", msgVO.getSiteId());
		mav.addObject("TID", msgVO.getTId());
		mav.addObject("USERID", msgVO.getUserId());
		return mav;
	}*/

	public static String getTransactionId(String hashCode) {
		return DateUtils.getTodayAll() + hashCode;
	}

	public static Map<String, Object> getJsonDataSetGantt(List<?> dataList) {
		if(dataList.size() == 0) {
			return null;
		}

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> localDataList = (List<Map<String, Object>>) dataList;

		int columnCnt = 0;
    	Map<String, Object> rtnMap = new LinkedHashMap<String, Object>();
    	Map<String, Object> colMap = new LinkedHashMap<String, Object>();
    	Map<String, Object> colInfoMap = new LinkedHashMap<String, Object>();

    	Map<String, Object> data = localDataList.get(0);

		colInfoMap.put("ColumnName", "category");
		colInfoMap.put("DataType", "String");
		colInfoMap.put("Alias", 0);

		colMap.put("category", colInfoMap);

		colInfoMap = new LinkedHashMap<String, Object>();

		colInfoMap.put("ColumnName", "segments");
		colInfoMap.put("DataType", "Array");
		colInfoMap.put("Alias", 1);

		colMap.put("segments", colInfoMap);

        List<LinkedHashMap<String, Object>> dataConvertList = new ArrayList<LinkedHashMap<String, Object>>();

        for(Map<String, Object> row : localDataList){
        	columnCnt = 0;
        	Map<String, Object> rowData = new LinkedHashMap<String, Object>();
        	Map<String, Object> rowDataSeg = new LinkedHashMap<String, Object>();
        	List<LinkedHashMap<String, Object>> dataConvertListSeg = new ArrayList<LinkedHashMap<String, Object>>();

        	for(String key : data.keySet()){
        		if(columnCnt == 0)
        			rowData.put("0", row.get(key));
        		else if(columnCnt == 1)
        			rowDataSeg.put("start", row.get(key));
        		else if(columnCnt == 2)
        			rowDataSeg.put("end", row.get(key));
        		else if(columnCnt == 3)
        			rowDataSeg.put("duration", row.get(key));
        		else if(columnCnt == 4)
        			rowDataSeg.put("actual_start", row.get(key));
        		else if(columnCnt == 5)        		{
        			rowDataSeg.put("actual_end", row.get(key));
        			dataConvertListSeg.add((LinkedHashMap<String, Object>) rowDataSeg);

        			rowData.put("1", dataConvertListSeg);
        			break;
        		}
        		columnCnt ++;
        	}
        	dataConvertList.add((LinkedHashMap<String, Object>) rowData);
        }

        rtnMap.put("Columns", colMap);
        rtnMap.put("Records", dataList.size());
    	rtnMap.put("Rows", dataConvertList);

    	return rtnMap;
    }


	public static Map<String, Object> convertObj2Map(Object obj) throws
	IllegalAccessException,
	IllegalArgumentException,
	InvocationTargetException
	{
	    Class<?> pomclass = obj.getClass();
	    pomclass = obj.getClass();
	    Method[] methods = obj.getClass().getMethods();


	    Map<String, Object> map = new HashMap<String, Object>();
	    for (Method m : methods) {
	       if (m.getName().startsWith("get") && !m.getName().startsWith("getClass")) {
	          Object value = (Object) m.invoke(obj);
	          map.put(m.getName().substring(3), (Object) value);
	       }
	    }
	    return map;
	}

	public static Map<String, Object> convertObj2MapCap(Object obj) throws
	IllegalAccessException,
	IllegalArgumentException,
	InvocationTargetException
	{
	    Class<?> pomclass = obj.getClass();
	    pomclass = obj.getClass();
	    Method[] methods = obj.getClass().getMethods();


	    Map<String, Object> map = new HashMap<String, Object>();
	    for (Method m : methods) {
	       if (m.getName().startsWith("get") && !m.getName().startsWith("getClass")) {
	          Object value = (Object) m.invoke(obj);
	          map.put(m.getName().substring(3).toUpperCase(), (Object) value);
	       }
	    }
	    return map;
	}

	public static List<HashMap<String, String>> orderByAsc(ArrayList<HashMap<String, String>> list, final String key) {
	    //오름차순 정렬
	    Collections.sort(list, new Comparator<HashMap<String, String >>() {
	    	@Override
	        public int compare(HashMap<String, String> first,
	                HashMap<String, String> second) {

	            return first.get(key).compareTo(second.get(key));
	        }
	    });
	    //System.out.println(list);
	    return list;
	}

	public static List<HashMap<String, String>> orderByDesc(ArrayList<HashMap<String, String>> list, final String key) {
	    //내림차순 정렬
	    Collections.sort(list, new Comparator<HashMap<String, String >>() {
	        @Override
	        public int compare(HashMap<String, String> first,
	                HashMap<String, String> second) {

	            int firstValue = Integer.valueOf(first.get(key));
	            int secondValue = Integer.valueOf(second.get(key));

	            // 내림차순 정렬
	            if (firstValue > secondValue) {
	                return -1;
	            } else if (firstValue < secondValue) {
	                return 1;
	            } else /* if (firstValue == secondValue) */ {
	                return 0;
	            }
	        }
	    });
	    //System.out.println(list);
	    return list;
	}


	public static Map ConverObjectToMap(Object obj){
		try {
			//Field[] fields = obj.getClass().getFields(); //private field는 나오지 않음.
			Field[] fields = obj.getClass().getDeclaredFields();
			Map resultMap = new HashMap();
			for(int i=0; i<=fields.length-1;i++){
				fields[i].setAccessible(true);
				resultMap.put(fields[i].getName(), fields[i].get(obj));
			}
			return resultMap;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static Object convertMapToObject(Map map, Object objClass){
		String keyAttribute = null;
		String setMethodString = "set";
		String methodString = null;
		Iterator itr = map.keySet().iterator();
		while(itr.hasNext()){
			keyAttribute = (String) itr.next();
			methodString = setMethodString+keyAttribute.substring(0,1).toUpperCase()+keyAttribute.substring(1);
			try {
				Method[] methods = objClass.getClass().getDeclaredMethods();
				for(int i=0;i<=methods.length-1;i++){
					if(methodString.equals(methods[i].getName())){
						System.out.println("invoke : "+methodString);
						methods[i].invoke(objClass, map.get(keyAttribute));
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return objClass;
	}

	public static boolean isEmpty(Map map, String key) {
		if (!map.containsKey(key)) {
			return true;
		}

		if (null == map.get(key) || "" == map.get(key).toString()) {
			return true;
		}

		return false;
	}

/*	public static Map<String, Object> setTokenInfo(HttpServletRequest request, Map<String, Object> reqParam) {
		// TOKEN INFO Parsing
		Map<String, String> tokenInfo = (Map<String, String>) request.getAttribute("TOKEN_INFO");

		reqParam.put("TOKEN_USER_ID", tokenInfo.get("USER_ID"));
		reqParam.put("TOKEN_USER_TYPE", tokenInfo.get("USER_TYPE"));
		reqParam.put("TOKEN_USER_LANG", tokenInfo.get("USER_LANG"));
		reqParam.put("TOKEN_SITE_ID", tokenInfo.get("SITE_ID"));

		return reqParam;
	}*/

}