package com.dongwoo.SQM.common.repository;

import com.dongwoo.SQM.common.dto.CommonDTO;
import com.dongwoo.SQM.common.dto.DataSetDTO;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.support.WebApplicationContextUtils;


import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class CommonRepository {
	@Autowired
	private ServletContext servletContext;

	private final SqlSessionTemplate sql;

	public List<LinkedHashMap<?, ?>> getSimpleList(String queryId) {
		return sql.selectList(queryId);
	}

	public CommonDTO getStoredQuery(Map<String, Object> parameterMap) {
        return (CommonDTO) sql.selectOne("LcsCommonDAO.getStoredQuery", parameterMap);
    }

	public CommonDTO getStoredQueryObject(Map<String, Object> parameterMap) {
        return (CommonDTO) sql.selectOne("LcsCommonDAO.getStoredQueryObject", parameterMap);
    }

	public List<Map<String, Object>> getStoredQueryExecuteObject(String query, Map<String, Object> parameterMap) {
		return sql.selectList(query, parameterMap);
	}


	public  DataSetDTO getStoredQueryExecuteDataSet(String query, Map<String, Object> parameterMap) {
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(servletContext);

		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate((DataSource) ctx.getBean("dataSource"));

		Map<?, ?> rtnObj = namedParameterJdbcTemplate.query(query, parameterMap, new ResultSetExtractor<Map<?,?>>() {

			@Override
			public Map<String, Object> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				HashMap<String, Object> resultMap = new HashMap<String, Object>();

				ResultSetMetaData metadata = resultSet.getMetaData();
				int columnCount = metadata.getColumnCount();

				LinkedHashMap<String, Object> colData = new LinkedHashMap<String, Object>();
				Map<String, Object> columnData = null;
				for(int i = 1 ; i <= columnCount ; i++){
					columnData = new HashMap<String, Object>();
					columnData.put("ColumnName", metadata.getColumnName(i));
					columnData.put("DataType", metadata.getColumnTypeName(i));
					columnData.put("Alias", i - 1);
					colData.put(metadata.getColumnName(i), columnData);
				}

				ArrayList<LinkedHashMap<Object, Object>> rowData = new ArrayList<LinkedHashMap<Object, Object>>();
				LinkedHashMap<Object, Object> row = null;
				while (resultSet.next()) {
					row = new LinkedHashMap<Object, Object>();
					for(int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
						// String column = resultSet.getMetaData().getColumnName(i);
						// Object object = resultSet.getObject(i);
						row.put((i - 1), resultSet.getObject(i));
					}
					rowData.add(row);
				}

		        resultMap.put("Columns", colData);
		        resultMap.put("Records", rowData.size());
		        resultMap.put("Rows", rowData);

				return resultMap;
			}
		});

		 DataSetDTO rtnVo = new  DataSetDTO();
		rtnVo.setColumns((LinkedHashMap<String, Object>) rtnObj.get("Columns"));
		rtnVo.setRecords((Integer) rtnObj.get("Records"));
		rtnVo.setRows((ArrayList<LinkedHashMap<Object, Object>>) rtnObj.get("Rows"));

		return rtnVo;
	}
	

	public int executeUpdateQuery(String query, Map<String, Object> parameterMap) {
		return sql.update(query, parameterMap);
    }

	//LCS Common Code 조회
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getCommonCodeList(Map<String, Object> parameterMap) {
		return sql.selectList("LcsCommonDAO.getCommonCodeList", parameterMap);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getQueryForList(String sqlId, Map<?, ?> parameterMap) {
		return sql.selectList(sqlId, parameterMap);
	}

	public int insertHistoryTable(Map parameterMap) {
        return sql.insert(" Common.insertHistoryTable", parameterMap);
    }

	public int updateActivity(Map parameterMap) {
        return sql.update(" Common.updateActivity", parameterMap);
    }

    public List<LinkedHashMap<String, Object>> getVendorSimpleList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getVendorSimpleList", parameterMap);
	}

    public List<LinkedHashMap<String, Object>> getVendorList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getVendorList", parameterMap);
	}

    public List<LinkedHashMap<String, Object>> getMaterialList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getMaterialList", parameterMap);
	}

    public List<LinkedHashMap<String, Object>> getFactoryList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getFactoryList", parameterMap);
	}

    public List<LinkedHashMap<String, Object>> getYnList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getYnList", parameterMap);
	}

    public List<LinkedHashMap<String, Object>> getMaterialSimpleList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getMaterialSimpleList", parameterMap);
	}

    public List<LinkedHashMap<String, Object>> getMaterialFactoryList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getMaterialFactoryList", parameterMap);
	}

	public List<Object> searchCodeData(Map<String, Object> jsonMap) {
		return sql.selectList(" Common.searchCodeData", jsonMap);
	}

	public List<LinkedHashMap<String, Object>> getUserSimpleList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getUserSimpleList", parameterMap);
	}

	public LinkedHashMap<String, Object> getUserVendorString(Map<String, Object> parameterMap) {
		return sql.selectOne(" Common.getUserVendorString", parameterMap);
	}

	public Object insertUserAction(Map<String, Object> parameterMap) {
		return sql.insert(" Common.insertUserAction", parameterMap);
	}

	public int updateUserAction(Map<String, Object> parameterMap) {
		return sql.update(" Common.updateUserAction", parameterMap);
	}

	public String parseDate(String param) {
		return sql.selectOne(" Common.parseDate", param);
	}

	public String getMailTitle(String param) {
		return sql.selectOne(" Common.getMailTitle", param);
	}

	public String getMailContents(String param) {
		return sql.selectOne(" Common.getMailContents", param);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> authorityMenuMoveCheck(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.authorityMenuMoveCheck", parameterMap);
	}

	public LinkedHashMap<String, Object> authorityUserInfo(Map<String, Object> parameterMap) {
		return sql.selectOne(" Common.authorityUserInfo", parameterMap);
	}
	public List<LinkedHashMap<String, Object>> getAuthUserMenuFactoryList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getAuthUserMenuFactoryList", parameterMap);
	}
	public List<LinkedHashMap<String, Object>> getAuthMenuFactoryList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getAuthMenuFactoryList", parameterMap);
	}
	public List<LinkedHashMap<String, Object>> getAuthUserClassList(Map<String, Object> parameterMap) {
		return sql.selectList(" Common.getAuthUserClassList", parameterMap);
	}
}
