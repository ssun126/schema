package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.common.service.CommonService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuditMgmtController {
    //ajax json 관련 뷰 참조변수.
//    @Resource(name="ajaxMainView")
//    MappingJackson2JsonView ajaxMainView;
//
    @Resource(name = "commonService")
    private CommonService commonService;

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
        log.info("???????????????????????????????????????"+param);
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

    public String changeNull(Object ob){
        if(ob == null) return "";
        return ob.toString();
    }
}
