package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.BaseConfigDTO;
import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import com.dongwoo.SQM.siteMgr.service.SvhcListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.cfg.PropertiesConfigSource;
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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.WorksheetDocument;
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
            model.addAttribute("SVHC_ROW_COUNT",Integer.parseInt(baseConfigDTOInfo.getCONFIG_VALUE()));

            BaseConfigDTO baseConfigDTO = BaseConfigService.getBaseConfig_InfoCode("SVHC_SAMPLE_FILE");
            model.addAttribute("SVHC_SAMPLE_FILE_NAME",baseConfigDTO.getCONFIG_VALUE());

        } catch (Exception e) {
        }

        return "/svhclist/list";
    }

    //파일 업로드
    //@RequestMapping(value ="/svhcList/upload",method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    //@PostMapping("/svhcData/upload")
    @RequestMapping("/svhcData/upload")
    public ResponseEntity<?> upload_SvhcList(HttpServletRequest request, Model model
                                            ,@RequestParam(value="file", required = false) MultipartFile file
                                            ,@RequestParam(value="SVHC_FILE", required = false) MultipartFile svhcFile ) throws IOException{
        try {
            if(file != null){
                if(!file.isEmpty()){
                    //xlsx 인지 xls 인지 구분
                    String fileName = file.getOriginalFilename();
                    List<SvhcListDTO> svhcListDTOList = new ArrayList<>();
                    int svhcCnt =0;

                    Workbook workbook = null;
                    Sheet worksheet = null;


                    if(fileName.endsWith(".xlsx")) {
                        workbook = new XSSFWorkbook(file.getInputStream());
                        worksheet = workbook.getSheetAt(0);

                    }else if(fileName.endsWith(".xls")){
                        workbook = new HSSFWorkbook(file.getInputStream());
                        worksheet = workbook.getSheetAt(0);

                    }else{
                        return ResponseEntity.ok("|||[ERROR]||| Invalid file format. Only .xls and .xlsx are supported.");
                    }

                    //for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
                    for(int i = 19; i<worksheet.getPhysicalNumberOfRows()-1; i++){
                        svhcCnt++;
                        SvhcListDTO svhcListDTO = new SvhcListDTO();

                        DataFormatter formatter = new DataFormatter();
                        Row row = worksheet.getRow(i);

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



                    //전체삭제
                    svhcListService.deletAll();
                    svhcListService.insert_svhcBulk(svhcListDTOList);

                }
            }

            // Row수 업데이트
            BaseConfigDTO baseConfigDTOInfo = BaseConfigService.getBaseConfig_InfoCode("SVHC_ROW_COUNT");
            String svhcrow = GetParam(request,"SVHC_ROW_COUNT","");
            baseConfigDTOInfo.setCONFIG_VALUE(svhcrow);

            BaseConfigService.update(baseConfigDTOInfo);

            //샘플경로업데이트
            if(svhcFile != null) {
                if (!svhcFile.isEmpty()) {
                    String svhc_filepath = svhcListService.uploadFileData("svhc/sample", svhcFile);
                    String svhc_filename = svhcFile.getOriginalFilename();

                    BaseConfigDTO baseDTO = BaseConfigService.getBaseConfig_InfoCode("SVHC_SAMPLE_FILE");
                    baseDTO.setCONFIG_VALUE(svhc_filename);
                    baseDTO.setCONFIG_OPTION(svhc_filepath);
                    baseDTO.setCONFIG_OPTION2(svhc_filename);

                    BaseConfigService.update(baseDTO);

                }
            }

        } catch (Exception e) {
            //throw new RuntimeException(e);
            return ResponseEntity.ok("|||[ERROR]|||" + e.getMessage());
        }

        return ResponseEntity.ok("OK");

    }

    @RequestMapping("/svhcData/DeleteSvhcSampleFile")
    public ResponseEntity<?> DeleteSvhcSampleFile(HttpServletRequest request){
        BaseConfigDTO baseDTO = BaseConfigService.getBaseConfig_InfoCode("SVHC_SAMPLE_FILE");
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


