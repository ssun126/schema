package com.dongwoo.SQM.partMgmt.controller;

import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.companyInfo.dto.CompanyInfoDTO;
import com.dongwoo.SQM.partMgmt.dto.PartMgmtDTO;
import com.dongwoo.SQM.partMgmt.service.PartMgmtService;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.Banner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user/partMgmt")
public class PartMgmtController {
    private final PartMgmtService partMgmtService;

    //메인 리스트 화면
    @GetMapping("/matReg")
    public String initPartMgmt(Model model) {

        //바인딩 리스트
        //검색 basecode 취급플랜트
        List<HashMap> searchPlantList = partMgmtService.getPlantList();
        //검색 basecode 승인현황
        //List<HashMap> searhApprovalStatus = partMgmtService.getApprovalStatus();

        model.addAttribute("searchPlantList",searchPlantList);
        //model.addAttribute("searhApprovalStatus", searhApprovalStatus);

        return "partMgmtList/main";

    }

    @GetMapping("/searchPartMgmt")
    public ResponseEntity<?>  searchPartMgmt(@RequestParam("code") String code, @RequestParam("name") String name,@RequestParam("reguser") String reguser,
                                            @RequestParam("useyn") String useyn,@RequestParam("plant") String plant,@RequestParam("approval") String approval,
                                            @RequestParam("sdate") String sdate,@RequestParam("edate") String edate){
        //ist<PartMgmtDTO> partMgmtDTOList = PartMgmtService.searchPartMgmt(code,name,reguser,useyn,plant,approval,sdate,edate);
        try{
            PartMgmtDTO parmDTO = new PartMgmtDTO();
            parmDTO.setPM_PART_CODE(code);
            parmDTO.setPM_PART_NAME(name);
            parmDTO.setPM_REG_USER(reguser);
            parmDTO.setPM_PLANT(plant);
            parmDTO.setPM_ACTIVE_YN(useyn);
            parmDTO.setPM_APPROVAL_STATUS(approval);
            parmDTO.setPM_SDATE(sdate);
            parmDTO.setPM_EDATE(edate);

            List<PartMgmtDTO> partMgmtDTOList = partMgmtService.searchPartMgmt(parmDTO);
            log.info("dataaaaaaaa??????????????"+partMgmtDTOList);
            return ResponseEntity.ok(partMgmtDTOList);

        } catch (Exception e) {
            System.out.println("검색 에러!!!: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }
    }


    //자재코드 리스트
    @GetMapping("/PartCodeList")
    public ResponseEntity<?> partCodeList(){
        try{
            List<HashMap> PartCdList = partMgmtService.getpartCodeList();
            return ResponseEntity.ok(PartCdList);

        }catch(Exception e){
            System.out.println(" 자재코드 리스트 업 에러 : "+e );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"서버 오류 발생\"}");
        }

    }

    @PostMapping("/setPartMgmtData")
    public ResponseEntity<?> insertData(@RequestBody PartMgmtDTO partMgmtDTO) {
        log.info("PartMgmtDTO??????????????????"+partMgmtDTO);

        int resultCnt = partMgmtService.save(partMgmtDTO);

        // 요청 결과 반환 (응답에 상태 코드와 데이터를 포함)
        if(resultCnt > 0){
            return ResponseEntity.ok("Form submitted successfully!");
        }else{
            return ResponseEntity.ok("Form submitted fail!");
        }

    }

    // 엑셀 파일을 다운로드하는 엔드포인트
    @GetMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam("code") String code, @RequestParam("name") String name,@RequestParam("reguser") String reguser,
                                                @RequestParam("useyn") String useyn,@RequestParam("plant") String plant,@RequestParam("approval") String approval,
                                                @RequestParam("sdate") String sdate,@RequestParam("edate") String edate) throws IOException {

        PartMgmtDTO parmDTO = new PartMgmtDTO();
        parmDTO.setPM_PART_CODE(code);
        parmDTO.setPM_PART_NAME(name);
        parmDTO.setPM_REG_USER(reguser);
        parmDTO.setPM_PLANT(plant);
        parmDTO.setPM_ACTIVE_YN(useyn);
        parmDTO.setPM_APPROVAL_STATUS(approval);
        parmDTO.setPM_SDATE(sdate);
        parmDTO.setPM_EDATE(edate);

        List<PartMgmtDTO> partMgmtDTOList = partMgmtService.searchPartMgmt(parmDTO);

        //List<PartMgmtDTO> companyInfoList = companyInfoService.getList(criteria);

        // 엑셀 워크북과 시트 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("PartCode Info");

        // 헤더 생성
        Row headerRow = sheet.createRow(0);
        String[] header = {"자재코드", "자재명칭", "취급plant", "국가", "신청ID","사용여부","승인현황","승인일자"};
        for (int i = 0; i < header.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
        }

        // 데이터 삽입
        int rowNum = 1;
        for (PartMgmtDTO partDTO : partMgmtDTOList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(partDTO.getPM_PART_CODE());
            row.createCell(1).setCellValue(partDTO.getPM_PART_NAME());
            row.createCell(2).setCellValue(partDTO.getPM_PLANT());
            row.createCell(3).setCellValue(partDTO.getPM_COUNTRY());
            row.createCell(4).setCellValue(partDTO.getPM_REG_USER());
            row.createCell(5).setCellValue(partDTO.getPM_ACTIVE_YN());
            row.createCell(6).setCellValue(partDTO.getPM_APPROVAL_STATUS());
            row.createCell(7).setCellValue(partDTO.getPM_MODIFY_USER());
        }

        // 엑셀 파일을 바이트 배열로 변환
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        byte[] excelFile = byteArrayOutputStream.toByteArray();

        // ResponseEntity로 파일 전송
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Part_Management_info.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }





}
