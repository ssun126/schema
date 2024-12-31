package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.service.*;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.common.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auditMgmt")
public class AuditMgmtRestController {
    @Autowired
    private IsoAuthService isoAuthService;
    @Autowired
    private LabourHRService labourHRService;
    @Autowired
    private SafetyHealthService safetyHealthService;
    @Autowired
    private AuditCommonService auditCommonService;
    @Autowired
    private AuditResultService auditResultService;
    @Autowired
    private ConflictMineralsService conflictMineralsService;

    @Value("${Upload.path.audit}")
    private String uploadPath;

    private final FileStorageService fileStorageService;

    @Autowired
    public AuditMgmtRestController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
    // admin -Audit 공통 검색어로 업체 정보 가져오기
    @PostMapping("/searchAuditMgmt")
    public List<IsoAuthItemDTO> searchCompanies(HttpServletRequest req) {
        String name = req.getParameter("searchName");
        String code = req.getParameter("searchCode");
        String state = req.getParameter("searchState");
        String type = req.getParameter("searchType");
        log.info("type??"+type);

        // 검색 결과와 페이지
        return isoAuthService.searchCompanies(type, code, name, state);
    }

    //Audit 결과 리스트 조회
    //업체 목록 검색 LIST
    @PostMapping("/searchAuditRsltMgmt")
    public List<AuditMgmtDTO> searchAuditRsltMgmt(HttpServletRequest req) {
        String name = req.getParameter("searchName");
        String code = req.getParameter("searchCode");
        String state = req.getParameter("searchState");
        String type = req.getParameter("searchType");
        log.info("type??"+type);

        return auditResultService.searchCompanies(type, code, name, state);
    }

    /**
     * ISO 인증서 정보 리스트
     * @param criteria
     * @return
     */
    @GetMapping("/isoAuthItemList")
    @ResponseBody
    public List<IsoAuthItemDTO> getIsoAuthList(Criteria criteria, @RequestParam("COM_CODE") String com_code) {
        // 선택 회사의 ISO 인증서 리스트를 가져옵니다
        List<IsoAuthItemDTO> companyIsoAuthList = isoAuthService.getList(com_code);

        return companyIsoAuthList;
    }



    //IsoAuth 정보 가져오기
    @GetMapping("/getIsoAuthData")
    public ResponseEntity<?> getIsoAuthData(@RequestParam("param1")String param1, @RequestParam("param2")String param2) {
        IsoAuthItemDTO isoAuthItem = isoAuthService.findByIsoAuthItem(param1, param2);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if (isoAuthItem != null) {
            return ResponseEntity.ok().body(isoAuthItem);  // 회사 정보가 있을 경우 응답
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
        }
    }

    //IsoAuth 승인/반려 처리
    @PostMapping("/setIsoAuthData")
    public ResponseEntity<?> setIsoAuthData(@RequestParam("reason")String reason, @RequestParam("com_code")String com_code, @RequestParam("auth_code")String auth_code, @RequestParam("state")String state) {
        //ISO 인증서 승인 상태 업데이트
        int resultCnt = isoAuthService.updateStatus(com_code, auth_code, reason, state);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if(resultCnt > 0){
            return ResponseEntity.ok("Form submitted successfully!");
        }else{
            return ResponseEntity.ok("Form submitted fail!");
        }
    }

    //IsoAuth 정보 제출
    @PostMapping("/sendIsoAuthData")
    public String sendIsoAuthData(@RequestParam("data") String data,@RequestParam("type") String type, @RequestParam(value = "file_name", required = false) MultipartFile[] fileNames) throws IOException {
        try {
            isoAuthService.saveIsoAuthData(data, type, fileNames);  // 데이터와 파일을 서비스에 전달
            return "데이터가 성공적으로 저장되었습니다.";
        } catch (Exception e) {
            return "데이터 저장에 실패했습니다: " + e.getMessage();
        }
    }

    //인증서 파일 다운로드
    @GetMapping("/getIsoAuditFileDown")
    public ResponseEntity<Resource> downloadISOFile(@RequestParam("filename") String filename) throws Exception {
        Path path = fileStorageService.getISOUploadDirectory().resolve(filename).normalize();
        FileSystemResource resource = new FileSystemResource(path);

        if (!resource.exists() || !resource.isReadable()) {
            throw new Exception("File not found or not readable: " + filename);
        }

        // Encode the filename in UTF-8 and handle non-ASCII characters
        String encodedFilename = encodeFileName(filename);

        // Return the file as a downloadable resource
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    //인증서 파일 다운로드
    @GetMapping("/getAuditFileDown")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) throws Exception {
        Path path = fileStorageService.getUploadDirectory().resolve(filename).normalize();
        FileSystemResource resource = new FileSystemResource(path);

        if (!resource.exists() || !resource.isReadable()) {
            throw new Exception("File not found or not readable: " + filename);
        }

        // Encode the filename in UTF-8 and handle non-ASCII characters
        String encodedFilename = encodeFileName(filename);

        // Return the file as a downloadable resource
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    //Audit 공통 승인/반려 처리
    @PostMapping("/setAuthData")
    public ResponseEntity<?> setAuthData(@RequestParam("reason")String reason, @RequestParam("com_code")String com_code, @RequestParam("auth_seq")int auth_seq, @RequestParam("state")String state, @RequestParam("auth_type")String auth_type, @RequestParam("point")double point) {
        log.info("setAuthData!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        //인증서 승인 상태 업데이트
        int resultCnt = auditCommonService.updateStatus(com_code, auth_seq, reason, state, auth_type, point);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if(resultCnt > 0){
            return ResponseEntity.ok("Form submitted successfully!");
        }else{
            return ResponseEntity.ok("Form submitted fail!");
        }
    }


    /**
     * 노동환경 제출
     * @param fileNames
     * @return
     * @throws IOException
     */
    @PostMapping("/sendLabourAuthData")
    public String sendLabourAuthData(@RequestParam("data") String data, @RequestParam("chkType") String chkType, @RequestParam(value = "file_name", required = false) MultipartFile fileNames) throws IOException {
        try {

            labourHRService.saveAuthData(data,"LABOUR", fileNames, chkType);

            return "데이터가 성공적으로 저장되었습니다.";
        } catch (Exception e) {
            return "데이터 저장에 실패했습니다: " + e.getMessage();
        }
    }



    /**
     * 안전보건/환경, 품질관리 제출
     * @param fileNames
     * @return
     * @throws IOException
     */
    @PostMapping("/sendAuthData")
    public String sendCommonAuthData(@RequestParam("data") String data, @RequestParam("type") String type, @RequestParam("total") int total, @RequestParam(value = "file_name", required = false) MultipartFile[] fileNames) throws IOException {
        try {
            auditCommonService.saveCommonAuthData(data, type, total, fileNames);

            return "데이터가 성공적으로 저장되었습니다.";
        } catch (Exception e) {
            return "데이터 저장에 실패했습니다: " + e.getMessage();
        }
    }

    /**
     * 분쟁광물 제출
     * @param fileNames
     * @return
     * @throws IOException
     */
    @PostMapping("/sendConflictAuthData")
    public String sendConflictAuthData(@RequestParam("data") String data, @RequestParam("warranty") String warranty, @RequestParam("modify") String modify, @RequestParam("type") String type, @RequestParam(value = "file_name", required = false) MultipartFile[] fileNames) throws IOException {
        try {
            conflictMineralsService.saveAuthData(data, warranty, modify, type, fileNames);

            return "데이터가 성공적으로 저장되었습니다.";
        } catch (Exception e) {
            return "데이터 저장에 실패했습니다: " + e.getMessage();
        }
    }

    //파일명 인코딩
    private String encodeFileName(String filename) throws UnsupportedEncodingException {
        // Encode filename in UTF-8
        String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");

        // Ensure that non-ASCII characters are correctly encoded
        return encodedFilename;
    }



}
