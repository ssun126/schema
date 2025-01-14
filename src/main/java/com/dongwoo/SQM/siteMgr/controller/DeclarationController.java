package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.service.DeclarationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DeclarationController {
    private final DeclarationService declarationService;
    private final com.dongwoo.SQM.siteMgr.service.BaseConfigService BaseConfigService;

    @GetMapping("/admin/siteMgr/declarationList")
    public String findAll(Model model){
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<DeclarationDTO> declarationDTOList = declarationService.findAll();
            String declarationDTOListJsonStr = mapper.writeValueAsString(declarationDTOList);
            model.addAttribute("declarationDataList",declarationDTOListJsonStr);

            BaseConfigDTO baseConfigDTOInfo = BaseConfigService.getBaseConfig_InfoCode("DECLARATION_REV");
            model.addAttribute("DECLARATION_REV",baseConfigDTOInfo.getCONFIG_VALUE());

            BaseConfigDTO baseConfigDTO = BaseConfigService.getBaseConfig_InfoCode("DECL_SAMPLE_FILE");
            model.addAttribute("DECL_SAMPLE_FILE_NAME",baseConfigDTO.getCONFIG_VALUE());


        } catch (Exception e) {
        }

        return "/deClarationList/list";
    }

    //@PostMapping("/declaration/upload")
    @RequestMapping("/declaration/upload")
    public ResponseEntity<?> upload_Declaration(HttpServletRequest request, Model model
                                                ,@RequestParam(value = "file", required = false)MultipartFile file
                                                ,@RequestParam(value="DECL_FILE", required = false) MultipartFile declFile ) throws IOException {

        try{
            if(file != null) {
                if (!file.isEmpty()) {
                    //xlsx 인지 xls 인지 구분
                    String fileName = file.getOriginalFilename();
                    List<DeclarationDTO> declarationDTOList = new ArrayList<>();
                    int declCnt = 0;

                    Workbook workbook = null;
                    Sheet worksheet = null;

                    if(fileName.endsWith(".xlsx")){
                        workbook = new XSSFWorkbook(file.getInputStream());
                        worksheet = workbook.getSheetAt(0);
                    }else if(fileName.endsWith(".xls")){
                        workbook = new HSSFWorkbook(file.getInputStream());
                        worksheet = workbook.getSheetAt(0);
                    }else{
                        return ResponseEntity.ok("|||[ERROR]||| Invalid file format. Only .xls and .xlsx are supported.");
                    }

                    for(int i = 3; i<worksheet.getPhysicalNumberOfRows(); i++){
                        declCnt++;
                        DeclarationDTO declarationDTO = new DeclarationDTO();

                        DataFormatter formatter = new DataFormatter();
                        Row row = worksheet.getRow(i);

                        String DECL_NUM = formatter.formatCellValue(row.getCell(1));
                        String DECL_SUB_NUM = formatter.formatCellValue(row.getCell(2));
                        String DECL_NAME = formatter.formatCellValue(row.getCell(3));
                        String DECL_CASNUM = formatter.formatCellValue(row.getCell(4));
                        String DECL_WEIGHT = formatter.formatCellValue(row.getCell(5));
                        String DECL_CLASS = formatter.formatCellValue(row.getCell(6));
                        String DECL_GROUND = formatter.formatCellValue(row.getCell(7));


                        log.info("엑셀 값 : "+declCnt+"!!!!!" +DECL_NUM+ " !!!"+DECL_SUB_NUM+ " !!!"+DECL_NAME+ " !!!"+DECL_CASNUM+ " !!!"+DECL_WEIGHT+ " !!!"+DECL_CLASS+ " !!!"+DECL_GROUND);
                        declarationDTO.setDECL_IDX(declCnt);
                        declarationDTO.setDECL_NUM(DECL_NUM);
                        declarationDTO.setDECL_SUB_NUM(DECL_SUB_NUM);
                        declarationDTO.setDECL_NAME(DECL_NAME);
                        declarationDTO.setDECL_CASNUM(DECL_CASNUM);
                        declarationDTO.setDECL_WEIGHT(DECL_WEIGHT);
                        declarationDTO.setDECL_CLASS(DECL_CLASS);
                        declarationDTO.setDECL_GROUND(DECL_GROUND);

                        declarationDTOList.add(declarationDTO);

                    }



                    //전체삭제
                    declarationService.deleteAll();
                    declarationService.insert_deClarationBulk(declarationDTOList);

                }
            }


            // Row수 업데이트
            BaseConfigDTO baseConfigDTOInfo = BaseConfigService.getBaseConfig_InfoCode("DECLARATION_REV");
            String svhcrow = GetParam(request,"DECLARATION_REV","");
            baseConfigDTOInfo.setCONFIG_VALUE(svhcrow);

            BaseConfigService.update(baseConfigDTOInfo);

            //샘플경로업데이트
            if(declFile != null) {
                if (!declFile.isEmpty()) {
                    String decl_filepath = declarationService.uploadFileData("decl/sample", declFile);
                    String decl_filename = declFile.getOriginalFilename();

                    BaseConfigDTO baseDTO = BaseConfigService.getBaseConfig_InfoCode("SVHC_SAMPLE_FILE");
                    baseDTO.setCONFIG_VALUE(decl_filename);
                    baseDTO.setCONFIG_OPTION(decl_filepath);
                    baseDTO.setCONFIG_OPTION2(decl_filename);

                    BaseConfigService.update(baseDTO);

                }
            }

        }catch (SQLException e){
            //throw new RuntimeException(e);
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");

    }

    @RequestMapping("/declaration/DeleteDeclSampleFile")
    public ResponseEntity<?> DeleteDeclSampleFile(HttpServletRequest request){
        BaseConfigDTO baseDTO = BaseConfigService.getBaseConfig_InfoCode("DECL_SAMPLE_FILE");
        baseDTO.setCONFIG_VALUE("");
        baseDTO.setCONFIG_OPTION("");
        baseDTO.setCONFIG_OPTION2("");

        BaseConfigService.update(baseDTO);

        return ResponseEntity.ok("OK");
    }

    private String GetParam(HttpServletRequest request, String pName, String pDefault) {
        String ParamValue = request.getParameter(pName);

        if (ParamValue == null || ParamValue.isEmpty()) {
            ParamValue = pDefault;
        };

        return ParamValue;
    }
}
