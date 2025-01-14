package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import com.dongwoo.SQM.siteMgr.service.SvhcListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SvhcListController {

    private final SvhcListService svhcListService;
    private final com.dongwoo.SQM.siteMgr.service.BaseConfigService BaseConfigService;

    @GetMapping("/admin/siteMgr/svhcList")
    public String findAll(Model model){
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<SvhcListDTO> svhcListDTOList = svhcListService.findAll();
            String svhcListDTOListJsonStr = mapper.writeValueAsString(svhcListDTOList);
            model.addAttribute("svhcDataList",svhcListDTOListJsonStr);

            BaseConfigDTO baseConfigDTOInfo = BaseConfigService.getBaseConfig_InfoCode("SVHC_ROW_COUNT");
            model.addAttribute("svhcRowCount",baseConfigDTOInfo.getCONFIG_VALUE());
        } catch (Exception e) {
        }

        return "/svhclist/list";
    }

    //파일 업로드
    //@RequestMapping(value ="/svhcList/upload",method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    //@PostMapping("/svhcData/upload")
    @RequestMapping("/svhcData/upload")
    public ResponseEntity<?> upload_SvhcList(@RequestParam(value="file") MultipartFile file , Model model) throws IOException{
        //xlsx 인지 xls 인지 구분
        String fileName = file.getOriginalFilename();
        List<SvhcListDTO> svhcListDTOList = new ArrayList<>();
        int svhcCnt =0;

        if(fileName.endsWith(".xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);

            //for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
            for(int i = 19; i<worksheet.getPhysicalNumberOfRows()-1; i++){
                svhcCnt++;
                SvhcListDTO svhcListDTO = new SvhcListDTO();

                DataFormatter formatter = new DataFormatter();
                XSSFRow row = worksheet.getRow(i);

                String SVHC_NUM = formatter.formatCellValue((row.getCell(0)));
                String SVHC_NAME = formatter.formatCellValue((row.getCell(1)));

                String SVHC_CASNUM = formatter.formatCellValue((row.getCell(10)));
                String SVHC_EUNUM = formatter.formatCellValue((row.getCell(11)));

                svhcListDTO.setSVHC_IDX(svhcCnt);
                svhcListDTO.setSVHC_NUM(SVHC_NUM);
                svhcListDTO.setSVHC_NAME(SVHC_NAME.replaceAll("'","''"));
                svhcListDTO.setSVHC_CASNUM(SVHC_CASNUM);
                svhcListDTO.setSVHC_EUNUM(SVHC_EUNUM);

                svhcListDTOList.add(svhcListDTO);
            }
        }else if(fileName.endsWith(".xls")){
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            HSSFSheet worksheet = workbook.getSheetAt(0);

            //for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
            for(int i = 19; i<worksheet.getPhysicalNumberOfRows()-1; i++){
                svhcCnt++;
                SvhcListDTO svhcListDTO = new SvhcListDTO();

                DataFormatter formatter = new DataFormatter();
                HSSFRow row = worksheet.getRow(i);

                String SVHC_NUM = formatter.formatCellValue((row.getCell(0)));
                String SVHC_NAME = formatter.formatCellValue((row.getCell(1)));

                String SVHC_CASNUM = formatter.formatCellValue((row.getCell(10)));
                String SVHC_EUNUM = formatter.formatCellValue((row.getCell(11)));

                svhcListDTO.setSVHC_IDX(svhcCnt);
                svhcListDTO.setSVHC_NUM(SVHC_NUM);
                svhcListDTO.setSVHC_NAME(SVHC_NAME.replaceAll("'","''"));
                svhcListDTO.setSVHC_CASNUM(SVHC_CASNUM);
                svhcListDTO.setSVHC_EUNUM(SVHC_EUNUM);

                svhcListDTOList.add(svhcListDTO);
            }
        }else{
            return ResponseEntity.ok("|||[ERROR]||| Invalid file format. Only .xls and .xlsx are supported.");
        }



        try {
            //전체삭제
            svhcListService.deletAll();
            svhcListService.insert_svhcBulk(svhcListDTOList);

            // Row수 업데이트
            BaseConfigDTO baseConfigDTOInfo = BaseConfigService.getBaseConfig_InfoCode("SVHC_ROW_COUNT");
            baseConfigDTOInfo.setCONFIG_VALUE(String.valueOf(svhcListDTOList.size()));

            BaseConfigService.update(baseConfigDTOInfo);
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");

    }
}
