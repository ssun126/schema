package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.SvhcListDTO;
import com.dongwoo.SQM.siteMgr.service.SvhcListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.Banner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SvhcListController {

    private final SvhcListService svhcListService;

    @GetMapping("/admin/siteMgr/svhcList")
    public String findAll(Model model){
        List<SvhcListDTO> svhcListDTOList = svhcListService.findAll();
        model.addAttribute("svhcDataList",svhcListDTOList);
        return "/svhclist/list";
    }

    //파일 업로드
    //@RequestMapping(value ="/svhcList/upload",method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @PostMapping("/svhcData/upload")
    public String upload_SvhcList(@RequestParam(value="file") MultipartFile file , Model model) throws IOException{
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        List<SvhcListDTO> svhcListDTOList = new ArrayList<>();
        for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
            SvhcListDTO svhcListDTO = new SvhcListDTO();

            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            String SVHC_NUM = formatter.formatCellValue((row.getCell(0)));
            String SVHC_NAME = formatter.formatCellValue((row.getCell(1)));
            String SVHC_CASNUM = formatter.formatCellValue((row.getCell(2)));
            String SVHC_EUNUM = formatter.formatCellValue((row.getCell(3)));

            svhcListDTO.setSVHC_NUM(SVHC_NUM);
            svhcListDTO.setSVHC_NAME(SVHC_NAME.replaceAll("'","''"));
            svhcListDTO.setSVHC_CASNUM(SVHC_CASNUM);
            svhcListDTO.setSVHC_EUNUM(SVHC_EUNUM);

            svhcListDTOList.add(svhcListDTO);

        }

        try {
            //전체삭제
            svhcListService.deletAll();
            log.info("test startttttttttttt");
            svhcListService.insert_svhcBulk(svhcListDTOList);
            log.info("test 6666666666666666666666");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return  "redirect:/admin/siteMgr/svhcList";

    }
}
