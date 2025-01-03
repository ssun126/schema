package com.dongwoo.SQM.qualityCtrl.controller;

import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.qualityCtrl.dto.coaMgmtDTO;
import com.dongwoo.SQM.qualityCtrl.service.coaMgmtService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.system.dto.ComPanyCodeDTO;
import com.dongwoo.SQM.system.dto.UserInfoCompanyDTO;
import com.dongwoo.SQM.system.dto.UserInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class coaMgmtController {

    private final coaMgmtService coaMgmtService;

    private String GetParam(HttpServletRequest request, String pName, String pDefault) {
        String ParamValue = request.getParameter(pName);

        if (ParamValue == null || ParamValue.isEmpty()) {
            ParamValue = pDefault;
        };

        return ParamValue;
    }

    @PostMapping("/admin/qualityCtrl/userList")
    public String userList(Model model) {
        return "coaMgmt/apiUserList";
    }

    @PostMapping("/admin/qualityCtrl/getUserList")
    @ResponseBody
    public List<HashMap> getUserList(HttpServletRequest req) {
        String code = req.getParameter("searchUserid");
        String name = req.getParameter("searchUserName");
        return coaMgmtService.getUserList(code,name);
    }


    @GetMapping("/admin/qualityCtrl/coaMgmt")
    public String coaList(Model model) {
        //등록일  // 기본값 오늘날짜. sStartDate

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String sStartDate = today.format(formatter);

        model.addAttribute("sStartDate", sStartDate);

        List<BaseCodeDTO> deptList = coaMgmtService.GetBaseCode("COA_STATUS");
        model.addAttribute("coaStatusList", deptList);

        List<BaseCodeDTO> plantList = coaMgmtService.GetBaseCodePLANT("PLANT");
        model.addAttribute("coaPlantList", plantList);

        return "coaMgmt/coaList";
    }

    //COA LIST 검색
    @PostMapping("/admin/qualityCtrl/getCOAList")
    public ResponseEntity<?> getcoaList(@RequestBody coaMgmtDTO coaMgmtdto) {
        try {
            System.out.println("Received scoreMgmtDTO: " + coaMgmtdto);

//            reqParam.put("TOKEN_USER_ID", tokenInfo.get("USER_ID"));
//            reqParam.put("TOKEN_USER_TYPE", tokenInfo.get("USER_TYPE"));
//            reqParam.put("TOKEN_USER_LANG", tokenInfo.get("USER_LANG"));
//            reqParam.put("TOKEN_SITE_ID", tokenInfo.get("SITE_ID"));

            //로그인 사용자 정보
            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_gubun = user.getUSER_GUBUN();
            String user_id = user.getUsername();


            coaMgmtdto.setTOKEN_USER_TYPE("AU");   //user_gubun :  관리자 , 사용자 권한 구분 확인
            coaMgmtdto.setTOKEN_USER_LANG("KR");   //언어 설정 확인.
            coaMgmtdto.setTOKEN_USER_ID("tykkim@lg.com"); //user_id 유저 매핑 해야됨.   test ===  >   tester  ,tykkim@lg.com


//            String vendorNameStr = (String) reqParam.get("vendorName");
//            List<String> vendorNameList = new ArrayList<String>();
//
//            if (!StringUtils.isEmpty(vendorNameStr)) {
//                if (vendorNameStr.indexOf("|") > -1) {
//                    String[] vendorNameArr = vendorNameStr.split("|");
//                    for (int i = 0 ; i < vendorNameArr.length ; i++) {
//                        vendorNameList.add(vendorNameArr[i]);
//                    }
//                    coaMgmtdto.setvendorNameList(vendorNameList);
//                }
//            }


//            public int deleteCompanyUser(String comCode,int useridx , List<Integer> companyUseridxList) {
//                Map<String, Object> params = new HashMap<>();
//                params.put("COM_CODE", comCode);
//                params.put("USER_IDX", useridx);
//                params.put("list", companyUseridxList);
//                return sql.delete("Member.deleteUserinfoCompanyUser", params);
//            }
//
//             <delete id="deleteUserinfoCompanyUser" parameterType="map">
//                    DELETE FROM SC_USER_INFO_COMPANY_USER
//            WHERE COM_CODE = #{COM_CODE} AND USER_IDX =#{USER_IDX}
//            AND COM_USER_IDX NOT IN
//        <foreach collection="list" item="COM_USER_IDX" open="(" separator="," close=")">
//            #{COM_USER_IDX}
//        </foreach>
//    </delete>


            List<coaMgmtDTO> coaList = coaMgmtService.getCOAList(coaMgmtdto);

            return ResponseEntity.ok(coaList);

        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }

    //성적서 팝업
    @PostMapping("/admin/qualityCtrl/popCoa")
    public String popCoa(Model model ,HttpServletRequest request) {


       String coa_id = GetParam(request,"COA_ID", "")  ;
       String vendor_id  = GetParam(request,"VENDOR_ID", "") ;
       String material_id = GetParam(request,"MATERIAL_ID", "");
       String factory_id =GetParam(request,"FACTORY_ID", "");
       String lot_no  = GetParam(request,"LOT_NO", "");

        coaMgmtDTO coaParamDto = new coaMgmtDTO();
        coaParamDto.setCOA_ID(coa_id);
        coaParamDto.setVENDOR_ID(vendor_id);
        coaParamDto.setMATERIAL_ID(material_id);
        coaParamDto.setFACTORY_ID(factory_id);
        coaParamDto.setLOT_NO(lot_no);

        //test
           coaParamDto.setCOA_ID("COA-17040515102694");
           coaParamDto.setVENDOR_ID("1000428");
           coaParamDto.setMATERIAL_ID("RCAD0010");
           coaParamDto.setFACTORY_ID("1300");
           coaParamDto.setLOT_NO("058116091026-2");
        //

        coaMgmtDTO coaDETAIL = coaMgmtService.getCOADetailTitle(coaParamDto);
        model.addAttribute("DETAIL", coaDETAIL);
        System.out.println("select coaDETAIL: " + coaDETAIL);

        return "coaMgmt/popCoa";
    }

    //성적서 팝업 스팩리스트 검색
    @PostMapping("/admin/qualityCtrl/Detail")
    public ResponseEntity<?> caoDetail(@RequestBody coaMgmtDTO coaMgmtdto ,Model model) {
        try {
            System.out.println("Received coaMgmtdto: " + coaMgmtdto);

            //스펙 리스트
            List<coaMgmtDTO> specList = coaMgmtService.getCOADetailSpec(coaMgmtdto);
            System.out.println("select SPEC: " + specList);

            return ResponseEntity.ok(specList);

        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }


    //저장
    @PostMapping("/admin/qualityCtrl/updateVendorComment")
    @ResponseBody
    public Map<String, String> updateVendorComment(
            @RequestParam("COA_ID") String coa_id
            ,@RequestParam("LOT_NO") String lot_no
            ,@RequestParam("VENDOR_COMMENT") String vendor_comment
            , Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        try {

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_gubun = user.getUSER_GUBUN();
            String user_id = user.getUsername();

            coaMgmtDTO coaParamDto = new coaMgmtDTO();
            coaParamDto.setTOKEN_USER_ID(user_id);
            coaParamDto.setCOA_ID(coa_id);
            coaParamDto.setVENDOR_COMMENT(vendor_comment);
            coaParamDto.setLOT_NO(lot_no);

            coaMgmtService.updateVendorComment(coaParamDto);

            response.put("status", "success");
            response.put("message", "승인 처리 되었습니다.");


        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "처리 중 오류가 발생했습니다.");
        }
        return response;
    }


    //복사
    @PostMapping("/admin/qualityCtrl/copyCoa")
    @ResponseBody
    public Map<String, String> copyCoa(@RequestBody List<coaMgmtDTO> coaRegList) {
        Map<String, String> response = new HashMap<>();
        try {

            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_gubun = user.getUSER_GUBUN();
            String user_id = user.getUsername();

            for (coaMgmtDTO data : coaRegList) {

                coaMgmtDTO coaParamDto = new coaMgmtDTO();
                coaParamDto.setCOA_ID(data.getCOA_ID());
                coaParamDto.setVENDOR_ID(data.getVENDOR_ID());
                coaParamDto.setMATERIAL_ID(data.getMATERIAL_ID());
                coaParamDto.setFACTORY_ID(data.getFACTORY_ID());
                coaParamDto.setLOT_NO(data.getLOT_NO());

                coaParamDto.setSTOCK_DATE(data.getSTOCK_DATE());
                coaParamDto.setMF_DATE(data.getMF_DATE());
                coaParamDto.setE_DATE(data.getE_DATE());
                coaParamDto.setQUANTITY(data.getQUANTITY());

                coaParamDto.setCREATOR(user_id);
                coaParamDto.setCOPY_COA_ID(coaMgmtService.getCOANumber());

                coaMgmtService.copyCOAMaster(coaParamDto);

                coaMgmtService.copyCOADetail(coaParamDto);

            }

            response.put("status", "success");
            response.put("message", "복사 처리 되었습니다.");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "처리 중 오류가 발생했습니다.");
        }
        return response;
    }



}
