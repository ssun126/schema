package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.SafetyItemDTO;
import com.dongwoo.SQM.siteMgr.service.SafetyItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SafetyItemController {
    private final SafetyItemService safetyItemService;

    @GetMapping("/admin/siteMgr/safetyItem")
    public String findAll(Model model){
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<SafetyItemDTO> safetyItemDTOList = safetyItemService.findAll();
            String safetyItemListDTOJsonStr = mapper.writeValueAsString(safetyItemDTOList);
            model.addAttribute("safetyItemList",safetyItemListDTOJsonStr);

            log.info("test121121212====================="+safetyItemListDTOJsonStr);

        } catch (Exception e) {
        }

        return "safetyItem/list";
    }

    //파일 업로드
    //@RequestMapping(value ="/svhcList/upload",method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @PostMapping("/safetyItem/upload")
    public String upload_safetyList(@RequestParam(value="file") MultipartFile file , Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        List<SafetyItemDTO> safetyItemDTOList = new ArrayList<>();
        for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
            SafetyItemDTO safetyItemDTO = new SafetyItemDTO();

            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            String PROVISION = formatter.formatCellValue((row.getCell(0)));
            String AUDIT_ID = formatter.formatCellValue((row.getCell(1)));
            String AUDIT_ITEM = formatter.formatCellValue((row.getCell(2)));
            String AUDIT_CRITERIA = formatter.formatCellValue((row.getCell(3)));
            String POINT_CRITERIA = formatter.formatCellValue((row.getCell(4)));
            String DIVISION1 = formatter.formatCellValue((row.getCell(5)));
            String DIVISION2= formatter.formatCellValue((row.getCell(6)));

            safetyItemDTO.setPROVISION(PROVISION);
            safetyItemDTO.setAUDIT_ID(AUDIT_ID);
            safetyItemDTO.setAUDIT_ITEM(AUDIT_ITEM);
            safetyItemDTO.setAUDIT_CRITERIA(AUDIT_CRITERIA);
            safetyItemDTO.setPOINT_CRITERIA(POINT_CRITERIA);
            safetyItemDTO.setDIVISION1(DIVISION1);
            safetyItemDTO.setDIVISION2(DIVISION2);

            safetyItemDTOList.add(safetyItemDTO);
            log.info("labourItemDTO====================="+safetyItemDTO);
        }

        try {
            log.info("test333333333====================="+safetyItemDTOList);
            //전체삭제
            safetyItemService.deletAll();
            safetyItemService.insert_safetyBulk(safetyItemDTOList);

            log.info("test444444=====================");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return  "redirect:/admin/siteMgr/safetyItem";

    }
}
