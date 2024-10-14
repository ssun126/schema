package com.dongwoo.SQM.common.controller;

import com.dongwoo.SQM.common.dto.ExcelDTO;
import com.dongwoo.SQM.common.service.ExcelService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class ExcelPageController {
    private final ExcelService excelService;

    public ExcelPageController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @GetMapping("/excel/showExcel")
    public String showExcel() {
        log.info("show!");

        return "/excel/showExcel";
    }

    /*
     *   엑셀 다운로드 FORM 출력
     *   @return String(View Name)
     * */
    @GetMapping("/excel/downloadForm")
    public String excelDownloadForm() {
        return "excelDownloadForm";
    }

    /*
     *   엑셀 다운로드
     *   @param HttpServletResponse
     *   @throws IOException
     *   @throws RuntimeException
     * */
    @GetMapping("/excel/download")
    public void excelDownLoad(HttpServletResponse response) {
        log.info("/excel/download 요청 도착!!");

        // 엑셀로 출력할 리스트 조회
        List<ExcelDTO> excelList = excelService.findAll();

        // 엑셀 다운로드 로직 실행
       // excelUtils.studentExcelDownload(excelList, response);
    }
}
