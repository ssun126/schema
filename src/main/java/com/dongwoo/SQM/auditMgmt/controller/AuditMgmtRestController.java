package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.service.IsoAuthService;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import com.dongwoo.SQM.companyInfo.dto.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class AuditMgmtRestController {
    @Autowired
    private IsoAuthService isoAuthService;

    // 검색 API 처리 (여러 검색 조건 처리)
//    @GetMapping("/searchIsoAuth")
//    public SearchResult searchCompanies(@RequestParam("code") String code, @RequestParam("name") String name, Criteria criteria) {
//        // 검색 조건에 맞는 결과를 반환
//        List<IsoAuthItemDTO> companyInfoList = isoAuthService.searchCompanies(code, criteria);
//
//        // 페이지 네이게이션과 함께 반환
//        int total = isoAuthService.getTotalByKeyword(code, name);  // 검색 조건에 맞는 총 개수
//        PageDTO pageMaker = new PageDTO(total, 10, criteria);          // 페이지 DTO 생성
//
//        // 검색 결과와 페이지 네비게이터를 포함한 결과 객체 반환
//        return new SearchResult(companyInfoList, pageMaker);
//    }

    //IsoAuth 정보 가져오기
    @GetMapping("/getIsoAuthData")
    public ResponseEntity<?> getCompanyData(@RequestParam("param")String param) {
        IsoAuthItemDTO companyInfo = isoAuthService.findByCompanyId(param);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if (companyInfo != null) {
            return ResponseEntity.ok().body(companyInfo);  // 회사 정보가 있을 경우 응답
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
        }
    }

    //IsoAuth 정보 신규 저장
    @PostMapping("/setIsoAuthData")
    public ResponseEntity<?> setIsoAuthData(@RequestBody IsoAuthItemDTO isoAuthItemDTO) {
        int resultCnt = isoAuthService.save(isoAuthItemDTO);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if(resultCnt > 0){
            return ResponseEntity.ok("Form submitted successfully!");
        }else{
            return ResponseEntity.ok("Form submitted fail!");
        }
    }

    @GetMapping("/isoAuthItemList")
    public List<IsoAuthItemDTO> getIsoAuthList(Criteria criteria) {
        // ISO 인증서 리스트를 가져옵니다
        List<IsoAuthItemDTO> companyIsoAuthList = isoAuthService.getList(criteria);
        log.info("companyInfoList>>>>>>>>>>"+companyIsoAuthList);
        // 반환되는 데이터가 JSON 형식으로 자동 변환됩니다
        return companyIsoAuthList;
    }

    @GetMapping("/isoAuthItemList/pageMaker")
    public PageDTO getIsoAuthPage(Criteria criteria) {
        // pageMaker 객체를 생성하고 반환
        int total = isoAuthService.getTotal();  // 전체 데이터 개수
        PageDTO pageMaker = new PageDTO(total, 10, criteria);  // 10은 한 페이지당 보여줄 항목 수
        log.info("pageMaker>>>>>>>>>>"+pageMaker);
        return pageMaker;
    }
}
