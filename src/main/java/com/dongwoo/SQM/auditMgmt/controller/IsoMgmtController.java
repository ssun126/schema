package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.common.service.CommonService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONValue;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/api/iso")
public class IsoMgmtController {
	
    @Resource(name = "commonService")
    private CommonService commonService;

    @Resource(name = "auditCommonService")
    private AuditCommonService auditCommonService;

	@Value("${restfull.povis.url}")
	private String povisInterfaceURL;

	@Value("${Upload.path.pcnTemplate}")
	private String pcnTemplatePath;

	@Value("${Upload.path.audit}")
	private String auditPath;

	/**
     * AuditPlan getCodeList
     *
     * @param param
     * @param request
     * @return
     * @throws Exception
     */

	@PostMapping("/isoMgmt/getCodeList")
	public ResponseEntity<Map<String, Object>> getCodeList(@RequestParam(value = "param", required = false) String param, HttpServletRequest request) throws Exception {
		Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = commonService.jsonDataMap(param);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	HttpHeaders headers = new HttpHeaders();
    	ArrayList arrayList = new ArrayList();

    	try {
    		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	Map<String, String> tokenInfo = (Map<String, String>) request.getAttribute("TOKEN_INFO");
        	bodyMap.add("fnc", "GetCodeList");
        	bodyMap.add("pFlag", reqParam.get("pFlag").toString());
        	bodyMap.add("pGroupCd", reqParam.get("pGroupCd").toString());
        	RestTemplate restTemplate = new RestTemplate();
        	ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
			String jsonResponse = responseEn.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(jsonResponse);

			// Extract dataList
			JsonNode dataList = rootNode.path("dataList");
			// Iterate over the dataList
			if (dataList.isArray()) {
				for (JsonNode item : dataList) {
					arrayList.add(item);
				}
			}

			responseData.put("dataList", arrayList);
		} catch (Exception e) {
			log.info("Exception/Start(isoMgmt/getCodeList)============================>");
			log.info("param============================>");
			log.info(bodyMap.toString());
			log.info("",e);
			log.info("Exception/End(isoMgmt/getCodeList)==============================>");
		}
		return ResponseEntity.ok(responseData);  // JSON 응답 반환
	}
	/**
     * AuditPlan LIST
     *
     * @param param
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	@GetMapping("/isoMgmt/list/{param:.+}")
	public ResponseEntity<Map<String, Object>> isoMgmt(@PathVariable("param") String param,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = commonService.jsonDataMap(param);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	Map<String, String> tokenInfo = (Map<String, String>) request.getAttribute("TOKEN_INFO");
    	String multiLanguage ="ko";
    	if("EN".equals(tokenInfo.get("USER_LANG"))){
    		multiLanguage="en";
    	}else if("JA".equals(tokenInfo.get("USER_LANG"))){
    		multiLanguage="jp";
    	}else if("CN".equals(tokenInfo.get("USER_LANG"))){
    		multiLanguage="zh";
    	}else {
    		multiLanguage="ko";
    	}
    	HttpHeaders headers = new HttpHeaders();
    	ArrayList arrayList = new ArrayList();
    	try {
    		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "SDSISOV01");
        	bodyMap.add("flag", "L");
        	bodyMap.add("comp_cd", changeNull(reqParam.get("comp_cd")));
        	bodyMap.add("group_cd", changeNull(reqParam.get("group_cd")));
        	bodyMap.add("certify_type", changeNull(reqParam.get("certify_type")));
        	
        	//bodyMap.add("comp_cd", "dfd");
        	//bodyMap.add("group_cd", "dfd");
        	//bodyMap.add("certify_type", "dfd");

        	RestTemplate restTemplate = new RestTemplate();
        	ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
        	String list = responseEn.getBody();
        	/*if(list!=null){
	        	//povis 결과 리스트 데이터 컬럼을 DP Portal 식으로 맞춤
	        	list = list.replace("NCR_StatusNM", "L_Progress")
	        				.replace("NCR_No","L_NcrNo")
	        				.replace("TypeNM","L_AuditPlanType")
	        				.replace("NCR_TypeNM","L_NcrType")
	        				.replace("NCR_Subject","L_Title")
	        				.replace("NCR_Date","L_NcrPublishedDate")
	        				.replace("AuditorNM","Auditor")
	        				.replace("NCR_P_Plan_Date","L_CorrectiveActionPlanDate")
	        				.replace("NCR_R_Complete_Date","L_CorrectiveActionMakeDate")
	        				.replace("NCR_C_Confirm_Date","L_CompleteDate")
	        				.replace("NCR_L_AuditPlanType","L_NcrType") // 데이터 보정 처리

	        	;
        	}*/
        	if(list.equals("OK|상태변경성공")){
        		list=null;
        	}
        	if(list!=null){
        	//povis 결과 리스트 데이터 컬럼을 DP Portal 식으로 맞춤
        		list = list.replace("COMP_NM", "L_VendorName")
        				.replace("CERTIFY_TYPE", "L_IsoFileType")
        				.replace("CERTIFY_U_DATE", "L_IsoReplaceDate")
        				.replace("CERTIFY_E_DATE", "L_IsoEndDate")
        				.replace("CERTIFY_M_DATE", "L_IsoUploadDate")
        				 // 데이터 보정 처리
        	;
        	}

			responseData.put("LIST", list);
		} catch (Exception e) {
			log.info("Exception/Start(isoMgmt/list)============================>");
			log.info("param============================>");
			log.info(bodyMap.toString());
			log.info("",e);
			log.info("Exception/End(isoMgmt/list)==============================>");
		}

		return ResponseEntity.ok(responseData);  // JSON 응답 반환
	}
	/**
     * PCN getPcnInfo
     *
     * @param param
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

	@PostMapping("/isoMgmt/isoInsert")
	public ResponseEntity<Map<String, Object>> isoInsert(
			@RequestBody String param,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {

		Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = commonService.jsonDataMap(param);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	try {
    		 HttpHeaders headers = new HttpHeaders();
    		 HttpHeaders headers2 = new HttpHeaders();
    		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    		headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
    		headers2.setContentType(MediaType.MULTIPART_FORM_DATA);
    		headers2.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
    		
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "SDSISOM01M");
        	bodyMap.add("flag", "I");
        	//seq null or "" 일때 오류 발생
        	if("".equals(changeNull(reqParam.get("seq")))){
        		bodyMap.add("seq", "0");
        	}else{
        		bodyMap.add("seq", changeNull(reqParam.get("seq")));
        	}
        	bodyMap.add("group_cd", changeNull(reqParam.get("group_cd"))); //plant 코드
        	bodyMap.add("comp_cd", changeNull(reqParam.get("COM_CODE")));
        	bodyMap.add("comp_nm", changeNull(reqParam.get("COM_NAME")));
        	bodyMap.add("certify_type", changeNull(reqParam.get("certify_type"))); //ISO 종류
        	bodyMap.add("certify_u_date", changeNull(reqParam.get("certify_u_date"))); //갱신일
        	bodyMap.add("certify_e_date", changeNull(reqParam.get("certify_e_date"))); //만료일
        	bodyMap.add("certify_m_date", changeNull(reqParam.get("certify_m_date"))); //등록일
        	/*JSONObject fileJson = new JSONObject();
        	fileJson.put("fileJson", reqParam.get("file_info"));
        	JSONArray fileJsonAry = fileJson.getJSONArray("fileJson");
        	File file = null;

        	RestTemplate restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        	ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);

        	JSONObject jo = new JSONObject();
        	jo.put("test", responseEn.getBody());
        	JSONArray jarry = jo.getJSONArray("test");
        	Object test = JSONValue.parse(responseEn.getBody());

        	JSONObject rsultJsonObject = (JSONObject)jarry.get(0);
        	String isoSeq = rsultJsonObject.get("SEQ").toString();
        	log.info(rsultJsonObject.get("SEQ"));

        	*/
        	//++++++++++++++++++++++++++++++++실제 파일 통신+++++++++++++++++++++++++++++++
        	RestTemplate restTemplateFile = new RestTemplate();
        	restTemplateFile.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        	ResponseEntity<String> responseEnFile;
        	
        	/*
    		bodyMap = new LinkedMultiValueMap<String, Object>();
    		requestEntity = new HttpEntity<>(bodyMap, headers2);
    		bodyMap.add("file", new FileSystemResource(file));
    		bodyMap.add("fnc", "ISO_ATTACH");
        	bodyMap.add("seq", changeNull(rsultJsonObject.get("SEQ").toString()));*/
    		
        	/*for(int i=0; i<fileJsonAry.toArray().length; i++){
        		JSONObject fileInfo = (JSONObject) fileJsonAry.toArray()[i];
        		if(fileInfo.get("fileseq")!=null){
        			continue;
            	}
        		bodyMap = new LinkedMultiValueMap<String, Object>();
        		headers = new HttpHeaders();
        		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        		headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
        		requestEntity = new HttpEntity<>(bodyMap, headers);
        		
        		
            	bodyMap.add("fnc", "SDSISOM01M_ATTACH");
            	bodyMap.add("flag", "I");
            	bodyMap.add("iso_seq", isoSeq+"");
            	bodyMap.add("attach_seq", "0");
            	bodyMap.add("attach_size", changeNull(fileInfo.get("filesize")));
            	bodyMap.add("attach_filename", fileInfo.get("filename").toString());
            	bodyMap.add("attach_path", fileInfo.get("filepath").toString());
            	restTemplate = new RestTemplate();
            	restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            	responseEnFile = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
            	JSONObject jo2 = new JSONObject();
            	jo2.put("test", responseEnFile.getBody());
            	JSONArray jarry2 = jo2.getJSONArray("test");
            	JSONObject rsultJsonObject2 = (JSONObject)jarry2.get(0);
            	String file_seq = rsultJsonObject2.getString("seq");
            	
            	file = new File(PropUtils.get(Const.FILE_ISO_PATH)+"\\"+fileInfo.get("dpPortalFilePath"));
            	bodyMap = new LinkedMultiValueMap<String, Object>();
            	bodyMap.add("fnc", "ISO_ATTACH");
            	bodyMap.add("flag", "I");
            	bodyMap.add("seq", isoSeq+"");
            	bodyMap.add("attach_seq", file_seq);
            	bodyMap.add("pFlag", "filestream");
            	bodyMap.add("pGroupCd", reqParam.get("group_cd").toString());
            	bodyMap.add("file", new FileSystemResource(file));
            	headers = new HttpHeaders();
            	headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            	requestEntity = new HttpEntity<>(bodyMap, headers);
            	restTemplate = new RestTemplate();
            	responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
        	}*/

		} catch (Exception e) {
			log.info("Exception/Start(isoMgmt/auditNcrPlanInsert)============================>");
			log.info("param============================>");
			log.info(bodyMap.toString());
			log.info("",e);
			log.info("Exception/End(isoMgmt/auditNcrPlanInsert)==============================>");
		}

		return ResponseEntity.ok(responseData);
	}
	/**
     * PCN getPcnInfo
     *
     * @param param
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

	@PostMapping("/isoMgmt/isoDelete")
	public ResponseEntity<Map<String, Object>> delNcrResultFile(
			@RequestBody String param,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
		// JSON Return ResponseEntity<Map<String, Object>>
    	Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = commonService.jsonDataMap(param);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	ArrayList arrayList = new ArrayList();
    	try {
    		 HttpHeaders headers = new HttpHeaders();
    		 Charset utf8 = Charset.forName("UTF-8");

    		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    		headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "SDSISOM01M_ATTACH");
        	bodyMap.add("flag", "D");
        	bodyMap.add("iso_seq", changeNull(reqParam.get("iso_seq")));
        	bodyMap.add("attach_seq", changeNull(reqParam.get("attach_seq")));

        	RestTemplate restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        	ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);



		} catch (Exception e) {
			log.info("Exception/Start(isoMgmt/delNcrResultFile)============================>");
			log.info("param============================>");
			log.info(bodyMap.toString());
			log.info("",e);
			log.info("Exception/End(isoMgmt/delNcrResultFile)==============================>");
		}

		return ResponseEntity.ok(responseData);
	}
	@PostMapping("/isoMgmt/isoInfoDelete")
	public ResponseEntity<Map<String, Object>> isoInfoDelete(
			@RequestBody String param,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
		// JSON Return ResponseEntity<Map<String, Object>>
    	Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = commonService.jsonDataMap(param);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	ArrayList arrayList = new ArrayList();
    	try {
    		 HttpHeaders headers = new HttpHeaders();
    		 Charset utf8 = Charset.forName("UTF-8");

    		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    		headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "SDSISOM01M");
        	bodyMap.add("flag", "D");
        	bodyMap.add("seq", changeNull(reqParam.get("seq")));

        	RestTemplate restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        	ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);



		} catch (Exception e) {
			log.info("Exception/Start(isoMgmt/delNcrResultFile)============================>");
			log.info("param============================>");
			log.info(bodyMap.toString());
			log.info("",e);
			log.info("Exception/End(isoMgmt/delNcrResultFile)==============================>");
		}

		return ResponseEntity.ok(responseData);
	}
	/**
     * PCN firstUpsert
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

	@PostMapping("/isoMgmt/povisFileDownload")
	public void resultFileDownload(
			@RequestParam(value="fileName", required=false) String fileName,
			@RequestParam(value="isoSeq", required=false) String isoSeq,
			@RequestParam(value="attachSeq", required=false) String attachSeq,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	HttpHeaders headers = new HttpHeaders();
    	try {
    		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "RETURN_ATTACH_ISO");
        	bodyMap.add("iso_seq",isoSeq);
        	bodyMap.add("attach_seq",attachSeq);
        	RestTemplate restTemplate = new RestTemplate();
        	ResponseEntity<byte[]> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, byte[].class);

        	fileName = fileName.replaceAll(" ", "");
        	String docName = URLEncoder.encode(fileName,"UTF-8");
        	response.setHeader("Content-Disposition","attachment;filename=\"" + docName.replaceAll("\\_[0-9]*\\.", ".") +"\"");
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.setContentType("application/octer-stream");
			response.setHeader("Pragma", "no-cache;");
			response.setHeader("Expires", "-1;");
			response.setContentLength(responseEn.getBody().length);
			response.getOutputStream().write(responseEn.getBody());
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} catch (Exception e) {
			log.info("Exception/Start(isoMgmt/povisFileDownload)============================>");
			log.info("param============================>");
			log.info(bodyMap.toString());
			log.info("",e);
			log.info("Exception/End(isoMgmt/povisFileDownload)==============================>");
		}

	}
	/**
     * DP Portal pcnFileDownload
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	@RequestMapping(value="/isoMgmt/dpPortalFileDownload")
	public void dpPortalPcnFileDownload(
			@RequestParam(value="fileName", required=false) String fileName,
			@RequestParam(value="povisPath", required=false) String povisPath,
			@RequestParam(value="dpPortalPath", required=false) String dpPortalPath,
			@RequestParam(value="attachType", required=false) String attachType,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	HttpHeaders headers = new HttpHeaders();
    	try {
    		String rootPath =null;
        	if("PCNTEMPLATE".equals(attachType)){
        		rootPath = pcnTemplatePath+"\\";
        		fileName = dpPortalPath;
        	}else{
        		rootPath = auditPath +"\\";
        	}
    		File file = new File(rootPath+dpPortalPath);
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
			log.info("Exception/Start(isoMgmt/dpPortalFileDownload)============================>");
			log.info("param============================>");
			log.info(bodyMap.toString());
			log.info("",e);
			log.info("Exception/End(isoMgmt/dpPortalFileDownload)==============================>");
		}

	}
	
	@PostMapping("/isoMgmt/isoFileDetail")
	public ResponseEntity<Map<String, Object>> isoFileDetail(
			@RequestBody String param,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
		// JSON Return ResponseEntity<Map<String, Object>>
    	Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = commonService.jsonDataMap(param);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	ArrayList arrayList = new ArrayList();
    	try {
    		 HttpHeaders headers = new HttpHeaders();
    		 Charset utf8 = Charset.forName("UTF-8");

    		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    		headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "ATTACH_ISO_View");
        	bodyMap.add("flag", "L");
        	bodyMap.add("seq", changeNull(reqParam.get("seq")));

        	RestTemplate restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        	ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
        	log.info(responseEn.getBody());
			responseData.put("fileInfo", JSONValue.parse(responseEn.getBody()) );


		} catch (Exception e) {
			log.info("Exception/Start(isoMgmt/isoFileDetail)============================>");
			log.info("param============================>");
			log.info(bodyMap.toString());
			log.info("",e);
			log.info("Exception/End(isoMgmt/isoFileDetail)==============================>");
		}

		return ResponseEntity.ok(responseData);
	}
	
	
	public String changeNull(Object ob){
		if(ob == null) return "";
		return ob.toString();
	}
}