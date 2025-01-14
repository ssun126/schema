package com.dongwoo.SQM.otherrestfull.controller;

import com.dongwoo.SQM.common.service.CommonService;
import com.dongwoo.SQM.otherrestfull.service.OtherRestfullService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/otherRestfull")
public class OtherRestfullController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource(name = "commonService")
    private CommonService commonService;
    @Resource(name = "otherRestfullService")
    private OtherRestfullService otherRestfullService;

    @RequestMapping(value="/povis/test", method = {RequestMethod.POST, RequestMethod.GET})
	public ResponseEntity<Map<String, Object>> testHelloworld(
			HttpServletRequest request,
			HttpServletResponse response
	) throws Exception{
		Map<String, Object> responseData = new HashMap<String, Object>();
    	try {
			responseData.put("msg", "RESTFull TEST SUCCESS");
    	} catch (Exception ex) {
    		responseData.put("msg", "RESTFull FAIL");
    		throw ex;
    	}

    	return ResponseEntity.ok(responseData);
	}

	@RequestMapping(value="/povis/audit", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> povisAuditPost(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestBody String param
	) throws Exception{
		Map<String, Object> responseData = new HashMap<String, Object>();
		Map<String, Object> reqParam = commonService.jsonDataMap(param);

		// 요청된 URI
		String requestURI = request.getRequestURI().split("%7B")[0];
		logger.info(">>>>> [" + requestURI + " Start] [param " + reqParam + "]");

    	try {
    		responseData.put("fnc",   String.valueOf(reqParam.get("fnc"))  );
    		responseData.put("msg", "RESTFull SUCCESS");
    		String fnc = reqParam.get("fnc").toString();
			if(fnc.equals("null")){
				throw new Exception("/audit 요청, 파라미터 비었음!");
			}
    		if("vendorList".equals(reqParam.get("fnc").toString())){
    			List<?> resultList = otherRestfullService.getVendorList(reqParam);
    			responseData.put("result", resultList);
    		}else if("userList".equals(reqParam.get("fnc").toString())){
    			List<?> resultList = otherRestfullService.getUserList(reqParam);
    			responseData.put("result", resultList);
    		}
    	} catch (Exception ex) {
    		responseData.put("msg", "RESTFull FAIL");
			logger.info("Exception/Start(/povis/audit - povis interchange)============================>");
			logger.info(ex.getMessage());
			logger.info("Exception/End(/povis/audit - end)==============================>");
    		throw ex;
    	}

    	return ResponseEntity.ok(responseData);
	}
	/**
	 * 로그인 및 단말기 등록 실행.
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/push/appdown")
	public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	try {
    		String rootPath =null;
      		//rootPath ="D:\\DPPORTAL\\STORAGE\\PUSH\\";
      		rootPath ="D:\\SQM\\STORAGE\\PUSH\\";
    		//rootPath ="/home/tomcat/InstallAppFile/";
      		String fileName = "dongwooPushApp_REAL_20190108.apk";
    		File file = new File(rootPath+fileName);
    		byte fileByte[] = FileUtils.readFileToByteArray(file);
    		String docName = URLEncoder.encode(fileName,"UTF-8");
        	response.setHeader("Content-Disposition","attachment;filename=\"" + docName.replaceAll("\\_[0-9]*\\.", ".") +"\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.setContentType("application/octer-stream");
			response.setContentLength(fileByte.length);
			response.getOutputStream().write(fileByte);
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} catch (Exception e) {

		}
	}
	public String changeNull(Object ob){
		if(ob == null) return "";
		return ob.toString();
	}
	//restful get 방식 예시
/*	@RequestMapping(value="/povis/audit", method = RequestMethod.GET)
	public ResponseEntity<Map<String, Object>> povisAuditGet(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam Map<String, String> param
	) throws Exception{
		System.out.println("start======================>");
		String fnc =param.get("fnc");
		ResponseEntity<Map<String, Object>> rtnMav = new ResponseEntity<Map<String, Object>>(ajaxMainView);
		Map<String, Object> reqParam = new HashMap<String,Object>();
    	try {
    		responseData.put("fnc",  fnc);
    		responseData.put("msg", "RESTFull SUCCESS");
    		if("vendorList".equals(fnc)){
    			responseData.put("result", otherRestfullService.getVendorList(reqParam));
    		}
    	} catch (Exception ex) {
    		responseData.put("msg", "RESTFull FAIL");
    		throw ex;
    	}

    	return ResponseEntity.ok(responseData);
	}*/
}
