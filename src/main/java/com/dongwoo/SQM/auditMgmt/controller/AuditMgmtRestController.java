package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditSearchResult;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.config.security.UserCustom;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuditMgmtRestController {
    @Autowired
    private IsoAuthService isoAuthService;

    // admin -검색어로 ISO 업체리스트
    @GetMapping("/searchAuditMgmt")
    public AuditSearchResult searchCompanies(@RequestParam("type") String type, @RequestParam("code") String code, @RequestParam("name") String name, @RequestParam("state") String state, Criteria criteria) {
        // 검색 조건에 맞는 결과를 반환
        List<AuditMgmtDTO> isoAuthList = isoAuthService.searchCompanies(type, code,name,state,criteria);

        // 페이지 네이게이션과 함께 반환
        int total = isoAuthService.getTotalByKeyword(type,code,name,state);  // 검색 조건에 맞는 총 개수
        PageDTO pageMaker = new PageDTO(total, 10, criteria);          // 페이지 DTO 생성

        // 검색 결과와 페이지 네비게이터를 포함한 결과 객체 반환
        return new AuditSearchResult(isoAuthList, pageMaker);
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

    //IsoAuth 정보 신규 저장
    @PostMapping("/setIsoAuthData")
    public ResponseEntity<?> setIsoAuthData(@RequestBody AuditMgmtDTO isoAuthDTO) {
        int resultCnt = isoAuthService.saveAuth(isoAuthDTO);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if(resultCnt > 0){
            return ResponseEntity.ok("Form submitted successfully!");
        }else{
            return ResponseEntity.ok("Form submitted fail!");
        }
    }
    @Value("${Upload.path.attach}")
    private String uploadPath;

    /**
     * ISO 인증서 정보 리스트
     * @param criteria
     * @return
     */
    @GetMapping("/isoAuthItemList")
    public List<IsoAuthItemDTO> getIsoAuthList(Criteria criteria, @RequestParam("COM_CODE") String com_code) {
        // ISO 인증서 리스트를 가져옵니다
        List<IsoAuthItemDTO> companyIsoAuthList = isoAuthService.getList(criteria, com_code);
        return companyIsoAuthList;
    }
    @GetMapping("/isoAuthItemList/pageMaker")
    public PageDTO getIsoAuthPage(Criteria criteria) {
        // pageMaker 객체를 생성하고 반환
        int total = isoAuthService.getTotal();  // 전체 데이터 개수
        PageDTO pageMaker = new PageDTO(total, 10, criteria);  // 10은 한 페이지당 보여줄 항목 수
        return pageMaker;
    }

    // admin -만료일 - 업체별 ISO 인증서 정보
    @GetMapping("/searchIsoExpDate")
    public List<IsoAuthItemDTO> searchExpDateIsoAuth(@RequestParam("code") String code, @RequestParam("name") String name, @RequestParam("expDate") String expDate,  Criteria criteria) {
        // 검색 조건에 맞는 결과를 반환
        List<IsoAuthItemDTO> isoAuthList = isoAuthService.getExpDateList(code,name,expDate,criteria);
        return isoAuthList;
    }

    @RequestMapping("/auditMgmt/isoDetail")
    public String loadPage1(Model model) {
        // 필요한 데이터를 모델에 추가
        return "isoAuth/detail";
    }

    @GetMapping("/getIsoAuthFileDown")
    public ResponseEntity<FileSystemResource> downloadFile(@RequestParam("fileName") String filename) {
        try {
            // 파일 경로 설정
            File file = new File(uploadPath + File.separator + filename);
            if (file.exists()) {
                // 파일이 존재하면, 파일을 반환
                FileSystemResource resource = new FileSystemResource(file);

                // 파일 MIME 타입을 설정 (파일의 확장자에 맞는 MIME 타입을 설정)
                String mimeType = "application/octet-stream"; // 기본값은 application/octet-stream (모든 파일 유형에 적용 가능)
                try {
                    mimeType = Files.probeContentType(file.toPath()); // 파일의 MIME 타입을 자동으로 추정
                } catch (Exception e) {
                    e.printStackTrace(); // 오류가 발생하면 기본 MIME 타입을 사용
                }

                // 파일 다운로드 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                // 파일 다운로드시 원본 파일명으로 다운로드되도록 설정
                String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
                headers.add("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
                headers.setContentType(MediaType.parseMediaType(mimeType)); // MIME 타입 설정

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            } else {
                // 파일이 존재하지 않으면 404 상태 반환
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //IsoAuth 정보 제출
    @PostMapping("/sendIsoAuthData")
    public String sendIsoAuthData(@RequestParam("data") String data, @RequestParam(value = "file_name") MultipartFile[] fileNames, @AuthenticationPrincipal UserCustom user) throws IOException {
        try {
            isoAuthService.saveIsoAuthData(data, fileNames, user);  // 데이터와 파일을 서비스에 전달
            return "데이터가 성공적으로 저장되었습니다.";
        } catch (Exception e) {
            return "데이터 저장에 실패했습니다: " + e.getMessage();
        }



    }


}
