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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/api/audit/auditNcrMgmt/getCodeList")
    public ResponseEntity<Map<String, Object>> getCodeList(@RequestParam(value="param", required = false) String param, HttpServletRequest request) throws Exception {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> reqParam = commonService.jsonDataMap(param);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
        HttpHeaders headers = new HttpHeaders();
        ArrayList arrayList = new ArrayList();
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
            bodyMap.add("fnc", "GetCodeList");
            bodyMap.add("pFlag",  "PARTNER_AUDIT_TYPE");
            bodyMap.add("pGroupCd", "9999");
            bodyMap.add("USERID", "covision");
            bodyMap.add("SITEID", "DW01");
            RestTemplate restTemplate = new RestTemplate();
            try {
                log.info("requestEntity=====================================>"+requestEntity);
                ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
                log.info("responseEn=====================================>"+responseEn.getBody());

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
            log.info("",e);
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
            HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
            bodyMap.add("fnc", "JTAUDIV03");
            bodyMap.add("ncr_no", changeNull(reqParam.get("ncr_no")));
            bodyMap.add("type", changeNull(reqParam.get("type")));
            bodyMap.add("partnerscd", changeNull(reqParam.get("partnerscd")));
            bodyMap.add("partnersnm", changeNull(reqParam.get("partnersnm")));
            bodyMap.add("ncr_type", changeNull(reqParam.get("ncr_type")));
            bodyMap.add("year", changeNull(reqParam.get("year")));
            bodyMap.add("ncr_status", changeNull(reqParam.get("ncr_status")));

            RestTemplate restTemplate = new RestTemplate();
            log.info("requestEntity=====================================>"+requestEntity);
            ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
            log.info("responseEn=====================================>"+responseEn.getBody());
            /*String list = responseEn.getBody();
            if(list!=null){
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
            responseData.put("LIST", responseEn.getBody());
        } catch (Exception e) {
            log.info("Exception/Start(auditNcrMgmt/list)============================>");
            log.info("param============================>");
            log.info(bodyMap.toString());
            log.info("",e);
            log.info("Exception/End(auditNcrMgmt/list)==============================>");
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
    @PostMapping("/getAuditNcrInfo")
    public ResponseEntity<Map<String, Object>> getAuditNcrInfo(
            @RequestBody String param,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        Map<String, Object> responseData = new HashMap<>();
        // JSON Parameter Parsing
        Map<String, Object> reqParam = commonService.jsonDataMap(param);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
        ArrayList arrayList = new ArrayList();
        try {
            HttpHeaders headers = new HttpHeaders();
            Charset utf8 = Charset.forName("UTF-8");

            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
            HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
            bodyMap.add("fnc", "JTAUDIM03");
            bodyMap.add("ncr_seq", reqParam.get("ncr_seq"));

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
            responseData.put("auditNcrInfo",JSONValue.parse(responseEn.getBody()));

        } catch (Exception e) {
            log.info("Exception/Start(auditNcrMgmt/getAuditNcrInfo)============================>");
            log.info("param============================>");
            log.info(bodyMap.toString());
            log.info("",e);
            log.info("Exception/End(auditNcrMgmt/getAuditNcrInfo)==============================>");
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

    @RequestMapping(value="/auditNcrMgmt/getAuditNcrInfoTab", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getAuditNcrInfoTab(
            @RequestBody String param,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        Map<String, Object> responseData = new HashMap<>();
        // JSON Parameter Parsing
        Map<String, Object> reqParam = commonService.jsonDataMap(param);
        try {

            responseData = (Map<String, Object>) getTabInfoInnerFn(reqParam.get("ncr_seq"),reqParam.get("dif"),reqParam);

        } catch (Exception e) {
            log.info("Exception/Start(auditNcrMgmt/getAuditNcrInfoTab)============================>");
            log.info("",e);
            log.info("Exception/End(auditNcrMgmt/getAuditNcrInfoTab)==============================>");
        }

        return ResponseEntity.ok(responseData);
    }

    public ResponseEntity<Map<String, Object>> getTabInfoInnerFn(Object ncrSeq, Object dif, Map<String,Object> reqParam){
        Map<String, Object> responseData = new HashMap<>();
        // JSON Parameter Parsing
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
        ArrayList arrayList = new ArrayList();
        try {

            Map<String,String>userInfo = auditCommonService.getUserInfoMap(reqParam);
            responseData.put("userInfo", userInfo);

            HttpHeaders headers = new HttpHeaders();
            Charset utf8 = Charset.forName("UTF-8");
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
            HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
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

            responseData.put("auditNcrInfoPlan", JSONValue.parse(responseEn.getBody()) );

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
            responseData.put("auditNcrInfoResult", JSONValue.parse(responseEn.getBody()) );

            bodyMap = new LinkedMultiValueMap<String, Object>();
            bodyMap.add("fnc", "JTAUDIM03_RESULT_ATTACH");
            bodyMap.add("flag", "L");
            bodyMap.add("dif", dif);
            bodyMap.add("ncr_seq", ncrSeq);
            requestEntity = new HttpEntity<>(bodyMap, headers);
            responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);

            JSONValue.parse(responseEn.getBody());
            responseData.put("auditNcrInfoResultFile", JSONValue.parse(responseEn.getBody()) );

        } catch (Exception e) {
            log.info("Exception/Start(getTabInfoInnerFn)============================>");
            log.info("param============================>");
            log.info(bodyMap.toString());
            log.info("",e);
            log.info("Exception/End(getTabInfoInnerFn)==============================>");
        }

        return ResponseEntity.ok(responseData);
    }
    

    public String changeNull(Object ob){
        if(ob == null) return "";
        return ob.toString();
    }
}
