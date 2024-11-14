package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.service.DeclarationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DeclarationController {
    private final DeclarationService declarationService;

    @GetMapping("/admin/siteMgr/declarationList")
    public String findAll(Model model){
        List<DeclarationDTO> declarationDTOList = declarationService.findAll();
        model.addAttribute("declarationDataList",declarationDTOList);
        return "/deClarationList/list";
    }

    @PostMapping("/declaration/upload")
    public String upload_Declaration(@RequestParam(value = "file")MultipartFile file) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        List<DeclarationDTO> declarationDTOList = new ArrayList<>();

        for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
            DeclarationDTO declarationDTO = new DeclarationDTO();

            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            String DECL_NUM = formatter.formatCellValue(row.getCell(0));
            String DECL_SUB_NUM = formatter.formatCellValue(row.getCell(1));
            String DECL_NAME = formatter.formatCellValue(row.getCell(2));
            String DECL_CASNUM = formatter.formatCellValue(row.getCell(3));
            String DECL_WEIGHT = formatter.formatCellValue(row.getCell(4));
            String DECL_CLASS = formatter.formatCellValue(row.getCell(5));
            String DECL_GROUND = formatter.formatCellValue(row.getCell(6));


            log.info("엑셀 값 : " +DECL_NUM+ " !!!"+DECL_SUB_NUM+ " !!!"+DECL_NAME+ " !!!"+DECL_CASNUM+ " !!!"+DECL_WEIGHT+ " !!!"+DECL_CLASS+ " !!!"+DECL_GROUND);
            declarationDTO.setDECL_NUM(DECL_NUM);
            declarationDTO.setDECL_SUB_NUM(DECL_SUB_NUM);
            declarationDTO.setDECL_NAME(DECL_NAME);
            declarationDTO.setDECL_CASNUM(DECL_CASNUM);
            declarationDTO.setDECL_WEIGHT(DECL_WEIGHT);
            declarationDTO.setDECL_CLASS(DECL_CLASS);
            declarationDTO.setDECL_GROUND(DECL_GROUND);

            declarationDTOList.add(declarationDTO);

        }

        try{
            //전체삭제
            declarationService.deleteAll();
            declarationService.insert_deClarationBulk(declarationDTOList);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return "redirect:/admin/siteMgr/declarationList";

    }
}
