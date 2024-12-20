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
//    @Resource(name = "commonService")
//    private CommonService commonService;
//
    @Value("${restfull.povis.url}")
    private String povisInterfaceURL;

    @PostMapping("/api/audit/auditNcrMgmt/getCodeList")
    public ResponseEntity<Map<String, Object>> getCodeList(@RequestParam(value="param", required = false) String param) {
        Map<String, Object> response = new HashMap<>();
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<String, Object>();
        HttpHeaders headers = new HttpHeaders();
        ArrayList arrayList = new ArrayList();
        try {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String,Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
            bodyMap.add("fnc", "GetCodeList");
            bodyMap.add("pFlag", "PARTNER_AUDIT_NCRSTA");
            bodyMap.add("pGroupCd", "9999");
            bodyMap.add("USERID", "covision");
            bodyMap.add("SITEID", "DW01");
            RestTemplate restTemplate = new RestTemplate();
            log.info("povisInterfaceURL============================>"+povisInterfaceURL);
            log.info("requestEntity============================>"+requestEntity);
            //ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);

            try {
                ResponseEntity<String> responseEn = restTemplate.exchange(povisInterfaceURL, HttpMethod.POST, requestEntity, String.class);
                System.out.println("Response: " + responseEn.getBody());

                String jsonResponse = responseEn.getBody();
                System.out.println("jsonResponse: " + jsonResponse);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                // Extract dataList
                JsonNode dataList = rootNode.path("dataList");
                // Iterate over the dataList
                if (dataList.isArray()) {
                    for (JsonNode item : dataList) {
                        log.info("arrayList============================>"+item.toString());
                        arrayList.add(item);
                    }
                }

                /*JSONObject jo = new JSONObject();
                jo.put("test", responseEn.getBody());
                log.info("test============================>"+responseEn.getBody());
                JSONArray jarry = jo.getJSONArray("test");

                for(int i=0; i<jarry.toArray().length; i++){
                    arrayList.add(jarry.toArray()[i]);
                }*/
                response.put("dataList", arrayList);
            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                System.out.println("HTTP Status: " + ex.getStatusCode());
                System.out.println("Response Body: " + ex.getResponseBodyAsString());
            } catch (Exception ex) {
                ex.printStackTrace();  // 다른 예외를 처리
            }

        } catch (Exception e) {
            log.info("Exception/Start(auditNcrMgmt/getCodeList)============================>");
            log.info("param============================>");
            log.info(bodyMap.toString());
            log.info("",e);
            log.info("Exception/End(auditNcrMgmt/getCodeList)==============================>");
        }
        return ResponseEntity.ok(response);  // JSON 응답 반환
    }

    /**
     * AuditPlan getCodeList
     *
     * @param param
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/api/audit/auditNcrMgmt/getCodeList__")
    public ModelAndView getCodeList(@RequestParam("param")String param, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // JSON Return ModelAndView
      /*  ModelAndView rtnMav = new ModelAndView(ajaxMainView);
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
            JSONObject jo = new JSONObject();
            jo.put("test", responseEn.getBody());
            JSONArray jarry = jo.getJSONArray("test");
            int multiLanguage=0;
            if("EN".equals(tokenInfo.get("USER_LANG"))){
                multiLanguage=1;
            }else if("JA".equals(tokenInfo.get("USER_LANG"))){
                multiLanguage=2;
            }else if("CN".equals(tokenInfo.get("USER_LANG"))){
                multiLanguage=3;
            }else {
                multiLanguage=0;
            }
            for(int i=0; i<jarry.toArray().length; i++){
                arrayList.add(jarry.toArray()[i]);
                JSONObject test = (JSONObject) jarry.toArray()[i];
                String[] tes = test.get("name").toString().split(";");
                if(tes.length>multiLanguage){
                    test.put("name", test.get("name").toString().split(";")[multiLanguage]);
                }else{
                    test.put("name", "");
                }
            }
            rtnMav.addObject("dataList", arrayList);
        } catch (Exception e) {
            log.info("Exception/Start(auditNcrMgmt/getCodeList)============================>");
            log.info("param============================>");
            log.info(bodyMap.toString());
            log.info("",e);
            log.info("Exception/End(auditNcrMgmt/getCodeList)==============================>");
        }

        return rtnMav;*/
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());  // JSON 응답을 위한 뷰 설정
        modelAndView.addObject("param", param);
        return modelAndView;
    }
}
