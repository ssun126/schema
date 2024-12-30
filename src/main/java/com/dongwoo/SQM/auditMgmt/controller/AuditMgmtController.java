package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.common.service.CommonService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONValue;
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
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuditMgmtController {
    @Resource(name = "commonService")
    private CommonService commonService;

    @Resource(name = "auditCommonService")
    private AuditCommonService auditCommonService;

    @Value("${restfull.povis.url}")
    private String povisInterfaceURL;

    @Value("${Upload.path.audit}")
    private String auditFilePath;

    @PostMapping("/api/audit/auditNcrMgmt/getCodeList")
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
            log.info("Exception/Start(auditNcrMgmt/getCodeList)============================>");
            log.info(bodyMap.toString());
            log.info("", e);
            log.info("Exception/End(auditNcrMgmt/getCodeList)==============================>");
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
    @GetMapping("/api/audit/auditNcrMgmt/list/{param}")
    public ResponseEntity<Map<String, Object>> auditNcrMgmt(@PathVariable("param") String param,
                                                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        // JSON Parameter Parsing
        Map<String, Object> reqParam = commonService.jsonDataMap(param);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();

        Map<String, Object> responseData = new HashMap<>();
        HttpHeaders headers = new HttpHeaders();
        ArrayList arrayList = new ArrayList();
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
            bodyMap.add("fnc", "JTAUDIV03");
            bodyMap.add("ncr_no", changeNull(reqParam.get("ncr_no")));
            bodyMap.add("type", changeNull(reqParam.get("type")));
            bodyMap.add("partnerscd", changeNull(reqParam.get("partnerscd")));
            bodyMap.add("partnersnm", changeNull(reqParam.get("partnersnm")));
            bodyMap.add("ncr_type", changeNull(reqParam.get("ncr_type")));
            bodyMap.add("year", changeNull(reqParam.get("year")));
            bodyMap.add("ncr_status", changeNull(reqParam.get("ncr_status")));

            RestTemplate restTemplate = new RestTemplate();
            log.info("requestEntity=====================================>" + requestEntity);
            ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
            log.info("responseEn=====================================>" + responseEn.getBody());
            responseData.put("LIST", responseEn.getBody());
        } catch (Exception e) {
            log.info("Exception/Start(auditNcrMgmt/list)============================>");
            log.info("param============================>");
            log.info(bodyMap.toString());
            log.info("", e);
            log.info("Exception/End(auditNcrMgmt/list)==============================>");
        }

        return ResponseEntity.ok(responseData);  // JSON 응답 반환
    }


    /**
     * PCN getPcnInfo
     *
     * @return
     * @throws Exception
     */
    @PostMapping("/api/audit/auditNcrMgmt/getAuditNcrInfo")
    public ResponseEntity<Map<String, Object>> getAuditNcrInfo(@RequestParam(value = "param", required = false) String param, HttpServletRequest request) throws Exception {
        Map<String, Object> responseData = new HashMap<>();
        // JSON Parameter Parsing
        Map<String, Object> reqParam = reqToMap(request);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
        ArrayList arrayList = new ArrayList();

        Object ncrSeq = GetParam(request,"ncr_seq", "");
        try {
            HttpHeaders headers = new HttpHeaders();
            Charset utf8 = Charset.forName("UTF-8");
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
            bodyMap.add("fnc", "JTAUDIM03");
            bodyMap.add("ncr_seq",ncrSeq);
            log.info("ncr_seq ==================="+ncrSeq);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
            log.info("responseEn ==================="+JSONValue.parse(responseEn.getBody()));

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseEn.getBody());
            Object ncrDif = rootNode.get("NCR_DIF").asText();
            responseData = getTabInfoInnerFn(ncrSeq,ncrDif,reqParam);
            responseData.put("auditNcrInfo", JSONValue.parse(responseEn.getBody()));
        } catch (Exception e) {
            log.info("Exception/Start(auditNcrMgmt/getAuditNcrInfo)============================>");
            log.info("param============================>");
            log.info(bodyMap.toString());
            log.info("", e);
            log.info("Exception/End(auditNcrMgmt/getAuditNcrInfo)==============================>");
        }
        return ResponseEntity.ok(responseData);
    }

    /**
     * PCN getPcnInfo
     *
     * @param param
     * @return
     * @throws Exception
     */

    @PostMapping("/api/audit/auditNcrMgmt/getAuditNcrInfoTab")
    public ResponseEntity<Map<String, Object>> getAuditNcrInfoTab(@RequestParam(value = "param", required = false) String param, HttpServletRequest request) throws Exception {
        Map<String, Object> responseData = new HashMap<>();
        // JSON Parameter Parsing
        Map<String, Object> reqParam = reqToMap(request);
        try {
            String ncr_seq = GetParam(request,"ncr_seq", "");
            String dif = GetParam(request,"dif", "");
            responseData = (Map<String, Object>) getTabInfoInnerFn(ncr_seq, dif, reqParam);

        } catch (Exception e) {
            log.info("Exception/Start(auditNcrMgmt/getAuditNcrInfoTab)============================>");
            log.info("", e);
            log.info("Exception/End(auditNcrMgmt/getAuditNcrInfoTab)==============================>");
        }

        return ResponseEntity.ok(responseData);
    }

    public Map<String, Object> getTabInfoInnerFn(Object ncrSeq, Object dif, Map<String, Object> reqParam) {
        Map<String, Object> responseData = new HashMap<>();
        // JSON Parameter Parsing
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
        ArrayList arrayList = new ArrayList();
        try {

            Map<String, String> userInfo = auditCommonService.getUserInfoMap(reqParam);
            responseData.put("userInfo", userInfo);

            HttpHeaders headers = new HttpHeaders();
            Charset utf8 = Charset.forName("UTF-8");
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));


            requestEntity = new HttpEntity<>(bodyMap, headers);
            bodyMap.add("fnc", "JTAUDIM03_PLAN");
            bodyMap.add("dif", dif);
            bodyMap.add("typ", "L");
            bodyMap.add("ncr_seq", ncrSeq);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);

            responseData.put("auditNcrInfoPlan", JSONValue.parse(responseEn.getBody()));

            bodyMap = new LinkedMultiValueMap<String, Object>();
            requestEntity = new HttpEntity<>(bodyMap, headers);
            bodyMap.add("fnc", "JTAUDIM03_RESULT");
            bodyMap.add("dif", dif);
            bodyMap.add("typ", "L");
            bodyMap.add("ncr_seq", ncrSeq);
            restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
            responseData.put("auditNcrInfoResult", JSONValue.parse(responseEn.getBody()));

            bodyMap = new LinkedMultiValueMap<String, Object>();
            bodyMap.add("fnc", "JTAUDIM03_RESULT_ATTACH");
            bodyMap.add("flag", "L");
            bodyMap.add("dif", dif);
            bodyMap.add("ncr_seq", ncrSeq);
            requestEntity = new HttpEntity<>(bodyMap, headers);
            responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);

            responseData.put("auditNcrInfoResultFile", JSONValue.parse(responseEn.getBody()));

        } catch (Exception e) {
            log.info("Exception/Start(getTabInfoInnerFn)============================>");
            log.info("param============================>");
            log.info(bodyMap.toString());
            log.info("", e);
            log.info("Exception/End(getTabInfoInnerFn)==============================>");
        }

        return responseData;
    }



    /**
     * PCN getPcnInfo
     *
     * @param param
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/auditNcrMgmt/auditNcrPlanInsert")
    public ResponseEntity<Map<String, Object>>  auditNcrPlanInsert(@RequestParam(value = "param", required = false) String param,HttpServletRequest request) throws Exception {
        Map<String, Object> responseData = new HashMap<>();
        // JSON Parameter Parsing
        Map<String, Object> reqParam = reqToMap(request);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
            HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
            bodyMap.add("fnc", "JTAUDIM03_PLAN");
            bodyMap.add("dif", changeNull(reqParam.get("dif")));
            bodyMap.add("ncr_seq", changeNull(reqParam.get("ncr_seq")));
            if("".equals(changeNull(reqParam.get("NCR_P_Make_Date")))){
                bodyMap.add("typ", changeNull("I"));
            }else{
                bodyMap.add("typ", changeNull(reqParam.get("typ")));
            }
            System.out.println(reqParam.get("typ").toString().equals("U"));
            bodyMap.add("ncr_p_plan_date", changeNull(reqParam.get("ncr_p_plan_date")));
            bodyMap.add("ncr_p_make_empcd", changeNull(reqParam.get("ncr_p_make_empcd")));
            bodyMap.add("ncr_p_make_empnm", changeNull(reqParam.get("ncr_p_make_empnm")));
            bodyMap.add("ncr_p_email", changeNull(reqParam.get("ncr_p_email")));
            bodyMap.add("ncr_p_comment", changeNull(reqParam.get("ncr_p_comment")));


            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
            bodyMap.remove("typ");
            bodyMap.add("typ", changeNull(reqParam.get("typ")));
            responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
            Object test = JSONValue.parse(responseEn.getBody());
            if(reqParam.get("typ").toString().equals("U")){
                reqParam.put("sendType", "plan");
                //auditCommonService.commonSendMail(reqParam);
            }

        } catch (Exception e) {
            log.info("Exception/Start(auditNcrMgmt/auditNcrPlanInsert)============================>");
            log.info("param============================>");
            log.info(bodyMap.toString());
            log.info("",e);
            log.info("Exception/End(auditNcrMgmt/auditNcrPlanInsert)==============================>");
        }

        return ResponseEntity.ok(responseData);
    }
    /**
     * PCN getPcnInfo
     *
     * @param param
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/auditNcrMgmt/auditNcrResultInsert", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> auditNcrResultInsert(
            @RequestBody String param,
            HttpServletRequest request
    ) throws Exception {
        // JSON Return
        Map<String, Object> responseData = new HashMap<>();
        // JSON Parameter Parsing
        Map<String, Object> reqParam = reqToMap(request);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
        ArrayList arrayList = new ArrayList();
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpHeaders headers2 = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
            headers2.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers2.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
            HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
            bodyMap.add("fnc", "JTAUDIM03_RESULT");
            bodyMap.add("dif", changeNull(reqParam.get("dif")));
            bodyMap.add("ncr_seq", changeNull(reqParam.get("ncr_seq")));
            bodyMap.add("typ", changeNull(reqParam.get("typ")));
            bodyMap.add("ncr_r_complete_date", changeNull(reqParam.get("ncr_r_complete_date")));
            bodyMap.add("ncr_r_comment", changeNull(reqParam.get("ncr_r_comment")));


            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
           // Object test = JSONValue.parse(responseEn.getBody());

            String jsonResponse = responseEn.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // Extract dataList
            JsonNode dataList = rootNode.path("file_info");
            // Iterate over the dataList
            if (dataList.isArray()) {
                for (JsonNode item : dataList) {
                    //arrayList.add(item);
                    bodyMap = new LinkedMultiValueMap<String, Object>();
                    requestEntity = new HttpEntity<>(bodyMap, headers);

                    File file = new File(auditFilePath+"\\"+item.get("dpPortalFilePath"));
                    bodyMap.add("fnc", "JTAUDIM03_RESULT_ATTACH");
                    bodyMap.add("flag","I");
                    bodyMap.add("dif", changeNull(reqParam.get("dif")));
                    bodyMap.add("ncr_seq", changeNull(reqParam.get("ncr_seq")));
                    bodyMap.add("attach_seq", changeNull(item.get("fileseq")));
                    bodyMap.add("attach_size", file.length()+"");
                    bodyMap.add("attach_filename", changeNull(item.get("filename")));
                    bodyMap.add("attach_path", changeNull(item.get("dpPortalFilePath")));
                    restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters()
                            .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
                    responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
                    if("U".equals(changeNull(reqParam.get("typ")))) {
                        bodyMap = new LinkedMultiValueMap<String, Object>();
                        requestEntity = new HttpEntity<>(bodyMap, headers2);
                        file = new File(auditFilePath + "\\" + item.get("dpPortalFilePath"));
                        bodyMap.add("file", new FileSystemResource(file));
                        bodyMap.add("fnc", "JTAUDIT_ATTACH");
                        bodyMap.add("dif", changeNull(reqParam.get("dif")));
                        bodyMap.add("ncr_seq", changeNull(reqParam.get("ncr_seq")));
                        if ("".equals(changeNull(item.get("fileseq")))) {
                            bodyMap.add("attach_seq", "");
                        } else {
                            bodyMap.add("attach_seq", changeNull(item.get("fileseq")));
                        }

                        restTemplate = new RestTemplate();
                        restTemplate.getMessageConverters()
                                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
                        responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
                    }
                }
            }

            if(reqParam.get("typ").equals("U")){
                reqParam.put("sendType", "result");
                //auditCommonService.commonSendMail(reqParam);
            }
        } catch (Exception e) {
            log.info("Exception/Start(auditNcrMgmt/auditNcrResultInsert)============================>");
            log.info("param============================>");
            log.info(bodyMap.toString());
            log.info("",e);
            log.info("Exception/End(auditNcrMgmt/auditNcrResultInsert)==============================>");
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
