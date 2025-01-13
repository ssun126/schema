package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.AuditItemPointDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.LabourHRDTO;
import com.dongwoo.SQM.auditMgmt.repository.AuditMgmtRepository;
import com.dongwoo.SQM.auditMgmt.repository.IsoAuthRepository;
import com.dongwoo.SQM.auditMgmt.repository.LabourHRRepository;
import com.dongwoo.SQM.config.security.UserCustom;
import com.dongwoo.SQM.siteMgr.dto.LabourItemDTO;
import com.dongwoo.SQM.siteMgr.service.LabourItemService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LabourHRService {
    private final LabourHRRepository labourHRRepository;
    private final AuditMgmtRepository auditMgmtRepository;
    private final LabourItemService labourItemService;
    private final IsoAuthRepository isoAuthRepository;

    @Value("${Upload.path.audit}")
    private String uploadPath;

    public int insertFileInfo(LabourHRDTO labourHRDTO) {
        return labourHRRepository.insertFileInfo(labourHRDTO);
    }

    public void saveAuthData(String tableData, String type, MultipartFile fileNames, String chkType) throws IOException {

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();
        int loginIdx = user.getUSER_IDX();

        //회사별 Audit 데이터 저장
        AuditMgmtDTO authDTO = new AuditMgmtDTO();
        authDTO.setCOM_CODE(comCode);
        authDTO.setAUTH_TYPE(type);
        authDTO.setREG_DW_USER_IDX(loginIdx);  // 등록자
        authDTO.setUP_DW_USER_IDX(loginIdx);  // 수정자
        authDTO.setSEND_USER_IDX(loginIdx);  //제출자
        authDTO.setINPUT_TYPE(chkType);  //제출자
        authDTO.setAPPROVE_STATE("SEND"); //제출

        int comCnt = auditMgmtRepository.selectAuthCnt(authDTO);
        if(comCnt > 0) {
            int rsltCnt = auditMgmtRepository.updateAuth(authDTO);  // updateItem
        }else{
            int rsltCnt = auditMgmtRepository.insertAuth(authDTO); //저장
        }
        AuditMgmtDTO authMgmtDTO = auditMgmtRepository.selectAuth(authDTO);

        // 파일이 존재하면 처리
        if (Objects.equals(chkType, "file") && fileNames != null && authMgmtDTO != null) {

            int comUserIdx = user.getCOM_USER_IDX();
            XSSFWorkbook workbook = new XSSFWorkbook(fileNames.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            List<AuditItemPointDTO> labourItemDTOList = new ArrayList<>();

            log.info("worksheet:::"+worksheet.getPhysicalNumberOfRows());
            for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
                AuditItemPointDTO auditItemDTO = new AuditItemPointDTO();

                DataFormatter formatter = new DataFormatter();
                XSSFRow row = worksheet.getRow(i);

                String AUTH_TYPE = "LABOUR";
                String AUDIT_ID = formatter.formatCellValue((row.getCell(1)));
                double POINT = Double.parseDouble(formatter.formatCellValue((row.getCell(6)))); //점수
                String AUDIT_COMMENT = formatter.formatCellValue((row.getCell(7))); //근거자료
                auditItemDTO.setCOM_CODE(comCode);
                auditItemDTO.setAUDIT_ID(AUDIT_ID);
                auditItemDTO.setAUTH_TYPE(AUTH_TYPE);
                auditItemDTO.setPOINT(POINT);
                auditItemDTO.setAUDIT_COMMENT(AUDIT_COMMENT);
                auditItemDTO.setREG_COM_USER_IDX(comUserIdx);
                auditItemDTO.setAUTH_SEQ(authMgmtDTO.getAUTH_SEQ());

                labourItemDTOList.add(auditItemDTO);
                log.info("labourItemDTO====================="+auditItemDTO);
            }

            try {
                insertLabourItem(labourItemDTOList); //파일의 점수 저장
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            //파일 저장
            LabourHRDTO dto = new LabourHRDTO();
            String filePath = saveFile(fileNames);

            // Paths 클래스를 사용하여 파일명 추출
            Path path = Paths.get(filePath);
            String fileName = path.getFileName().toString();  // 경로에서 파일명만 추출
            dto.setCOM_CODE(comCode);
            dto.setAUTH_TYPE("LABOUR");
            dto.setAUTH_SEQ(authMgmtDTO.getAUTH_SEQ());
            dto.setFILE_NAME(fileName);  // 파일명 추가
            dto.setFILE_PATH(filePath);  // 파일 경로 추가

            int rtCnt = labourHRRepository.insertFileInfo(dto);  // insert

        }else{
            // JSON 문자열을 DTO 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            List<AuditItemPointDTO> authItems = objectMapper.readValue(tableData, new TypeReference<List<AuditItemPointDTO>>() {});

            for (AuditItemPointDTO dto : authItems) {
                dto.setCOM_CODE(comCode);
                dto.setAUTH_TYPE("LABOUR");
                assert authMgmtDTO != null;
                dto.setAUTH_SEQ(authMgmtDTO.getAUTH_SEQ());
                int rtCnt = labourHRRepository.insertAuthItem(dto);  // insert
                log.info("데이터 저장 Count: " + rtCnt);
            }
        }
    }
    @Transactional
    public void saveLabourUploadData(@RequestParam(value="file") MultipartFile file, int auth_seq) throws IOException {
        log.info("saveLabourUploadData====================="+auth_seq);
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int comUserIdx = user.getCOM_USER_IDX();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        List<AuditItemPointDTO> labourItemDTOList = new ArrayList<>();
        for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
            AuditItemPointDTO auditItemDTO = new AuditItemPointDTO();

            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            String AUTH_TYPE = "LABOUR";
            String AUDIT_ID = formatter.formatCellValue((row.getCell(1)));
            double POINT = Double.parseDouble(formatter.formatCellValue((row.getCell(6)))); //점수
            String AUDIT_COMMENT = formatter.formatCellValue((row.getCell(7))); //근거자료

            auditItemDTO.setAUDIT_ID(AUDIT_ID);
            auditItemDTO.setAUTH_TYPE(AUTH_TYPE);
            auditItemDTO.setPOINT(POINT);
            auditItemDTO.setAUDIT_COMMENT(AUDIT_COMMENT);
            auditItemDTO.setREG_COM_USER_IDX(comUserIdx);
            auditItemDTO.setAUTH_SEQ(auth_seq);

            labourItemDTOList.add(auditItemDTO);
            log.info("labourItemDTO====================="+auditItemDTO);
        }

        try {
            insertLabourItem(labourItemDTOList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("파일이 없습니다.");
        }

        // 저장할 파일의 경로 설정 (파일 이름에 타임스탬프를 추가하여 중복 방지)
        String fileName = file.getOriginalFilename()+ "_"+System.currentTimeMillis() ;
        File destinationFile  = new File(uploadPath  + File.separator +  fileName);
        file.transferTo(destinationFile );  // 파일 저장

        return destinationFile.getAbsolutePath();  // 저장된 파일 경로 반환
    }

    //업체별 정보
    public LabourHRDTO getCompanyAuth(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return labourHRRepository.getCompanyAuth(params);
    }

    //업체별 인증 파일 정보
    public LabourHRDTO getCompanyAuthFile(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return labourHRRepository.getCompanyAuthFile(params);
    }

    //업체별 평가항목/점수 정보
    public List<AuditItemPointDTO> getCompanyAuthItemPoint(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return labourHRRepository.getCompanyAuthItemPoint(params);
    }

    public void insertLabourItem(List<AuditItemPointDTO> labourItemDTOList) throws SQLException {
        log.info("test11111111111111111111111");
        try{
            for(AuditItemPointDTO dto : labourItemDTOList){
                log.info("test2-1");
                auditMgmtRepository.insertItemPoint(dto);
            }
        }catch (Exception e){
            log.info(e.toString());
        }
    }

}
