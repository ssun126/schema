package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditSearchResult;
import com.dongwoo.SQM.auditMgmt.dto.LabourHRDTO;
import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import com.dongwoo.SQM.auditMgmt.service.LabourHRService;
import com.dongwoo.SQM.auditMgmt.service.SafetyHealthService;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.common.service.FileStorageService;
import com.dongwoo.SQM.config.security.UserCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    @Value("${Upload.path.attach}")
    private String uploadPath;

    private final FileStorageService fileStorageService;

    @Autowired
    public AuditMgmtRestController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
    // admin -Audit 공통 검색어로 업체 정보 가져오기
    @GetMapping("/searchAuditMgmt")
    public AuditSearchResult searchCompanies(@RequestParam("type") String type, @RequestParam("code") String code, @RequestParam("name") String name, @RequestParam("state") String state, Criteria criteria) {
        // 검색 조건에 맞는 결과를 반환
        List<AuditMgmtDTO> isoAuthList = isoAuthService.searchCompanies(type, code, name, state, criteria);

        // 페이지 네이게이션과 함께 반환
        int total = isoAuthService.getTotalByKeyword(type,code,name,state);  // 검색 조건에 맞는 총 개수
        PageDTO pageMaker = new PageDTO(total, 10, criteria);          // 페이지 DTO 생성

        // 검색 결과와 페이지 네비게이터를 포함한 결과 객체 반환
        return new AuditSearchResult(isoAuthList, pageMaker);
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
        List<IsoAuthItemDTO> companyIsoAuthList = isoAuthService.getList(criteria, com_code);

        return companyIsoAuthList;
    }

    // admin -만료일 - 업체별 ISO 인증서 정보
    @GetMapping("/searchIsoExpDate")
    public List<IsoAuthItemDTO> searchExpDateIsoAuth(@RequestParam("code") String code, @RequestParam("name") String name, @RequestParam("expDate") String expDate,  Criteria criteria) {
        // 검색 조건에 맞는 결과를 반환
        List<IsoAuthItemDTO> isoAuthList = isoAuthService.getExpDateList(code,name,expDate,criteria);
        return isoAuthList;
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
    @GetMapping("/getIsoAuthFileDown")
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

    /**
     * 노동환경 제출
     * @param fileNames
     * @return
     * @throws IOException
     */
    @PostMapping("/sendLabourAuthData")
    public String sendLabourAuthData(@RequestParam(value = "file_name", required = false) MultipartFile fileNames) throws IOException {
        try {

            labourHRService.saveAuthData("LABOUR" , fileNames);

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
    public String sendLabourAuthData(@RequestParam("data") String data, @RequestParam("type") String type, @RequestParam(value = "file_name", required = false) MultipartFile[] fileNames) throws IOException {
        try {
            auditCommonService.saveAuthData(data, type, fileNames);

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
