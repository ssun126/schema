package com.dongwoo.SQM.qualityCtrl.controller;

import com.dongwoo.SQM.common.service.CommonService;
import com.dongwoo.SQM.common.util.DataUtils;
import com.dongwoo.SQM.common.util.PropUtils;
import com.dongwoo.SQM.companyInfo.service.PartCodeService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.qualityCtrl.dto.coaMgmtDTO;
import com.dongwoo.SQM.qualityCtrl.service.coaMgmtService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
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
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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

    private final CommonService commonService ;


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

    private String changeCoaSpecData(String data, String decimalPlace){
        String result=null;
        String str = data;
        int decimalPlaceInt = Integer.parseInt(decimalPlace);
        try {
            //정수형 데이터 확인
            str = Long.parseLong(str)+"";
        } catch (NumberFormatException e) {
            try {
                //실수형 데이터 확인
                str = Double.parseDouble(str)+"";
            } catch (NumberFormatException e2) {
                return data;
            }

        }
        //소수점으로 분할 및 소숫점 데이터처리
        String[] ary = str.split("\\.");
        if(ary.length==1){
            result = ary[0];
        }else if(ary.length==2){
            //result = ary[0]+"."+temp;
            result = ary[0]+"."+ary[1];
        }else{
            result = str;
        }
        //반올림 처리
        int rounds = 1;
        for(int i =0; i<decimalPlaceInt; i++){
            rounds *=10;
        }
        double temp = Math.round(Double.parseDouble(result)*rounds);
        result = temp/rounds+"";
        return result;
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

    //자재코드 팝업
    @PostMapping("/admin/qualityCtrl/materialList")
    public String materialList(Model model) {
        return "coaMgmt/apiMaterialList";
    }


    @PostMapping("/admin/qualityCtrl/getCoaMaterialList")
    @ResponseBody
    public List<HashMap> getCoaMaterialList(HttpServletRequest req) {
        String code = req.getParameter("searchMaterialId");
        String name = req.getParameter("searchMaterialName");
        String vendorId = req.getParameter("searchVandorId");
        return coaMgmtService.getCoaMaterialList(code,name ,vendorId);
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

    //엑셀 업로드 팝업 플렌트 코드 조회
    @PostMapping("/admin/qualityCtrl/getMaterialFactoryListCoa")
    @ResponseBody
    public List<HashMap> getMaterialFactoryListCoa(HttpServletRequest req ,HttpServletRequest request) {
        String vendorId = req.getParameter("vendorId");
        String materialId = req.getParameter("materialId");
        String selLang = getSelLangCookie(request,"DP");
        return coaMgmtService.getMaterialFactoryListCoa(vendorId ,materialId  ,selLang);
    }

    //COA LIST 검색
    @PostMapping("/admin/qualityCtrl/getCOAList")
    public ResponseEntity<?> getcoaList(@RequestBody coaMgmtDTO coaMgmtdto ,HttpServletRequest request) {
        try {
            System.out.println("Received scoreMgmtDTO: " + coaMgmtdto);

            //로그인 사용자 정보
            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_gubun = user.getUSER_GUBUN();
            String user_type = "";
            if(Objects.equals(user_gubun, "100")) {
                user_type="AU";     //user_gubun :  관리자 , 사용자 권한 구분 확인
            }
            String user_id = user.getUsername();
            String sUSER_LANG = getSelLangCookie(request,"DP");

            coaMgmtdto.setTOKEN_USER_TYPE(user_type);
            coaMgmtdto.setTOKEN_USER_LANG(sUSER_LANG);
            coaMgmtdto.setTOKEN_USER_ID(user_id);

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



    //파일 업로드
    @RequestMapping("/admin/qualityCtrl/upload")
    public ResponseEntity<?> excelUpload(@RequestParam(value="file") MultipartFile file
            ,HttpServletRequest request
            ,Model model) throws IOException, SQLException {


        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        Sheet sheet = null;
        Row row = null;



        //xlsx 인지 xls 인지 구분
        if(fileName.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
            sheet = workbook.getSheetAt(0);

        }else if(fileName.endsWith(".xls")){
            workbook = new HSSFWorkbook(file.getInputStream());
            sheet = workbook.getSheetAt(0);

        }else{
            return ResponseEntity.ok("|||[ERROR]||| Invalid file format. Only .xls and .xlsx are supported.");
        }

        Row headerRow = sheet.getRow(0);
        int rowCount = sheet.getPhysicalNumberOfRows();
        int cellCount = headerRow.getPhysicalNumberOfCells();
        Date date = null;

        ArrayList<LinkedHashMap<String, Object>> rowList = new ArrayList<LinkedHashMap<String, Object>>();
        LinkedHashMap<String, Object> rowMap = null;

        try {
            row = sheet.getRow(3);
            rowMap = new LinkedHashMap<String, Object>();
            for (int j = 0 ; j < cellCount ; j++ ) {
                if(row.getCell(j)==null){
                    rowMap.put(headerRow.getCell(j).getStringCellValue(), "");
                }else{
                    switch (row.getCell(j).getCellType()) {
                        case STRING:
                            rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getStringCellValue());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(row.getCell(j))) {
                                date = row.getCell(j).getDateCellValue();
                                rowMap.put(headerRow.getCell(j).getStringCellValue(), new SimpleDateFormat("yyyy-MM-dd").format(date));
                            } else {

                                if ( j < 3 || headerRow.getCell(j).getStringCellValue().equals("탱크로리번호") )
                                {
                                    //row.getCell(j).setCellType(CellType.STRING);
                                    //rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getStringCellValue());

                                    rowMap.put(headerRow.getCell(j).getStringCellValue(), String.valueOf(row.getCell(j).getNumericCellValue()));
                                }
                                else
                                {
                                    rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getNumericCellValue());
                                }
                            }
                            break;
                        case BOOLEAN:
                            rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getBooleanCellValue());
                            break;
                        case FORMULA:
                            rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getCellFormula());
                            break;
                        case ERROR:
                            rowMap.put(headerRow.getCell(j).getStringCellValue(), "");
                            break;
                        case BLANK:
                            rowMap.put(headerRow.getCell(j).getStringCellValue(), "");
                            break;
                        default :
                            rowMap.put(headerRow.getCell(j).getStringCellValue(), "");
                            break;
                    }
                }
            }
            if ( ! StringUtils.isEmpty(String.valueOf(rowMap.get(headerRow.getCell(0).getStringCellValue())))
                    && ! StringUtils.isEmpty(String.valueOf(rowMap.get(headerRow.getCell(1).getStringCellValue())))
                    && ! StringUtils.isEmpty(String.valueOf(rowMap.get(headerRow.getCell(2).getStringCellValue()))) ) {

                return ResponseEntity.ok("|||[ERROR]||| 스팩이 변경되었으니 새로 다운받아 사용하시기 바랍니다.");
            }

            for (int i = 4 ; i < rowCount ; i++ ) {
                row = sheet.getRow(i);
                if(row==null){
                    continue;
                }
                rowMap = new LinkedHashMap<String, Object>();
                for (int j = 0 ; j < cellCount ; j++ ) {

                    if(row.getCell(j)==null){
                        rowMap.put(headerRow.getCell(j).getStringCellValue(), "");
                    }else{
                        switch (row.getCell(j).getCellType()) {
                            case STRING:
                                rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getStringCellValue());
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(row.getCell(j))) {
                                    date = row.getCell(j).getDateCellValue();
                                    rowMap.put(headerRow.getCell(j).getStringCellValue(), new SimpleDateFormat("yyyy-MM-dd").format(date));
                                } else {

                                    if ( j < 5 || headerRow.getCell(j).getStringCellValue().equals("탱크로리번호") )
                                    {
                                        //row.getCell(j).setCellType(CellType.STRING);
                                        //rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getStringCellValue());
                                        rowMap.put(headerRow.getCell(j).getStringCellValue(), String.valueOf(row.getCell(j).getNumericCellValue()));
                                    }
                                    else
                                    {
                                        //double temp2 = row.getCell(j).getNumericCellValue();
                                        //row.getCell(j).setCellType(CellType.STRING);
                                        //String temp = row.getCell(j).getStringCellValue();
                                        //rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getStringCellValue()); //소숫점 오류
                                        rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getNumericCellValue());
                                    }
                                }
                                break;
                            case BOOLEAN:
                                rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getBooleanCellValue());
                                break;
                            case FORMULA:
                                rowMap.put(headerRow.getCell(j).getStringCellValue(), row.getCell(j).getCellFormula());
                                break;
                            case ERROR:
                                rowMap.put(headerRow.getCell(j).getStringCellValue(), "");
                                break;
                            case BLANK:
                                rowMap.put(headerRow.getCell(j).getStringCellValue(), "");
                                break;
                            default :
                                rowMap.put(headerRow.getCell(j).getStringCellValue(), "");
                                break;
                        }
                    }
                }
                rowList.add(rowMap);
            }
        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]||| 엑셀파일에 오류가 발생하였습니다. 엑셀파일을 다시 다운로드 후 재 작성하세요..");
        }


        try {

            Map<String, Object> reqParam = new HashMap<>();  ;
            String sVendor = GetParam(request, "ddlUpVendor", "");
            String sMaterial = GetParam(request, "ddlUpMaterial", "");
            String sPlant = GetParam(request, "ddlUpPlant", "");
            String sUSER_LANG = getSelLangCookie(request,"DP");

            reqParam.put("vendorId", sVendor);
            reqParam.put("materialId", sMaterial);
            reqParam.put("plantId", sPlant);
            reqParam.put("USER_LANG", sUSER_LANG);

            Map<String, String> resultMap = setCoaList(reqParam, rowList);

            if ("Y".equals(resultMap.get("SUCCESS"))) {
                return ResponseEntity.ok("OK");
            } else {
                System.out.println("coaRegList: err " + resultMap.get("MSG") );
                return ResponseEntity.ok("|||[ERROR]||| " +resultMap.get("MSG") );
            }

        } catch (Exception e) {
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

    }



    public Map<String, String> setCoaList(Map<String,Object> parameterMap
            ,ArrayList<LinkedHashMap<String, Object>> rowList) {


        Map<String, String> rtnMap = new HashMap<String, String>();

        String coaNumber = coaMgmtService.getCOANumber();
        parameterMap.put("coaId", coaNumber);

        coaMgmtService.insertCOADFile(parameterMap);  //첨부파일

        List<LinkedHashMap<String,Object>> specList = coaMgmtService.getSpecList(parameterMap);

        Map<String, Object> masterMap = null;
        Map<String, Object> detailMap = null;

        String stockDate = "";
        String mfDate = "";
        String tlNum = "";
        String eDate = "";
        String quantity = "";
        String lotNo = "";
        boolean isEmptyMessage = false;
        boolean isDataError = false;
        ArrayList<String> emptyMsg =new ArrayList<String>();
        ArrayList<String> dataErrorMsg =new ArrayList<String>();
        JSONArray jemptyMsg = new JSONArray();
        JSONArray jdataErrorMsg = new JSONArray();

        for (int i = 0 ; i < rowList.size() ; i++) {

            stockDate = "";
            mfDate = "";
            tlNum = "";

            LinkedHashMap<String, Object> rowData = rowList.get(i);
            masterMap = new HashMap<String, Object>();
            masterMap.put("COA_ID", coaNumber);
            masterMap.put("VENDOR_ID", parameterMap.get("vendorId"));
            masterMap.put("MATERIAL_ID", parameterMap.get("materialId"));
            masterMap.put("FACTORY_ID", parameterMap.get("plantId"));
            try {
                if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.lotno.kr"))) {
                    lotNo = rowData.get(PropUtils.get("excel.template.coa.lotno.kr")).toString();
                } else if(!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.lotno.en"))) {
                    lotNo = rowData.get(PropUtils.get("excel.template.coa.lotno.en")).toString();
                }

                if (!StringUtils.isEmpty(lotNo)) {
                    masterMap.put("LOT_NO", lotNo);
                } else {
                    emptyMsg.add("L_LotNo");
                    JSONObject jobject = new JSONObject();
                    jobject.put("code", "L_LotNo");
                    jemptyMsg.add(jobject);
                    isEmptyMessage =true;
                }
            } catch (Exception e) {
                isDataError = true;
                dataErrorMsg.add("L_LotNo");
                JSONObject jobject = new JSONObject();
                jobject.put("code", "L_LotNo");
                jdataErrorMsg.add(jobject);
            }

            try {
                if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.mdate.kr"))) {
                    mfDate = rowData.get(PropUtils.get("excel.template.coa.mdate.kr")).toString();
                } else if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.mdate.en"))){
                    mfDate = rowData.get(PropUtils.get("excel.template.coa.mdate.en")).toString();
                }

                if (!StringUtils.isEmpty(mfDate)) {
                    mfDate = coaMgmtService.parseDate(mfDate);
                } else {
                    emptyMsg.add("L_ManufactureDate");
                    JSONObject jobject = new JSONObject();
                    jobject.put("code", "L_ManufactureDate");
                    jemptyMsg.add(jobject);
                    isEmptyMessage =true;
                }
            } catch (Exception ex) {
                isDataError = true;
                dataErrorMsg.add("L_ManufactureDate");
                JSONObject jobject = new JSONObject();
                jobject.put("code", "L_ManufactureDate");
                jdataErrorMsg.add(jobject);
            }

            masterMap.put("L_VendorId", masterMap.get("VENDOR_ID"));
            masterMap.put("L_MaterialId", masterMap.get("MATERIAL_ID"));
            masterMap.put("L_FactoryId", masterMap.get("FACTORY_ID"));

            LinkedHashMap<String,Object> material = coaMgmtService.getMaterial(masterMap);

            if(material== null){
                rtnMap.put("SUCCESS", "N");
                rtnMap.put("MSG", "material data err.");
                //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return rtnMap;
            }


            try {
                if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.ddate.kr"))) {
                    stockDate = rowData.get(PropUtils.get("excel.template.coa.ddate.kr")).toString();
                } else if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.ddate.en"))){
                    stockDate = rowData.get(PropUtils.get("excel.template.coa.ddate.en")).toString();
                }

                if (!StringUtils.isEmpty(stockDate)) {
                    stockDate = coaMgmtService.parseDate(stockDate);
                } else {
                    emptyMsg.add("L_DeliveryDate");
                    JSONObject jobject = new JSONObject();
                    jobject.put("code", "L_DeliveryDate");
                    jemptyMsg.add(jobject);
                    isEmptyMessage =true;
                }
            } catch (Exception ex) {
                isDataError = true;
                dataErrorMsg.add("L_DeliveryDate");
                JSONObject jobject = new JSONObject();
                jobject.put("code", "L_DeliveryDate");
                jdataErrorMsg.add(jobject);
            }

            try {
                if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.edate.kr"))) {
                    eDate = rowData.get(PropUtils.get("excel.template.coa.edate.kr")).toString();
                } else if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.edate.en"))){
                    eDate = rowData.get(PropUtils.get("excel.template.coa.edate.en")).toString();
                }

                if (!StringUtils.isEmpty(eDate)) {
                    eDate = coaMgmtService.parseDate(eDate);
                } else {
                    emptyMsg.add("L_ExpirationDate");
                    JSONObject jobject = new JSONObject();
                    jobject.put("code", "L_ExpirationDate");
                    jemptyMsg.add(jobject);
                    isEmptyMessage =true;
                }
            } catch (Exception ex) {
                isDataError = true;
                dataErrorMsg.add("L_ExpirationDate");
                JSONObject jobject = new JSONObject();
                jobject.put("code", "L_ExpirationDate");
                jdataErrorMsg.add(jobject);
            }

            try {
                if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.quantity.kr"))) {
                    quantity = rowData.get(PropUtils.get("excel.template.coa.quantity.kr")).toString();
                } else if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.quantity.en"))) {
                    quantity = rowData.get(PropUtils.get("excel.template.coa.quantity.en")).toString();
                }
                if (!StringUtils.isEmpty(quantity)) {
                } else {
                    emptyMsg.add("L_Volume");
                    JSONObject jobject = new JSONObject();
                    jobject.put("code", "L_Volume");
                    jemptyMsg.add(jobject);
                    isEmptyMessage =true;
                }
            } catch (Exception ex) {
                isDataError = true;
                dataErrorMsg.add("L_Volume");
                JSONObject jobject = new JSONObject();
                jobject.put("code", "L_Volume");
                jdataErrorMsg.add(jobject);
            }

            if ("Y".equals(material.get("TN_FLAG"))) {
                try {
                    if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.tlno.kr"))) {
                        tlNum = rowData.get(PropUtils.get("excel.template.coa.tlno.kr")).toString();
                    } else if (!DataUtils.isEmpty(rowData, PropUtils.get("excel.template.coa.tlno.en"))){
                        tlNum = rowData.get(PropUtils.get("excel.template.coa.tlno.en")).toString();
                    }
                    if (!StringUtils.isEmpty(quantity)) {
                    } else {
                        emptyMsg.add("L_TankLorryNo");
                        JSONObject jobject = new JSONObject();
                        jobject.put("code", "L_TankLorryNo");
                        jemptyMsg.add(jobject);
                        isEmptyMessage =true;
                    }
                } catch (Exception ex) {
                    isDataError = true;
                    dataErrorMsg.add("L_TankLorryNo");
                    JSONObject jobject = new JSONObject();
                    jobject.put("code", "L_TankLorryNo");
                    jdataErrorMsg.add(jobject);
                }

                masterMap.put("ETC", tlNum);
            }

            if(isEmptyMessage){
                //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                rtnMap.put("SUCCESS", "N");
                rtnMap.put("JSONTYPE", "Y");
                rtnMap.put("MSG", jemptyMsg.toJSONString());
                return rtnMap;
            }
            if(isDataError){
                //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                rtnMap.put("SUCCESS", "N");
                rtnMap.put("JSONTYPE", "Y");
                rtnMap.put("MSG", jdataErrorMsg.toJSONString());
                return rtnMap;
            }
            masterMap.put("STOCK_DATE", stockDate);
            masterMap.put("MF_DATE", mfDate);
            masterMap.put("E_DATE", eDate);
            masterMap.put("QUANTITY", quantity);
            masterMap.put("COA_STATUS", "A");


            UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String user_gubun = user.getUSER_GUBUN();
            String user_id = user.getUsername();
            masterMap.put("CREATOR", user_id);

            masterMap.put("LANGUAGE_TYPE",parameterMap.get("USER_LANG"));

            if (coaMgmtService.getExistCount(masterMap) > 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                rtnMap.put("SUCCESS", "N");
                rtnMap.put("MSG", "M_COAAlreadyExist");
                //동일 데이터가 존재합니다. 삭제 후  입력해주세요
                return rtnMap;
            }

            coaMgmtService.insertCOAMaster(masterMap);

            // 데이터 갯수 확인
            // logger.debug(" >>>>>>>>>>>>> [ rowData.size() : " + rowData.size() +  " / specList.size() : " + specList.size() + " ]" );
            if ("Y".equals(material.get("TN_FLAG"))) {
                // TN_FLAG = "Y"인 경우 SPEC 이외의 컬럼은 4개
                if ((rowData.size() - 6) != specList.size()) {
                   // TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    rtnMap.put("SUCCESS", "N");
                    rtnMap.put("MSG", "업로드한 COA의 스펙 갯수가 기준 스펙 갯수와 다릅니다.");

                    return rtnMap;
                }
            } else {
                if ((rowData.size() - 5) != specList.size()) {
                    //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    rtnMap.put("SUCCESS", "N");
                    rtnMap.put("MSG", "업로드한 COA의 스펙 갯수가 기준 스펙 갯수와 다릅니다.");

                    return rtnMap;
                }
            }



            try {
                for (int j = 0 ; j < specList.size() ; j++) {
                    LinkedHashMap<String,Object> spec = specList.get(j);
                    detailMap = new HashMap<String, Object>();

                    // 데이터 유무 확인
                    if (rowData.containsKey(spec.get("SPEC_NAME"))) {
                        // resulVal = (String) rowData.get(spec.get("SPEC_NAME"));
                        detailMap.put("RESULT_VALUE", rowData.get(spec.get("SPEC_NAME")).toString());
                    } else if (rowData.containsKey(spec.get("SPEC_ENG_NAME"))) {
                        // resulVal = (String) rowData.get(spec.get("SPEC_NAME"));
                        detailMap.put("RESULT_VALUE", rowData.get(spec.get("SPEC_ENG_NAME")).toString());
                    } else {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        rtnMap.put("SUCCESS", "N");
                        rtnMap.put("MSG", "전 항목 COA Data가 입력되지 않았습니다.");
                        return rtnMap;
                    }
                    //필수여부 체크
                    if(spec.get("REQUIRED_YN").equals("Y")){
                        if(detailMap.get("RESULT_VALUE")==null||detailMap.get("RESULT_VALUE").equals("")||detailMap.get("RESULT_VALUE").equals("N/A")){
                            rtnMap.put("SUCCESS", "N");
                            rtnMap.put("MSG", "전 항목 COA Data가 입력되지 않았습니다.");
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return rtnMap;
                        }
                    }
                    //실수 변경 로직
                    String RESULT_VALUE = changeCoaSpecData(detailMap.get("RESULT_VALUE").toString(),spec.get("DECIMAL_PLACE").toString());
                    detailMap.put("RESULT_VALUE",RESULT_VALUE);

                    detailMap.put("COA_ID", coaNumber);
                    detailMap.put("LOT_NO", masterMap.get("LOT_NO"));
                    detailMap.put("MATERIAL_ID", masterMap.get("MATERIAL_ID"));
                    detailMap.put("VENDOR_ID", masterMap.get("VENDOR_ID"));
                    detailMap.put("FACTORY_ID", masterMap.get("FACTORY_ID"));
                    detailMap.put("SPEC_ID", spec.get("SPEC_ID"));
                    detailMap.put("SPEC_LSL", spec.get("SPEC_LSL"));
                    detailMap.put("SPEC_USL", spec.get("SPEC_USL"));
                    detailMap.put("SPEC_LCL", spec.get("SPEC_LCL"));
                    detailMap.put("SPEC_UCL", spec.get("SPEC_UCL"));
                    detailMap.put("IS_SPECIN", "Y");
                    //20231207 관리선 존재시, 값 검증
                    if (null != spec.get("SPEC_LSL") && Double.parseDouble(spec.get("SPEC_LSL").toString()) > Double.parseDouble(detailMap.get("RESULT_VALUE").toString())) {
                        detailMap.put("IS_SPECIN", "N");
                    } else if (null != spec.get("SPEC_USL") && Double.parseDouble(spec.get("SPEC_USL").toString()) < Double.parseDouble(detailMap.get("RESULT_VALUE").toString())) {
                        detailMap.put("IS_SPECIN", "N");
                    }

                    coaMgmtService.insertCOADetail(detailMap);
                }

                Map<String,Object> checkParam = new HashMap<String, Object>();
                checkParam.put("L_COAId", masterMap.get("COA_ID"));
                checkParam.put("L_VendorId", masterMap.get("L_VendorId"));
                checkParam.put("L_MaterialId", masterMap.get("L_MaterialId"));
                checkParam.put("L_FactoryId", masterMap.get("L_FactoryId"));
                checkParam.put("L_LotNo", masterMap.get("LOT_NO"));
                checkParam.put("L_StockDate", masterMap.get("STOCK_DATE"));

                Map<String,Object> checkMap = coaMgmtService.regCheck(checkParam);
                Map<String,Object> checkMap2 = coaMgmtService.regSpecCheck(checkParam);

                if(Integer.parseInt(checkMap2.get("IS_SPEC_YN_CNT").toString())>0){
                    ArrayList<String> paramFactoryList = new ArrayList<String>();

                    paramFactoryList.add(masterMap.get("L_FactoryId").toString());


//                    MANAGE_PART_YN;            //-- PART 관리자 □  이건 전체 다 보낸다.
//                    MANAGE_COA_YN_01;          //-- COA 반도체 □ 1200
//                    MANAGE_COA_YN_02;          //-- COA 첨단소재 □ 1300
//                    MANAGE_COA_YN_03;          //-- COA 첨단-삼기 □ 8100
//                    MANAGE_COA_YN_04;          //-- COA 통신디바이스 □  ( 4100  , 3100  ,5100  )


                    String mailCodeMsg = "";
                    if("1200".equals(masterMap.get("L_FactoryId").toString()) && "Y".equals(checkMap.get("ISPR"))){
                        //parameterMap.put("job_type", "COASPECOUT1200PR");
                        paramFactoryList.add("MANAGE_COA_YN_01");
                        mailCodeMsg="(익산PR)";
                    }else if("1200".equals(masterMap.get("L_FactoryId").toString()) && "N".equals(checkMap.get("ISPR"))){
                        //parameterMap.put("job_type", "COASPECOUT1200WET");
                        paramFactoryList.add("MANAGE_COA_YN_01");
                        mailCodeMsg="(익산WET)";
                    }else if("1300".equals(masterMap.get("L_FactoryId").toString()) && "Y".equals(checkMap.get("ISPR"))){
                        //parameterMap.put("job_type", "COASPECOUT1300PR");
                        paramFactoryList.add("MANAGE_COA_YN_02");
                        mailCodeMsg="(평택PR)";
                    }else if("1300".equals(masterMap.get("L_FactoryId").toString()) && "N".equals(checkMap.get("ISPR"))){
                        //parameterMap.put("job_type", "COASPECOUT1300WET");
                        paramFactoryList.add("MANAGE_COA_YN_02");
                        mailCodeMsg="(평택WET)";}

                    List<HashMap> targetList = null;
                    parameterMap.put("factoryList", paramFactoryList);
                    targetList = coaMgmtService.getEmailTargetUser(parameterMap);
                    //List<LinkedHashMap<String, Object>> targetList = mailReciveSettingDAO.getEmailTargetUser(parameterMap);

                    String[] targetUserArr = new String[targetList.size()];
                    for(int j=0; j<targetList.size(); j++){
                        HashMap<String, Object>  targetUser = targetList.get(j);
                        targetUserArr[j] = targetUser.get("EMAIL").toString();
                    }

                    MimeMessage msg = mailSender.createMimeMessage();
                    MimeMessageHelper msgHelper = new MimeMessageHelper(msg, true, "UTF-8");


                    msgHelper.setTo(targetUserArr);
                    msgHelper.setFrom(PropUtils.get("mail.sender.account"));
                    System.out.println(material.toString());
                    System.out.println(masterMap.toString());

                    String contents="";
                    String subject="[DP-PORTAL] "+"SPEC OUT 알림";
                    contents += "■SPECOUT 데이터가 등록되었습니다."+mailCodeMsg;
                    contents += "<table width='700' cellpadding='0' cellspacing='0' border='1' style='border-collapse:collapse; border-color:#CECFCE; border-style:solid; font-size:12px;' >"
                            + "<tr>"
                            + "<td align='center' width='100' style='background-color:#EFEFEF;' >원료명</td>"
                            + "<td align='center' width='100' style='background-color:#EFEFEF;' >COA_ID</td>"
                            + "<td align='center' width='100' style='background-color:#EFEFEF;' >LOT</td>"
                            + "<td align='center' width='100' style='background-color:#EFEFEF;' >출하일</td>"
                            + "<td align='center' width='100' style='background-color:#EFEFEF;' >스펙명</td>"
                            + "</tr>";
                    contents += "<tr>"

                            + "<td align='center' >" + material.get("MATERIAL_NAME").toString() + "</td>"
                            + "<td align='center' >" + masterMap.get("COA_ID").toString() + "</td>"
                            + "<td align='center' >" + masterMap.get("LOT_NO").toString() + "</td>"
                            + "<td align='center' >" + masterMap.get("STOCK_DATE").toString() + "</td>"
                            + "<td align='center' >" + checkMap2.get("SPEC_OUT_NAMES").toString() + "</td>"
                            + "</tr></table>";

                    msgHelper.setSubject(subject);
                    String mailContents = "";
                    mailContents +=contents;
                    msgHelper.setText("", mailContents);
                    try {
                        //coa specout 프로세스 개선으로  pr wet 전체 전송
                        //if("Y".equals(checkMap.get("ISPR"))){
                        mailSender.send(msg);
                        //}
                    } catch (Exception e) {
                        // TODO: handle exception
                        //logger.info("smtp error");  sylee
                        //logger.info(e.getMessage());  sylee
                        e.printStackTrace();
                    }
                }
            }
            catch (NumberFormatException e){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                rtnMap.put("SUCCESS", "N");
                rtnMap.put("MSG", "관리선이 설정된 Data에는 특수문자를 제외하고 등록 바랍니다. 이상 시 동우화인켐 담당자 연락 바랍니다.");
                return rtnMap;
            }
            catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                rtnMap.put("SUCCESS", "N");
                rtnMap.put("MSG", "필요한 스펙데이터가 없습니다");
                return rtnMap;
            }
        }

        rtnMap.put("SUCCESS", "Y");
        return rtnMap;
    }




//end
}
