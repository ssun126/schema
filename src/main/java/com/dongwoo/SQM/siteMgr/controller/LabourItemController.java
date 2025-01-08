package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.LabourItemDTO;
import com.dongwoo.SQM.siteMgr.service.LabourItemService;
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
public class LabourItemController {
    private final LabourItemService labourItemService;

    @GetMapping("/admin/siteMgr/labourItem")
    public String findAll(Model model){
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<LabourItemDTO> labourItemDTOS = labourItemService.findAll();
            String labourItemListDTOListJsonStr = mapper.writeValueAsString(labourItemDTOS);
            model.addAttribute("labourItemList",labourItemListDTOListJsonStr);

            log.info("test121121212====================="+labourItemListDTOListJsonStr);

        } catch (Exception e) {
        }

        return "labourItem/list";
    }

    //파일 업로드
    //@RequestMapping(value ="/svhcList/upload",method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @PostMapping("/labourItem/upload")
    public String upload_labourList(@RequestParam(value="file") MultipartFile file , Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        List<LabourItemDTO> labourItemDTOList = new ArrayList<>();
        for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
            LabourItemDTO labourItemDTO = new LabourItemDTO();

            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            String PROVISION = formatter.formatCellValue((row.getCell(0)));
            String AUDIT_ID = formatter.formatCellValue((row.getCell(1)));
            String AUDIT_ITEM = formatter.formatCellValue((row.getCell(2)));
            String AUDIT_DESC = formatter.formatCellValue((row.getCell(3)));
            String AUDIT_CRITERIA = formatter.formatCellValue((row.getCell(4)));
            String POINT_CRITERIA = formatter.formatCellValue((row.getCell(5)));

            labourItemDTO.setPROVISION(PROVISION);
            labourItemDTO.setAUDIT_ID(AUDIT_ID);
            labourItemDTO.setAUDIT_ITEM(AUDIT_ITEM);
            labourItemDTO.setAUDIT_DESC(AUDIT_DESC);
            labourItemDTO.setAUDIT_CRITERIA(AUDIT_CRITERIA);
            labourItemDTO.setPOINT_CRITERIA(POINT_CRITERIA);

            labourItemDTOList.add(labourItemDTO);
            log.info("labourItemDTO====================="+labourItemDTO);
        }
        workbook.close();
        try {
            log.info("test333333333====================="+labourItemDTOList);
            //전체삭제
            labourItemService.deletAll();
            labourItemService.insert_labourBulk(labourItemDTOList);

            log.info("test444444=====================");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return  "redirect:/admin/siteMgr/labourItem";

    }


}
