package com.dongwoo.SQM.siteMgr.controller;

import com.dongwoo.SQM.siteMgr.dto.QualityItemDTO;
import com.dongwoo.SQM.siteMgr.service.QualityItemService;
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
public class QualityItemController {
    private final QualityItemService qualityItemService;

    @GetMapping("/admin/siteMgr/qualityItem")
    public String findAll(Model model){
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<QualityItemDTO> qualityItemDTOList = qualityItemService.findAll();
            String qualityItemListDTOJsonStr = mapper.writeValueAsString(qualityItemDTOList);
            model.addAttribute("qualityItemList",qualityItemListDTOJsonStr);

            log.info("test121121212====================="+qualityItemListDTOJsonStr);

        } catch (Exception e) {
        }

        return "qualityItem/list";
    }

    //파일 업로드
    //@RequestMapping(value ="/svhcList/upload",method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @PostMapping("/qualityItem/upload")
    public String upload_qualityList(@RequestParam(value="file") MultipartFile file , Model model) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        List<QualityItemDTO> qualityItemDTOList = new ArrayList<>();
        for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
            QualityItemDTO qualityItemDTO = new QualityItemDTO();

            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            String MAIN_ITEM = formatter.formatCellValue((row.getCell(0)));
            String AUDIT_ID = formatter.formatCellValue((row.getCell(1)));
            String AUDIT_ITEM = formatter.formatCellValue((row.getCell(2)));


            qualityItemDTO.setMAIN_ITEM(MAIN_ITEM);
            qualityItemDTO.setAUDIT_ID(Integer.parseInt(AUDIT_ID));
            qualityItemDTO.setAUDIT_ITEM(AUDIT_ITEM);


            qualityItemDTOList.add(qualityItemDTO);
            log.info("labourItemDTO====================="+qualityItemDTO);
        }

        try {
            log.info("test333333333====================="+qualityItemDTOList);
            //전체삭제
            qualityItemService.deletAll();
            qualityItemService.insert_qualityBulk(qualityItemDTOList);

            log.info("test444444=====================");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return  "redirect:/admin/siteMgr/qualityItem";

    }
}


