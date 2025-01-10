package com.dongwoo.SQM.qualityCtrl.controller;

import com.dongwoo.SQM.common.util.PropUtils;
import com.dongwoo.SQM.companyInfo.service.PartCodeService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.qualityCtrl.dto.coaMgmtDTO;
import com.dongwoo.SQM.qualityCtrl.service.coaMgmtService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.UserMgrDTO;
import com.dongwoo.SQM.siteMgr.repository.UserMgrRepository;
import com.dongwoo.SQM.siteMgr.service.UserMgrService;
import com.dongwoo.SQM.system.dto.ComPanyCodeDTO;
import com.dongwoo.SQM.system.dto.UserInfoCompanyDTO;
import com.dongwoo.SQM.system.dto.UserInfoDTO;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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

    private final UserMgrService userMgrService;

    private final PartCodeService partCodeService;



    // E-Mail
    @Resource(name="mailSender")
    private JavaMailSender mailSender;

    private String getSelLangCookie(HttpServletRequest request ,String siteType) {
        Cookie[] cookies = request.getCookies();
        String selLangCookieValue = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("selLang".equals(cookie.getName())) {

                    if(siteType.equals("DP")) {
                        selLangCookieValue = switch (cookie.getValue()) {
                            case "KOR" -> "KR";
                            case "ENG" -> "EN";
                            case "JPN" -> "JA";
                            case "CHN" -> "CN";
                            default -> throw new RuntimeException("KR");
                        };
                    }else {
                        selLangCookieValue = cookie.getValue();
                    }
                    break;
                }
            }
        }

        return selLangCookieValue;
    }

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

        List<BaseCodeDTO> statusList = coaMgmtService.GetBaseCode("COA_STATUS");
        model.addAttribute("coaStatusList", statusList);

        List<BaseCodeDTO> plantList = coaMgmtService.GetBaseCodePLANT("PLANT");
        model.addAttribute("coaPlantList", plantList);

        //엑셀 업로드 벤더.SC_COMPANY_CODE
        List<HashMap> companyList = partCodeService.getCompanyList("", "");
        model.addAttribute("companyList", companyList);


        return "coaMgmt/coaList";
    }

    @PostMapping("/admin/qualityCtrl/getMaterialListCoa")
    @ResponseBody
    public List<HashMap> getMaterialListCoa(HttpServletRequest req) {
        String COM_CODE = req.getParameter("COM_CODE");

        //엑셀 업로드 자재코드  SC_PART_CODE
        return coaMgmtService.getMaterialListCoa(COM_CODE);
    }

    @PostMapping("/admin/qualityCtrl/getMaterialFactoryListCoa")
    @ResponseBody
    public List<HashMap> getMaterialFactoryListCoa(HttpServletRequest req ,HttpServletRequest request) {
        String vendorId = req.getParameter("vendorId");
        String materialId = req.getParameter("materialId");
        String selLang = getSelLangCookie(request,"DP");
        return coaMgmtService.getMaterialFactoryList(vendorId ,materialId  ,selLang);
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

            int ret = 0 ;
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

                ret =  coaMgmtService.copyCOAMaster(coaParamDto);
                if(ret > 0) {
                     coaMgmtService.copyCOADetail(coaParamDto);
                }
            }
            response.put("status", "success");
            response.put("message", "복사 처리 되었습니다.");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "처리 중 오류가 발생했습니다.");
        }
        return response;
    }



    //삭제
    @PostMapping("/admin/qualityCtrl/deleteCoa")
    @ResponseBody
    public Map<String, String> deleteCoa(@RequestBody List<coaMgmtDTO> coaRegList) {
        Map<String, String> response = new HashMap<>();
        try {

            int ret = 0 ;
            for (coaMgmtDTO data : coaRegList) {

                if ("A".equals(data.getCOA_STATUS().toUpperCase())
                        || "D".equals(data.getCOA_STATUS().toUpperCase())
                        || "H".equals(data.getCOA_STATUS().toUpperCase()) ) {

                    coaMgmtDTO coaParamDto = new coaMgmtDTO();
                    coaParamDto.setCOA_ID(data.getCOA_ID());
                    coaParamDto.setVENDOR_ID(data.getVENDOR_ID());
                    coaParamDto.setMATERIAL_ID(data.getMATERIAL_ID());
                    coaParamDto.setFACTORY_ID(data.getFACTORY_ID());
                    coaParamDto.setLOT_NO(data.getLOT_NO());

                    ret = coaMgmtService.delCOAMaster(coaParamDto);
                    if (ret > 0) {
                        coaMgmtService.delCOADetail(coaParamDto);
                    }

                } else {
                    //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    response.put("status", "error");
                    response.put("message", "NOT DELETE STATUS EXIST");
                    return response;
                }
            }
            response.put("status", "success");
            response.put("message", "복사 처리 되었습니다.");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "처리 중 오류가 발생했습니다.");
        }



        return response;
    }

    //registCheck  체크
    @PostMapping("/admin/qualityCtrl/registCheck")
    @ResponseBody
    public Map<String, Object> registCheck(@RequestBody List<coaMgmtDTO> coaRegList ,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("Received coaRegList: " + coaRegList);


            Cookie[] cookies = request.getCookies();
            String selLangCookieValue = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("selLang".equals(cookie.getName())) {
                        selLangCookieValue = cookie.getValue();
                        break;
                    }
                }
            }
//다국어 메세지 화면 리턴.
            if("KOR".equals(selLangCookieValue)) {
                response.put("NOTICE_CONT", PropUtils.get("notice.template.coa.cont.kr"));
                response.put("SPEC_CHECK_CONT1", PropUtils.get("spec.check.coa.cont1.kr"));
                response.put("SPEC_CHECK_CONT2", PropUtils.get("spec.check.coa.cont2.kr"));
                response.put("SPEC_CHECK_SPEC", PropUtils.get("spec.check.coa.spec.kr"));
                response.put("SPEC_CHECK_CTRL", PropUtils.get("spec.check.coa.ctrl.kr"));
            }else if("ENG".equals(selLangCookieValue)) {
               response.put("NOTICE_CONT", PropUtils.get("notice.template.coa.cont.en"));
               response.put("SPEC_CHECK_CONT1", PropUtils.get("spec.check.coa.cont1.en"));
               response.put("SPEC_CHECK_CONT2", PropUtils.get("spec.check.coa.cont2.en"));
               response.put("SPEC_CHECK_SPEC", PropUtils.get("spec.check.coa.spec.en"));
               response.put("SPEC_CHECK_CTRL", PropUtils.get("spec.check.coa.ctrl.en"));
            }else if("JPN".equals(selLangCookieValue)) {
                response.put("NOTICE_CONT", PropUtils.get("notice.template.coa.cont.ja"));
                response.put("SPEC_CHECK_CONT1", PropUtils.get("spec.check.coa.cont1.ja"));
                response.put("SPEC_CHECK_CONT2", PropUtils.get("spec.check.coa.cont2.ja"));
                response.put("SPEC_CHECK_SPEC", PropUtils.get("spec.check.coa.spec.ja"));
                response.put("SPEC_CHECK_CTRL", PropUtils.get("spec.check.coa.ctrl.ja"));
            }else if("CHN".equals(selLangCookieValue)) {
                response.put("NOTICE_CONT", PropUtils.get("notice.template.coa.cont.cn"));
                response.put("SPEC_CHECK_CONT1", PropUtils.get("spec.check.coa.cont1.cn"));
                response.put("SPEC_CHECK_CONT2", PropUtils.get("spec.check.coa.cont2.cn"));
                response.put("SPEC_CHECK_SPEC", PropUtils.get("spec.check.coa.spec.cn"));
                response.put("SPEC_CHECK_CTRL", PropUtils.get("spec.check.coa.ctrl.cn"));
            }

            for (int i = 0 ; i < coaRegList.size() ; i ++) {

                coaMgmtDTO paramDto = coaRegList.get(i);
                coaMgmtDTO resultCheck = coaMgmtService.regCheck(paramDto);
                coaMgmtDTO resultSpecCheck = coaMgmtService.regSpecCheck(paramDto);

                if("Y".equals(resultCheck.getISPR()) && "1200".equals(resultCheck.getFACTORY_ID())){
                    response.put("MAIL_INFO", "ewnajang@dwchem.co.kr, khwanmin@dwchem.co.kr)");
                }else if("N".equals(resultCheck.getISPR()) && "1200".equals(resultCheck.getFACTORY_ID())){
                    response.put("MAIL_INFO", "ewnajang@dwchem.co.kr)");
                }else if("Y".equals(resultCheck.getISPR()) && "1300".equals(resultCheck.getFACTORY_ID())){
                    response.put("MAIL_INFO", "hana@dwchem.co.kr)");
                }else if("N".equals(resultCheck.getISPR()) && "1200".equals(resultCheck.getFACTORY_ID())){
                    response.put("MAIL_INFO", "ewnajang@dwchem.co.kr)");
                }else{
                    response.put("MAIL_INFO", "ewnajang@dwchem.co.kr)");
                }

                if("N".equals(resultCheck.getISPR())){
                    response.put("ISEXPIRED", false);
                }else if("Y".equals(resultCheck.getIS30LIMIT())){
                    response.put("ISEXPIRED", true);
                }else if("Y".equals(resultCheck.getIS50LIMIT())){
                    response.put("ISEXPIRED", true);
                }else{
                    response.put("ISEXPIRED", false);
                }


                if(Integer.parseInt(resultSpecCheck.getIS_SPEC_YN_CNT().toString())>0){
                    response.put("IS_SPEC_YN_CNT", true);
                }else if(Integer.parseInt(resultSpecCheck.getIS_CONTROL_YN_CNT().toString())>0){
                    response.put("IS_CONTROL_YN_CNT", true);
                }else {
                    //response.put("IS_SPEC_YN_CNT", false);
                    //response.put("IS_CONTROL_YN_CNT", false);

                    //test
                    response.put("IS_SPEC_YN_CNT", true );
                    response.put("IS_CONTROL_YN_CNT", true);
                }

            }

           response.put("status", "success");
           response.put("message", "유효성 처리 완료되었습니다.");

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "처리 중 오류가 발생했습니다.");
        }

        return  response ;
    }


    @PostMapping("/admin/qualityCtrl/regist")
    @ResponseBody
    public Map<String, Object> regist(@RequestBody List<coaMgmtDTO> coaRegList ,HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        String ifTarget = "";

        ArrayList<coaMgmtDTO> limsIFList = new ArrayList<coaMgmtDTO>();
        ArrayList<coaMgmtDTO> cpsIFMasterList = new ArrayList<coaMgmtDTO>();
        ArrayList<coaMgmtDTO> cpsIFDetailList = new ArrayList<coaMgmtDTO>();

        ArrayList<String> factoryList = new ArrayList<String>();

        try {
            for (int i = 0 ; i < coaRegList.size() ; i ++) {

                coaMgmtDTO paramDto = coaRegList.get(i);
                coaMgmtDTO checkMap = coaMgmtService.regCheck(paramDto);
                coaMgmtDTO checkMap2 = coaMgmtService.regSpecCheck(paramDto);

                //Map<String, Object> regData = coaRegList.get(i);
                                   //: === > paramDto
                //spec out체크로직 -> 메니저,관리자 권한만 통과
                if((Integer.parseInt(checkMap2.getIS_SPEC_YN_CNT())) > 0
                         //   && (!"AU".equals(parameterMap.get("USER_TYPE")) && !"MU".equals(parameterMap.get("USER_TYPE")))
                ){
                    //response.put("SUCCESS", "N");
                    response.put("status", "err");
                    response.put("MSG", "SPEC OUT");
                    return response;
                }

                String sStatus  = coaMgmtService.getStatusCOAMasterByPK(paramDto);

                if(!"A".equals(sStatus)){
                    continue;
                }
                coaMgmtDTO material = coaMgmtService.getMaterial(paramDto);

                factoryList.add(material.getFACTORY_ID());
                ifTarget = material.getIF_TARGET();
//2025-01-08 sylee
                ifTarget = "CPS" ;
//
                if ("LIMS".equals(ifTarget.toUpperCase())) {

                    coaMgmtDTO ifMasterData = coaMgmtService.interfaceLimsCOAMasterData(paramDto);

                    //check상태 변경
                    if("Y".equals(checkMap.getIS30LIMIT())){
                        ifMasterData.setCOA_STATUS("F");
                    }
                    if(Integer.parseInt(checkMap2.getIS_SPEC_YN_CNT())>0){
                        ifMasterData.setCOA_STATUS("I");
                    }
                    ifMasterData.setDB_LINK_TARGET_MASTER(PropUtils.get("if.lims.coa.master"));
                    coaMgmtService.interfaceCOAMaster(ifMasterData); //입력! 테이블 또는 뷰가 존재하지 않습니다  LIMS_INTF_SQM_INFO_TEMP

                    List<coaMgmtDTO> ifDetailList = coaMgmtService.interfaceLimsCOADetailData(paramDto);

                    for( int j=0; j<ifDetailList.size() ; j++ ){
                        coaMgmtDTO detailMap = ifDetailList.get(j);
                        detailMap.setDB_LINK_TARGET_DETAIL(PropUtils.get("if.lims.coa.detail"));
                        coaMgmtService.interfaceCOADetail(detailMap); //입력! 테이블 또는 뷰가 존재하지 않습니다 LIMS_INTF_SQM_COA_TEMP
                    }

                    limsIFList.add(ifMasterData);
                } else if ("CPS".equals(ifTarget.toUpperCase())) {
                    coaMgmtDTO ifMasterData = coaMgmtService.interfaceDqmsCOAMasterData(paramDto);
                    cpsIFMasterList.add(ifMasterData);

                    List<coaMgmtDTO> ifDetailList = coaMgmtService.interfaceDqmsCOADetailData(paramDto);
                    cpsIFDetailList.addAll(ifDetailList);
                } else if("SEMA".equals(ifTarget.toUpperCase())){
                    paramDto.setDB_LINK_TARGET_MASTER(PropUtils.get("if.lims.xian.coa.master"));
                    paramDto.setDB_LINK_TARGET_DETAIL(PropUtils.get("if.lims.xian.coa.detail"));
                    paramDto.setDB_LINK_TARGET_MASTER_SEQ(PropUtils.get("if.lims.xian.coa.master.seq"));
                    paramDto.setDB_LINK_TARGET_DETAIL_SEQ(PropUtils.get("if.lims.xian.coa.detail.seq"));
                    limsIFList.add(paramDto);
                }
                else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    response.put("status", "err");
                    response.put("MSG", "M_WrongIFTarget");
                    return response;
                }
            }

            //*LIMS  , *SEMA
            // Insert LIMS Interface Data
            if (limsIFList.size() > 0) {

                String DB_LINK_TARGET_APPLY ="";
                if ("LIMS".equals(ifTarget.toUpperCase())) {
                    DB_LINK_TARGET_APPLY = PropUtils.get("if.lims.coa.apply"); //LIMS_INTF_SQM_PROC
                } else if("SEMA".equals(ifTarget.toUpperCase())){
                    DB_LINK_TARGET_APPLY = PropUtils.get("if.lims.xian.coa.apply");  //LIMS_INTF_SQM_PROC@LIMS_DEV_XIAN_DB()
                }

                // CALL I/F PROCEDURE
                if (!"LIMS".equals(ifTarget.toUpperCase())) {
                    coaMgmtService.interfaceCOAProcedure(DB_LINK_TARGET_APPLY);  //CALL ${DB_LINK_TARGET_APPLY}()
                }
            }

            //*CPS
            // Insert CPS Interface Data
            if (cpsIFMasterList.size() > 0) {

                int seq = coaMgmtService.insertCOAListDqms(cpsIFMasterList, cpsIFDetailList);
                log.info("insertCOAListDqms_seq======"+seq);
            }

            // UPDATE STATUS
            for (int i = 0 ; i < coaRegList.size() ; i ++) {

                coaMgmtDTO paramDto = coaRegList.get(i);
                coaMgmtDTO checkMap = coaMgmtService.regCheck(paramDto);
                coaMgmtDTO checkMap2 = coaMgmtService.regSpecCheck(paramDto);

                //현업 권한만 spec out 등록가능
                if(Integer.parseInt(checkMap2.getIS_SPEC_YN_CNT())> 0
//                        && !"MU".equals(parameterMap.get("USER_TYPE"))
                ){
                    response.put("status", "err");
                    response.put("MSG", "SPECOUT REG CHECKING");
                    return response;
                }

                UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                //동우 유저
                if(user.getEMAIL() == null) {
                    String userId = user.getUSER_ID();
                    UserMgrDTO userDto = userMgrService.findUserMgrById(userId);
                    user.setEMAIL(userDto.getEMAIL());
                }

                String userEmail =  user.getEMAIL();

                if("Y".equals(checkMap.getIS30LIMIT())
                            || "Y".equals(checkMap.getIS50LIMIT())){
                    String[] targetUserArr = null;
                    String[] targetUserArr2 = null;

                    ArrayList<String> paramFactoryList = new ArrayList<String>();

                    //regData->paramDto  sylee 2025.01.08
                    if("1200".equals(paramDto.getFACTORY_ID())){
                        paramFactoryList.add("MANAGE_COA_YN_01");
                    }else if ("1300".equals(paramDto.getFACTORY_ID())){
                        paramFactoryList.add("MANAGE_COA_YN_02");
                    }else if ("3100".equals(paramDto.getFACTORY_ID())){
                        paramFactoryList.add("MANAGE_COA_YN_04");
                    }else if ("4100".equals(paramDto.getFACTORY_ID())){
                        paramFactoryList.add("MANAGE_COA_YN_04");
                    }else if ("5100".equals(paramDto.getFACTORY_ID())){
                        paramFactoryList.add("MANAGE_COA_YN_04");
                    }else {
                        paramFactoryList.add("MANAGE_COA_YN_03");
                    }
                      List<HashMap> targetList = null;
                      Map<String,Object> parameterMap = new HashMap<>();
                      parameterMap.put("factoryList", paramFactoryList);

                    //받는사람
//                    MANAGE_PART_YN;            //-- PART 관리자 □  이건 전체 다 보낸다.
//                    MANAGE_COA_YN_01;          //-- COA 반도체 □ 1200
//                    MANAGE_COA_YN_02;          //-- COA 첨단소재 □ 1300
//                    MANAGE_COA_YN_03;          //-- COA 첨단-삼기 □ 8100
//                    MANAGE_COA_YN_04;          //-- COA 통신디바이스 □  ( 4100  , 3100  ,5100  )

                    targetList = coaMgmtService.getEmailTargetUser(parameterMap);

                    targetUserArr = new String[targetList.size()];
                    targetUserArr2 = new String[1];
                    for(int j=0; j<targetList.size(); j++){
                        HashMap<String, Object> targetUser = targetList.get(j);
                        targetUserArr[j] = targetUser.get("EMAIL").toString();
                    }

                    targetUserArr2[0] = userEmail ;  //발송자

                    MimeMessage msg = mailSender.createMimeMessage();
                    MimeMessageHelper msgHelper = new MimeMessageHelper(msg, true, "UTF-8");

                    MimeMessage msg2 = mailSender.createMimeMessage();
                    MimeMessageHelper msgHelper2 = new MimeMessageHelper(msg2, true, "UTF-8");

                    msgHelper.setTo(targetUserArr);
                    msgHelper.setFrom(PropUtils.get("mail.sender.account"));

                    msgHelper2.setTo(targetUserArr2);
                    msgHelper2.setFrom(PropUtils.get("mail.sender.account"));

                    String contents="";
                    String subject="[DP-PORTAL] "+paramDto.getMATERIAL_NAME()+"/"+paramDto.getLOT_NO()+"/";

                    Cookie[] cookies = request.getCookies();
                    String selLangCookieValue = null;
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if ("selLang".equals(cookie.getName())) {
                                selLangCookieValue = cookie.getValue();
                                break;
                            }
                        }
                    }

                    //다국어 메세지.
                    if("KOR".equals(selLangCookieValue)){
                        contents = "<table width='510' cellpadding='0' cellspacing='0' border='1' style='border-collapse:collapse; border-color:#CECFCE; border-style:solid; font-size:12px;' >"
                                + "<tr>"
                                + "<td align='center' width='100' style='background-color:#EFEFEF;' >원료명</td>"
                                + "<td align='center' width='100' style='background-color:#EFEFEF;' >LOT</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >출하일</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >만료일</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >입고량</td>"
                                + "<td align='center' width='70' style='background-color:#EFEFEF;' >SPEC<br>IN/OUT</td>"
                                + "</tr>";
                        contents += "<tr>"
                                + "<td align='center' >" + paramDto.getMATERIAL_NAME() + "</td>"
                                + "<td align='center' >" + paramDto.getLOT_NO() + "</td>"
                                + "<td align='center' >" + paramDto.getSTOCK_DATE() + "</td>"
                                + "<td align='center' >" + paramDto.getE_DATE() + "</td>"
                                + "<td align='center' >" + paramDto.getQUANTITY() + "</td>"
                                + "<td align='center' >" + paramDto.getSPEC_IN() + "</td>"
                                + "</tr></table>";
                        contents+=PropUtils.get("notice.template.coa.cont.kr");
                        subject+=PropUtils.get("notice.template.coa.sub1.kr");
                        if("Y".equals(checkMap.getIS30LIMIT())){
                            subject+=PropUtils.get("notice.template.coa.sub3.kr");
                        }else{
                            subject+=PropUtils.get("notice.template.coa.sub2.kr");
                        }
                    }else if("ENG".equals(selLangCookieValue)) {
                        contents = "<table width='510' cellpadding='0' cellspacing='0' border='1' style='border-collapse:collapse; border-color:#CECFCE; border-style:solid; font-size:12px;' >"
                                + "<tr>"
                                + "<td align='center' width='100' style='background-color:#EFEFEF;' >name</td>"
                                + "<td align='center' width='100' style='background-color:#EFEFEF;' >LOT</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >ShipmentDate</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >ExpirationDate</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >Quantity</td>"
                                + "<td align='center' width='70' style='background-color:#EFEFEF;' >SPEC<br>IN/OUT</td>"
                                + "</tr>";
                        contents += "<tr>"
                                + "<td align='center' >" + paramDto.getMATERIAL_NAME() + "</td>"
                                + "<td align='center' >" + paramDto.getLOT_NO() + "</td>"
                                + "<td align='center' >" + paramDto.getSTOCK_DATE() + "</td>"
                                + "<td align='center' >" + paramDto.getE_DATE() + "</td>"
                                + "<td align='center' >" + paramDto.getQUANTITY() + "</td>"
                                + "<td align='center' >" + paramDto.getSPEC_IN() + "</td>"
                                + "</tr></table>";
                        contents+=PropUtils.get("notice.template.coa.cont.en");
                        subject+=PropUtils.get("notice.template.coa.sub1.en");
                        if("Y".equals(checkMap.getIS30LIMIT())){
                            subject+=PropUtils.get("notice.template.coa.sub3.en");
                        }else{
                            subject+=PropUtils.get("notice.template.coa.sub2.en");
                        }
                    }else if("JPN".equals(selLangCookieValue)) {
                        contents = "<table width='510' cellpadding='0' cellspacing='0' border='1' style='border-collapse:collapse; border-color:#CECFCE; border-style:solid; font-size:12px;' >"
                                + "<tr>"
                                + "<td align='center' width='100' style='background-color:#EFEFEF;' >原料名</td>"
                                + "<td align='center' width='100' style='background-color:#EFEFEF;' >LOT</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >出荷日</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >有効期限</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >数量</td>"
                                + "<td align='center' width='70' style='background-color:#EFEFEF;' >SPEC<br>IN/OUT</td>"
                                + "</tr>";
                        contents += "<tr>"
                                + "<td align='center' >" + paramDto.getMATERIAL_NAME() + "</td>"
                                + "<td align='center' >" + paramDto.getLOT_NO() + "</td>"
                                + "<td align='center' >" + paramDto.getSTOCK_DATE() + "</td>"
                                + "<td align='center' >" + paramDto.getE_DATE() + "</td>"
                                + "<td align='center' >" + paramDto.getQUANTITY() + "</td>"
                                + "<td align='center' >" + paramDto.getSPEC_IN() + "</td>"
                                + "</tr></table>";
                        contents+=PropUtils.get("notice.template.coa.cont.ja");
                        subject+=PropUtils.get("notice.template.coa.sub1.ja");
                        if("Y".equals(checkMap.getIS30LIMIT())){
                            subject+=PropUtils.get("notice.template.coa.sub3.ja");
                        }else{
                            subject+=PropUtils.get("notice.template.coa.sub2.ja");
                        }
                    }else if("CHN".equals(selLangCookieValue)) {
                        contents = "<table width='510' cellpadding='0' cellspacing='0' border='1' style='border-collapse:collapse; border-color:#CECFCE; border-style:solid; font-size:12px;' >"
                                + "<tr>"
                                + "<td align='center' width='100' style='background-color:#EFEFEF;' >原料名称</td>"
                                + "<td align='center' width='100' style='background-color:#EFEFEF;' >LOT</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >发货日期</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >截止日期</td>"
                                + "<td align='center' width='80' style='background-color:#EFEFEF;' >入库</td>"
                                + "<td align='center' width='70' style='background-color:#EFEFEF;' >SPEC<br>IN/OUT</td>"
                                + "</tr>";
                        contents += "<tr>"
                                + "<td align='center' >" + paramDto.getMATERIAL_NAME() + "</td>"
                                + "<td align='center' >" + paramDto.getLOT_NO() + "</td>"
                                + "<td align='center' >" + paramDto.getSTOCK_DATE() + "</td>"
                                + "<td align='center' >" + paramDto.getE_DATE() + "</td>"
                                + "<td align='center' >" + paramDto.getQUANTITY() + "</td>"
                                + "<td align='center' >" + paramDto.getSPEC_IN() + "</td>"
                                + "</tr></table>";
                        contents+=PropUtils.get("notice.template.coa.cont.cn");
                        subject+=PropUtils.get("notice.template.coa.sub1.cn");
                        if("Y".equals(checkMap.getIS30LIMIT())){
                            subject+=PropUtils.get("notice.template.coa.sub3.cn");
                        }else{
                            subject+=PropUtils.get("notice.template.coa.sub2.cn");
                        }
                    }

                    msgHelper.setSubject(subject);
                    msgHelper2.setSubject(subject);
                    String mailContents = "";
                    mailContents +=contents;
                    msgHelper.setText("", mailContents);
                    msgHelper2.setText("", mailContents);
                    try {
                        if("Y".equals(checkMap.getISPR())){
                            mailSender.send(msg);
                            mailSender.send(msg2);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        log.info("smtp error===============");
                    }
                }
                if("Y".equals(checkMap.getIS30LIMIT()) && "Y".equals(checkMap.getISPR())){   //regData.L_FactoryId
                    paramDto.setCOA_STATUS("F");
                }else if(Integer.parseInt(checkMap2.getIS_SPEC_YN_CNT())>0){
                    paramDto.setCOA_STATUS("I");
                }else{
                    paramDto.setCOA_STATUS("B");
                }

                coaMgmtService.updateCOAStatus(paramDto);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.put("status", "err");
            response.put("MSG", "L_InterfaceError");
            return response;
        }

        //담당자 메일 발송
        try {


            ArrayList<String> paramFactoryList = new ArrayList<String>();

            for(int j=0; j<factoryList.size();j++) {

                if ("1200".equals(factoryList.get(j))) {
                    paramFactoryList.add("MANAGE_COA_YN_01");
                } else if ("1300".equals(factoryList.get(j))) {
                    paramFactoryList.add("MANAGE_COA_YN_02");
                } else if ("3100".equals(factoryList.get(j))) {
                    paramFactoryList.add("MANAGE_COA_YN_04");
                } else if ("4100".equals(factoryList.get(j))) {
                    paramFactoryList.add("MANAGE_COA_YN_04");
                } else if ("5100".equals(factoryList.get(j))) {
                    paramFactoryList.add("MANAGE_COA_YN_04");
                } else {
                    paramFactoryList.add("MANAGE_COA_YN_03");
                }
            }

            Map<String,Object> parameterMap = new HashMap<>();
            parameterMap.put("factoryList", paramFactoryList);

            List<HashMap> targetList = coaMgmtService.getEmailTargetUser(parameterMap);

            if (null != targetList && targetList.size() > 0) {
                String[] targetUserArr = new String[targetList.size()];
                for (int i = 0 ; i < targetList.size() ; i++ ) {
                    HashMap<String, Object> targetUser = targetList.get(i);
                    targetUserArr[i] = targetUser.get("EMAIL").toString();
                }

                if (limsIFList.size() > 0 || cpsIFMasterList.size() > 0) {

                    MimeMessage msg = mailSender.createMimeMessage();
                    MimeMessageHelper msgHelper = new MimeMessageHelper(msg, true, "UTF-8");
                    msgHelper.setTo(targetUserArr);
                    msgHelper.setFrom(PropUtils.get("mail.sender.account"));
                    msgHelper.setSubject("COA가 등록되었습니다.(COA has been registered.)");

                    String mailContents = "■ COA가 등록되었습니다.";
                    String mailContents2 = "";
                    String displayDate = "입고일";

                    for (int i = 0 ; i < coaRegList.size() ; i ++) {

                        coaMgmtDTO paramDto = coaRegList.get(i);

                        mailContents2 += "<tr>"
                                + "<td align='center' >" + paramDto.getMATERIAL_NAME() + "</td>"
                                + "<td align='center' >" + paramDto.getLOT_NO() + "</td>"
                                + "<td align='center' >" + paramDto.getSTOCK_DATE() + "</td>"
                                + "<td align='center' >" + paramDto.getSPEC_IN() + "</td>"
                                + "</tr>";

                        if("PR".equals(paramDto.getWETPR())){
                            displayDate="출하일";
                        }
                    }
                    mailContents += "<table width='350' cellpadding='0' cellspacing='0' border='1' style='border-collapse:collapse; border-color:#CECFCE; border-style:solid; font-size:12px;' >"
                            + "<tr>"
                            + "<td align='center' width='100' style='background-color:#EFEFEF;' >원료명</td>"
                            + "<td align='center' width='100' style='background-color:#EFEFEF;' >LOT</td>"
                            + "<td align='center' width='80' style='background-color:#EFEFEF;' >"+displayDate+"</td>"
                            + "<td align='center' width='70' style='background-color:#EFEFEF;' >SPEC<br>IN/OUT</td>"
                            + "</tr>";
                    msgHelper.setText("", mailContents+mailContents2);
                    try {
                        mailSender.send(msg);
                    } catch (Exception e) {
                        log.info("smtp error 1===============");
                    }
                }
            }
        } catch (Exception ex) {
            log.info("smtp error 2===============");
        }

        response.put("status", "success");
        return response;
    }


//end
}
