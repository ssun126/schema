package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.service.AuditPlanService;
import com.dongwoo.SQM.common.service.CommonService;
import com.dongwoo.SQM.common.util.Const;
import com.dongwoo.SQM.common.util.PropUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONValue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuditPlanMgmtController {

    @Resource(name = "commonService")
    private CommonService commonService;

	@Value("${restfull.povis.url}")
	private String povisInterfaceURL;
	/**
     * AuditPlan getCodeList
     *
     * @param param
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

	@PostMapping("/api/audit/auditPlanMgmt/getCodeList")
	public ResponseEntity<Map<String, Object>> getCodeList(@RequestParam(value = "param", required = false) String param, HttpServletRequest request) throws Exception {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> reqParam = commonService.jsonDataMap(param);
		MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
		HttpHeaders headers = new HttpHeaders();
		ArrayList arrayList = new ArrayList();
		try {
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
			bodyMap.add("fnc", "GetCodeList");
			bodyMap.add("pFlag", "PARTNER_AUDIT_TYPE");
			bodyMap.add("pGroupCd", "9999");
			bodyMap.add("USERID", "covision");
			bodyMap.add("SITEID", "DW01");
			RestTemplate restTemplate = new RestTemplate();
			try {
				log.info("requestEntity=====================================>" + requestEntity);
				ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
				log.info("responseEn=====================================>" + responseEn.getBody());

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

				response.put("dataList", arrayList);
			} catch (HttpClientErrorException | HttpServerErrorException ex) {
				System.out.println("HTTP Status: " + ex.getStatusCode());
				System.out.println("Response Body: " + ex.getResponseBodyAsString());
			} catch (Exception ex) {
				ex.printStackTrace();  // 다른 예외를 처리
			}

		} catch (Exception e) {
			log.info("Exception/Start(auditPlanMgmt/getCodeList)============================>");
			log.info(bodyMap.toString());
			log.info("", e);
			log.info("Exception/End(auditPlanMgmt/getCodeList)==============================>");
		}
		return ResponseEntity.ok(response);  // JSON 응답 반환
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
	@GetMapping("/api/audit/auditPlanMgmt/list/{param:.+}")
	public ResponseEntity<Map<String, Object>> auditPlanMgmt(@PathVariable("param") String param,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = reqToMap(request);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
		String multiLanguage ="ko";
    	HttpHeaders headers = new HttpHeaders();
    	ArrayList arrayList = new ArrayList();
    	try {
    		String povisPlantCode ="";
    		if("1200".equals(changeNull(reqParam.get("plant")))){
    			povisPlantCode="IF";//익산
    		}else if("1300".equals(changeNull(reqParam.get("plant")))){
    			povisPlantCode="PT";//평택
    		}
    		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "JTAUDIV01");
        	bodyMap.add("ap_no", changeNull(reqParam.get("ap_no")));//changeNull(reqParam.get("pGroupCd"))
        	bodyMap.add("ap_type", changeNull(reqParam.get("ap_type")));
        	bodyMap.add("ap_partnerscd", changeNull(reqParam.get("ap_partnerscd")));
        	bodyMap.add("ap_partnersnm", changeNull(reqParam.get("ap_partnersnm")));
        	bodyMap.add("plant", povisPlantCode);
        	bodyMap.add("ap_way", changeNull(reqParam.get("ap_way")));
        	bodyMap.add("wetpr", changeNull(reqParam.get("wetpr")));
        	bodyMap.add("year", changeNull(reqParam.get("year")));
        	bodyMap.add("lang", multiLanguage);

        	RestTemplate restTemplate = new RestTemplate();
        	ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
        	/*String list = responseEn.getBody();
        	if(list!=null){
	        	//povis 결과 리스트 데이터 컬럼을 DP Portal 식으로 맞춤
	        	list = list.replace("AM_No", "L_AuditNo")
	        				.replace("AP_Type","L_AuditPlanType")
	        				.replace("AP_Way","L_AuditPlanWay")
	        				.replace("AP_Date","L_AuditPlanDate")
	        				.replace("Make_EmpNM","L_CreatorName")
	        				.replace("AP_Status","L_Progress")
	        				.replace("AP_PartnersNM","L_Vendor")
	        				.replace("Audit_Date","L_AuditDate")
	        	;
        	}else{
        		list ="";
        	}*/
			responseData.put("LIST", responseEn.getBody());
		} catch (Exception e) {
			log.info("Exception/Start(auditPlanMgmt/list)============================>");
			log.info("",e);
			log.info("Exception/End(auditPlanMgmt/list)==============================>");
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

	@PostMapping("/api/audit/auditPlanMgmt/getAuditPlanInfo")
	public ResponseEntity<Map<String, Object>> getAuditPlanInfo(
			@RequestBody String param,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
		Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = reqToMap(request);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	ArrayList arrayList = new ArrayList();
    	Map<String, String> tokenInfo = (Map<String, String>) request.getAttribute("TOKEN_INFO");
    	try {
    		 HttpHeaders headers = new HttpHeaders();
    		 Charset utf8 = Charset.forName("UTF-8");
    		 if("EN".equals(tokenInfo.get("USER_LANG"))){
         		bodyMap.add("lang", "en");
         	}else if("JA".equals(tokenInfo.get("USER_LANG"))){
         		bodyMap.add("lang", "jp");
         	}else if("CN".equals(tokenInfo.get("USER_LANG"))){
         		bodyMap.add("lang", "zh");
         	}else {
         		bodyMap.add("lang", "ko");
         	}
    		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    		headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "JTAUDIM01");
        	bodyMap.add("ap_seq", reqParam.get("ap_seq"));

        	RestTemplate restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        	ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
			JSONObject jo = new JSONObject(responseEn.getBody());

			// Step 2: Process "test" array from response
			JSONArray jarry = jo.getJSONArray("test");
			JSONObject auditPlanInfo = jarry.getJSONObject(0);
			String amSeq = auditPlanInfo.optString("AM_Seq", "default");  // Using optString for safety


			bodyMap = new LinkedMultiValueMap<String, Object>();
        	requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "JTAUDIM02M_ATTACH");
        	bodyMap.add("flag", "L");
        	bodyMap.add("am_seq", amSeq);

        	restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        	responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
        	//jo = new JSONObject();
			responseData.put("auditPlanInfoCsFile", JSONValue.parse(responseEn.getBody()) );
        	
        	bodyMap = new LinkedMultiValueMap<String, Object>();
        	requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "JTAUDIM02");
        	bodyMap.add("flag", "L");
        	bodyMap.add("am_seq", amSeq);

        	restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        	responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
        	//jo = new JSONObject();
			responseData.put("auditResultInfo", JSONValue.parse(responseEn.getBody()) );
        	
        	bodyMap = new LinkedMultiValueMap<String, Object>();
        	requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "JTAUDIM02M_RESULT_ATTACH");
        	bodyMap.add("flag", "L");
        	bodyMap.add("am_seq", amSeq);

        	restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        	responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
        	//jo = new JSONObject();
			responseData.put("auditPlanInfoResultFile", JSONValue.parse(responseEn.getBody()) );

		} catch (Exception e) {
			log.info("Exception/Start(auditPlanMgmt/getAuditPlanInfo)============================>");
			log.info("",e);
			log.info("Exception/End(auditPlanMgmt/getAuditPlanInfo)==============================>");
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

	@PostMapping("/api/audit/auditPlanMgmt/getAuditPlanCsTemplate")
	public ResponseEntity<Map<String, Object>> getAuditPlanCsTemplate(
			@RequestBody String param,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
		Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = reqToMap(request);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	ArrayList arrayList = new ArrayList();
    	try {
    		 HttpHeaders headers = new HttpHeaders();
    		 Charset utf8 = Charset.forName("UTF-8");

    		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    		headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "JTMANAM02_ATTACH");

        	RestTemplate restTemplate = new RestTemplate();
        	restTemplate.getMessageConverters()
            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        	ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
        	JSONObject jo = new JSONObject();
			responseData.put("csTemplateList", JSONValue.parse(responseEn.getBody()) );


		} catch (Exception e) {
			log.info("Exception/Start(auditPlanMgmt/getAuditPlanCsTemplate)============================>");
			log.info("",e);
			log.info("Exception/End(auditPlanMgmt/getAuditPlanCsTemplate)==============================>");
		}

		return ResponseEntity.ok(responseData);
	}
	/**
     * PCN pcnFileDownload
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
	@GetMapping("/api/audit/auditPlanMgmt/csTemplateFileDownload")
	public void pcnFileDownload(
			@RequestParam(value="fileName", required=false) String fileName,
			@RequestParam(value="povisFlag", required=false) String povisFlag,
			@RequestParam(value="attachType", required=false) String attachType,
			@RequestParam(value="attachSeq", required=false) String attachSeq,
			@RequestParam(value="pcnSeq", required=false) String pcnSeq,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	HttpHeaders headers = new HttpHeaders();
    	try {
    		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	bodyMap.add("fnc", "RETURN_ATTACH_CHKSHTFM");
        	bodyMap.add("attach_seq",attachSeq);
        	RestTemplate restTemplate = new RestTemplate();
        	ResponseEntity<byte[]> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, byte[].class);
        	//fileName = "test.xlsx";
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
			log.info("Exception/Start(auditPlanMgmt/csTemplateFileDownload)============================>");
			log.info("",e);
			log.info("Exception/End(auditPlanMgmt/csTemplateFileDownload)==============================>");
		}

	}
	/**
     * PCN firstUpsert
     *
     * @param param
     * @param request
     * @param response
     * @return
     * @throws Exception
     */

	@PostMapping("/api/audit/auditPlanMgmt/csSave")
	public ResponseEntity<Map<String, Object>>  csSave(
			@RequestBody String param,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
		Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = reqToMap(request);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	try {
			HttpHeaders headers = new HttpHeaders();
			Charset utf8 = Charset.forName("UTF-8");
			JSONObject fileJson = new JSONObject();
			fileJson.put("fileJson", reqParam.get("file_info"));
			JSONArray fileJsonAry = fileJson.getJSONArray("fileJson");

// JSONArray의 크기만큼 반복문을 돌리며 처리
			for (int i = 0; i < fileJsonAry.length(); i++) {
				// JSONArray에서 각 객체를 가져오기
				JSONObject test2 = fileJsonAry.getJSONObject(i);

				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));

				bodyMap = new LinkedMultiValueMap<String, Object>();
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

				// 필요한 파라미터들 추가
				bodyMap.add("fnc", "JTAUDIM02M_ATTACH");
				bodyMap.add("flag", "I");
				bodyMap.add("am_seq", changeNull(reqParam.get("am_seq")));
				bodyMap.add("attach_seq", changeNull(test2.get("fileseq")));
				bodyMap.add("attach_filename", changeNull(test2.get("filename")));
				bodyMap.add("attach_path", changeNull(test2.get("dpPortalFilePath")));
				bodyMap.add("attach_size", changeNull(test2.get("filesize")));

				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
				ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);

				// 응답 데이터를 처리
				JSONObject jo = new JSONObject();
				responseData.put("auditPlanInfo", new JSONObject(responseEn.getBody()));

				jo.put("test", responseEn.getBody());
				JSONArray jarry = jo.getJSONArray("test");

				JSONObject auditPlanInfo = jarry.getJSONObject(0);

				// 파일 처리
				bodyMap = new LinkedMultiValueMap<String, Object>();
				File file = new File(PropUtils.get(Const.FILE_AUDIT_PATH) + "\\" + test2.get("dpPortalFilePath"));

				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));

				requestEntity = new HttpEntity<>(bodyMap, headers);
				bodyMap.add("fnc", "JTAUDIT02_ATTACH");
				bodyMap.add("am_seq", changeNull(reqParam.get("am_seq")));
				bodyMap.add("attach_seq", (i + 1) + "");
				bodyMap.add("file", new FileSystemResource(file));

				restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
				responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);

				// 디버깅 정보 출력
				System.out.println("sonDebug====>" + auditPlanInfo.toString());
			}
    		//auditPlanService.sendCsFileCompleteMail(reqParam);
		} catch (Exception e) {
			log.info("Exception/Start(auditPlanMgmt/csSave)============================>");
			log.info("",e);
			log.info("Exception/End(auditPlanMgmt/csSave)==============================>");
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

	@PostMapping("/api/audit/auditPlanMgmt/csDownload")
	public void csDownload(
			@RequestParam(value="fileName", required=false) String fileName,
			@RequestParam(value="amSeq", required=false) String amSeq,
			@RequestParam(value="attachSeq", required=false) String attachSeq,
			@RequestParam(value="povisFileType", required=false) String povisFileType,
			HttpServletRequest request,
			HttpServletResponse response
			) throws Exception {
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	HttpHeaders headers = new HttpHeaders();
    	try {
    		
    		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        	HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        	if(povisFileType.equals("plan")){
        		bodyMap.add("fnc", "RETURN_ATTACH_JTAUDIM02M");
        	}else if(povisFileType.equals("result")){
        		bodyMap.add("fnc", "RETURN_JTAUDIM02M_RESULT_ATTACH");
        	}
        	bodyMap.add("am_seq",amSeq);
        	bodyMap.add("attach_seq",attachSeq);
        	RestTemplate restTemplate = new RestTemplate();
        	ResponseEntity<byte[]> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, byte[].class);
        	//fileName = "test.xlsx";
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
			log.info("Exception/Start(auditPlanMgmt/csDownload)============================>");
			log.info("",e);
			log.info("Exception/End(auditPlanMgmt/csDownload)==============================>");
		}

	}
	/**
     * PCN getPcnInfo
     *
     * @param param
     * @param request
     * @return
     * @throws Exception
     */

	@PostMapping("/auditPlanMgmt/csDelete")
	public ResponseEntity<Map<String, Object>> csDelete(
			@RequestBody String param,
			HttpServletRequest request
			) throws Exception {
		Map<String, Object> responseData = new HashMap<>();
    	// JSON Parameter Parsing
    	Map<String, Object> reqParam = reqToMap(request);
    	MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
    	ArrayList arrayList = new ArrayList();
    	Map<String, String> tokenInfo = (Map<String, String>) request.getAttribute("TOKEN_INFO");
    	try {
			HttpHeaders headers = new HttpHeaders();
			Charset utf8 = Charset.forName("UTF-8");

			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
			for(int i=0; i<Integer.parseInt(changeNull(reqParam.get("count")));i++){
				bodyMap = new LinkedMultiValueMap<String, Object>();
				HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
				bodyMap.add("fnc", "JTAUDIM02M_ATTACH");
				bodyMap.add("flag", "D");
				bodyMap.add("am_seq", changeNull(reqParam.get("am_seq")));
				bodyMap.add("attach_seq", (i+1)+"");
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters()
				.add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
				ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
			}
		} catch (Exception e) {
			log.info("Exception/Start(auditPlanMgmt/csDelete)============================>");
			log.info("",e);
			log.info("Exception/End(auditPlanMgmt/csDelete)==============================>");
		}

		return ResponseEntity.ok(responseData);
	}
	
	public String changeNull(Object ob) {
		if (ob == null) return "";
		return ob.toString();
	}

	private String GetParam(HttpServletRequest request, String pName, String pDefault) {
		String ParamValue = request.getParameter(pName);

		if (ParamValue == null || ParamValue.isEmpty()) {
			ParamValue = pDefault;
		};

		return ParamValue;
	}

	private Map<String, Object> reqToMap(HttpServletRequest request) {
		Map<String, Object> reqParam = new HashMap<>();
		// 요청 파라미터를 모두 Map에 넣기
		Enumeration<String> parameterNames = request.getParameterNames();
		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);

			// 파라미터가 여러 값일 수 있으므로 배열로 저장
			if (paramValues.length == 1) {
				reqParam.put(paramName, paramValues[0]);
			} else {
				reqParam.put(paramName, paramValues);
			}
		}

		return reqParam;
	}
}