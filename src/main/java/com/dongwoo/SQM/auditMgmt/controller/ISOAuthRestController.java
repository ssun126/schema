package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.IsoAuthDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoSearchResult;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.config.security.UserCustom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class ISOAuthRestController {
    @Autowired
    private IsoAuthService isoAuthService;

    // admin -검색어로 ISO 업체리스트
    @GetMapping("/searchIsoAuth")
    public IsoSearchResult searchCompanies(@RequestParam("code") String code, @RequestParam("name") String name, @RequestParam("state") String state,  Criteria criteria) {
        // 검색 조건에 맞는 결과를 반환
        List<IsoAuthDTO> isoAuthList = isoAuthService.searchCompanies(code,name,state,criteria);

        // 페이지 네이게이션과 함께 반환
        int total = isoAuthService.getTotalByKeyword(code,name,state);  // 검색 조건에 맞는 총 개수
        PageDTO pageMaker = new PageDTO(total, 10, criteria);          // 페이지 DTO 생성

        // 검색 결과와 페이지 네비게이터를 포함한 결과 객체 반환
        return new IsoSearchResult(isoAuthList, pageMaker);
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
    public ResponseEntity<?> setIsoAuthData(@RequestBody IsoAuthDTO isoAuthDTO) {
        int resultCnt = isoAuthService.save(isoAuthDTO);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if(resultCnt > 0){
            return ResponseEntity.ok("Form submitted successfully!");
        }else{
            return ResponseEntity.ok("Form submitted fail!");
        }
    }
    @Value("${Upload.path}")
    private String uploadPath;

    //IsoAuth 정보 신규 저장
    @PostMapping("/setIsoAuthItemData")
    public String setIsoAuthItemData(IsoAuthItemDTO isoAuthItemDTO, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("isoAuthItemDTO = " + isoAuthItemDTO);
        if (file.isEmpty()) {
            log.info("No file uploaded");
        } else {
            // 파일 업로드 처리 시작
            UUID uuid = UUID.randomUUID(); // 랜덤으로 식별자를 생성

            //String directory = "D:/devp/";
            String fileName = uuid + "_" + file.getOriginalFilename(); // UUID와 파일이름을 포함된 파일 이름으로 저장

            File saveFile = new File(uploadPath, URLEncoder.encode(fileName, StandardCharsets.UTF_8)); // projectPath는 위에서 작성한 경로, name은 전달받을 이름

            file.transferTo(saveFile);

            isoAuthItemDTO.setFILE_NAME(fileName);
            isoAuthItemDTO.setFILE_PATH(uploadPath + fileName); // static 아래부분의 파일 경로로만으로도 접근이 가능
            // 파일 업로드 처리 끝
        }
        int resultCnt = isoAuthService.saveItem(isoAuthItemDTO);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if(resultCnt > 0){
            return "File uploaded successfully!";
        }else{
            return "Form submitted fail!";
        }
    }

    /**
     * ISO 인증서 정보 리스트
     * @param criteria
     * @return
     */
    @GetMapping("/isoAuthItemList")
    public List<IsoAuthItemDTO> getIsoAuthList(Criteria criteria) {
        // ISO 인증서 리스트를 가져옵니다
        List<IsoAuthItemDTO> companyIsoAuthList = isoAuthService.getList(criteria);
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
    public ResponseEntity<FileSystemResource> downloadFile(@RequestParam("filename") String filename) {
        try {
            // 파일 경로 설정
            File file = new File(uploadPath +   URLEncoder.encode(filename, StandardCharsets.UTF_8));
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 파일 리소스 생성
            FileSystemResource resource = new FileSystemResource(file);

            // 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //IsoAuth 정보 제출
    @PostMapping("/sendIsoAuthData")
    public ResponseEntity<?> sendIsoAuthData(@RequestBody IsoAuthDTO isoAuthDTO, @AuthenticationPrincipal UserCustom user) {

        /*연동 필요*/

        /*전송 데이터 저장*/
        log.info("isoAuthDTO = " + isoAuthDTO);
        isoAuthDTO.setAPPROVE_STATE("SEND"); //제출
        isoAuthDTO.setAUTH_TYPE("ISO");
        isoAuthDTO.setREG_DW_USER_IDX(user.getUsername());
        isoAuthDTO.setUP_DW_USER_IDX(user.getUsername());

        int resultCnt = isoAuthService.save(isoAuthDTO);

        //상태 업데이트
        IsoAuthItemDTO isoAuthItemDTO = new IsoAuthItemDTO();
        isoAuthItemDTO.setCOM_CODE(isoAuthDTO.getCOM_CODE());
        isoAuthItemDTO.setITEM_STATE("SEND"); //제출
        isoAuthItemDTO.setREG_DW_USER_IDX(user.getUsername());
        isoAuthItemDTO.setUP_DW_USER_IDX(user.getUsername());
        isoAuthService.updateStatus(isoAuthItemDTO);

        /* 메일 발송 */

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if(resultCnt > 0){
            return ResponseEntity.ok("Form submitted successfully!");
        }else{
            return ResponseEntity.ok("Form submitted fail!");
        }
    }
}
