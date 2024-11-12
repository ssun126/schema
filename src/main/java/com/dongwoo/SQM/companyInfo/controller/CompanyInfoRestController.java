package com.dongwoo.SQM.companyInfo.controller;

import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.dto.PageDTO;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.companyInfo.dto.CpCodeDTO;
import com.dongwoo.SQM.companyInfo.dto.SearchResult;
import com.dongwoo.SQM.companyInfo.service.CompanyInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class CompanyInfoRestController {
    @Autowired
    private CompanyInfoService companyInfoService;

    // 검색 API 처리 (여러 검색 조건 처리)
    @GetMapping("/searchCompanies")
    public SearchResult searchCompanies(@RequestParam("name") String name, @RequestParam("code") String code, @RequestParam("nation") String nation, Criteria criteria) {
        // 검색 조건에 맞는 결과를 반환
        List<CompanyInfoDTO> companyInfoList = companyInfoService.searchCompanies(name, code, nation, criteria);

        // 페이지 네이게이션과 함께 반환
        int total = companyInfoService.getTotalByKeyword(name, code, nation);  // 검색 조건에 맞는 총 개수
        PageDTO pageMaker = new PageDTO(total, 10, criteria);          // 페이지 DTO 생성

        // 검색 결과와 페이지 네비게이터를 포함한 결과 객체 반환
        return new SearchResult(companyInfoList, pageMaker);
    }

    //업체 정보 가져오기
    @GetMapping("/getCompanyData")
    public ResponseEntity<?> getCompanyData(@RequestParam("param")String param) {
        CompanyInfoDTO companyInfo = companyInfoService.findByCompanyId(param);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if (companyInfo != null) {
            return ResponseEntity.ok().body(companyInfo);  // 회사 정보가 있을 경우 응답
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
        }
    }

    //업체정보 신규 저장
    @PostMapping("/setCompanyData")
    public ResponseEntity<?> setCompanyData(@RequestBody CpCodeDTO cpCodeDTO) {
        log.info("companyInfoDTO??????????????    "+cpCodeDTO);
        int resultCnt = companyInfoService.save(cpCodeDTO);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if(resultCnt > 0){
            return ResponseEntity.ok("Form submitted successfully!");
        }else{
            return ResponseEntity.ok("Form submitted fail!");
        }
    }

    @GetMapping("/companyList")
    public List<CompanyInfoDTO> getCompanyList(Criteria criteria) {
        // 회사 정보 리스트를 가져옵니다
        List<CompanyInfoDTO> companyInfoList = companyInfoService.getList(criteria);
        log.info("companyInfoList>>>>>>>>>>"+companyInfoList);
        // 반환되는 데이터가 JSON 형식으로 자동 변환됩니다
        return companyInfoList;
    }

    @GetMapping("/cpCodeList/pageMaker")
    public PageDTO getCompanyPage(Criteria criteria) {
        // pageMaker 객체를 생성하고 반환
        int total = companyInfoService.getTotal();  // 전체 데이터 개수
        PageDTO pageMaker = new PageDTO(total, 10, criteria);  // 10은 한 페이지당 보여줄 항목 수
        log.info("pageMaker>>>>>>>>>>"+pageMaker);
        return pageMaker;
    }
    // 엑셀 파일을 다운로드하는 엔드포인트
    @GetMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel(Criteria criteria) throws IOException {
        List<CompanyInfoDTO> companyInfoList = companyInfoService.getList(criteria);

        // 엑셀 워크북과 시트 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Company Info");

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] header = {"Company Code", "Company Name", "Country", "Status"};
        for (int i = 0; i < header.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
        }

        // 데이터 삽입
        int rowNum = 1;
        for (CompanyInfoDTO company : companyInfoList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(company.getCOM_CODE());
            row.createCell(1).setCellValue(company.getCOM_NAME());
            row.createCell(2).setCellValue(company.getCOM_NATION());
            row.createCell(3).setCellValue(company.getCOM_STATUS());
        }

        // 엑셀 파일을 바이트 배열로 변환
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        byte[] excelFile = byteArrayOutputStream.toByteArray();

        // ResponseEntity로 파일 전송
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=company_info.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }

}
