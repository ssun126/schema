package com.dongwoo.SQM.auditMgmt.controller;

import com.dongwoo.SQM.auditMgmt.dto.AuditItemPointDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.LabourHRDTO;
import com.dongwoo.SQM.auditMgmt.service.AuditCommonService;
import com.dongwoo.SQM.auditMgmt.service.LabourHRService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.LabourItemDTO;
import com.dongwoo.SQM.siteMgr.service.LabourItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class LabourHRController {
    private final LabourHRService labourHRService;
    private final LabourItemService labourItemService;
    private final AuditCommonService auditCommonService;

    @GetMapping("/admin/auditMgmt/labourHR")
    public String labourHRMain(Model model) {
        return "labourHR/adminMain";
    }

    @GetMapping("/admin/auditMgmt/labourHRDetail")
    public String labourHRDetail(Model model, @RequestParam("COM_CODE") String COM_CODE) {
        // 업체의 인증정보 가져오기 - 인증상태/인증일
        AuditMgmtDTO companyAuth = auditCommonService.getCompanyAuth("LABOUR", COM_CODE);
        model.addAttribute("companyAuth", companyAuth);

        // 회사의 노동환경 심사항목 정보를 가져옵니다.
        List<AuditItemPointDTO> auditItemPoint = labourHRService.getCompanyAuthItemPoint("LABOUR", COM_CODE);

        log.info("auditItemPoint+========"+auditItemPoint);
        for (AuditItemPointDTO dto: auditItemPoint){
            String originAudit = dto.getAUDIT_CRITERIA();
            String originPoint = dto.getPOINT_CRITERIA();
            String formattedAudit = originAudit != null ? originAudit.replace("\n", "<br>") : "";
            String formattedPoint = originPoint != null ? originPoint.replace("\n", "<br>") : "";
            dto.setAUDIT_CRITERIA(formattedAudit);
            dto.setPOINT_CRITERIA(formattedPoint);
        }
        log.info("dto+========"+auditItemPoint);
        model.addAttribute("auditItemPoint", auditItemPoint);

        LabourHRDTO companyAuthFile = labourHRService.getCompanyAuthFile("LABOUR", COM_CODE);
        model.addAttribute("companyAuthFile", companyAuthFile);

        return "labourHR/detail";
    }

    @GetMapping("/user/auditMgmt/labourHR")
    public String labourHRUserMain(Model model) {

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();

        // 업체의 인증정보 가져오기 - 인증상태/인증일
        AuditMgmtDTO companyAuth = auditCommonService.getCompanyAuth("LABOUR", comCode);
        model.addAttribute("companyAuth", companyAuth);

        // 회사의 노동환경 심사항목 정보를 가져옵니다.
        List<AuditItemPointDTO> auditItemPoint = labourHRService.getCompanyAuthItemPoint("LABOUR", comCode);
        for (AuditItemPointDTO dto: auditItemPoint){
            String originAudit = dto.getAUDIT_CRITERIA();
            String originPoint = dto.getPOINT_CRITERIA();
            String formattedAudit = originAudit != null ? originAudit.replace("\n", "<br>") : "";
            String formattedPoint = originPoint != null ? originPoint.replace("\n", "<br>") : "";
            dto.setAUDIT_CRITERIA(formattedAudit);
            dto.setPOINT_CRITERIA(formattedPoint);
        }
        model.addAttribute("auditItemPoint", auditItemPoint);

        LabourHRDTO companyAuthFile = labourHRService.getCompanyAuthFile("LABOUR", comCode);
        model.addAttribute("companyAuthFile", companyAuthFile);
        log.info("companyAuthFile;;;;;;;;"+companyAuthFile);

        return "labourHR/main";
    }

    //파일 업로드
    //@RequestMapping(value ="/svhcList/upload",method = RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @PostMapping("/user/auditMgmt/upload")
    public String uploadLabourData(@RequestParam(value="file") MultipartFile file , Model model) throws IOException {
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
