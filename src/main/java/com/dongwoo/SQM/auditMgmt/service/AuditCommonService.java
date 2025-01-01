package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditItemPointDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtHistDTO;
import com.dongwoo.SQM.auditMgmt.repository.AuditMgmtRepository;
import com.dongwoo.SQM.config.security.UserCustom;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditCommonService {
    private final AuditMgmtRepository auditMgmtRepository;

    @Value("${Upload.path.attach}")
    private String uploadPath;

    //Audit 공통 제출
    public void saveCommonAuthData(String tableData, String type, int total, MultipartFile[] fileNames) throws IOException {
        // JSON 문자열을 DTO 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        List<AuditItemPointDTO> authItems = objectMapper.readValue(tableData, new TypeReference<List<AuditItemPointDTO>>() {});

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();
        int loginIdx = user.getUSER_IDX();
        int comUserIdx = user.getCOM_USER_IDX();

        //회사별 Audit 데이터 저장
        AuditMgmtDTO authDTO = new AuditMgmtDTO();
        authDTO.setCOM_CODE(comCode);
        authDTO.setAUTH_TYPE(type); //SAFETY / QUALITY
        authDTO.setAPPROVE_STATE("SEND"); //제출
        authDTO.setREG_DW_USER_IDX(loginIdx);  // 파일 경로 추가
        authDTO.setUP_DW_USER_IDX(loginIdx);  // 파일 경로 추가
       // authDTO.setPOINT(total);  // 점수  점수는 승일시 넣도록
        log.info("authDTO::::::::::"+authDTO);

        int comCnt = auditMgmtRepository.selectAuthCnt(authDTO);
        if(comCnt > 0) {
            int rsltCnt = auditMgmtRepository.updateAuth(authDTO);  // updateItem
        }else{
            int rsltCnt = auditMgmtRepository.insertAuth(authDTO); //저장
        }

        // AUTH_SEQ 가져오기
        AuditMgmtDTO authMgmtDTO = auditMgmtRepository.selectAuth(authDTO);
        log.info(" authMgmtDTO.getAUTH_SEQ()::::::::::"+ authMgmtDTO.getAUTH_SEQ());
        //log.info(" total:::::::::"+  total);

        try {
            // 파일이 있는 경우 파일 정보 저장
            if (fileNames != null && fileNames.length > 0) {
                // 각 파일을 저장하고 경로를 DTO에 추가
                for (int i = 0; i < fileNames.length; i++) {
                    saveUploadData(fileNames[i], authMgmtDTO.getAUTH_SEQ(), type);//파일 내용 저장
                    String filePath = saveFile(fileNames[i]); //파일 저장

                    // Paths 클래스를 사용하여 파일명 추출
                    Path path = Paths.get(filePath);
                    String fileName = path.getFileName().toString();  // 경로에서 파일명만 추출

                    log.info("원본 파일명: " + fileNames[i].getOriginalFilename());
                    AuditMgmtDTO dto = new AuditMgmtDTO();
                    dto.setCOM_CODE(comCode);
                    dto.setAUTH_TYPE(type); // SAFETY / QUALITY
                    dto.setAUTH_SEQ(authMgmtDTO.getAUTH_SEQ());
                    dto.setFILE_NAME(fileName);  // 파일명 추가
                    dto.setFILE_PATH(filePath);  // 파일 경로 추가
                    log.info("dto: " + dto);
                    int rtCnt = auditMgmtRepository.insertFileInfo(dto);  // insert
                    log.info("파일저장 Count: " + rtCnt);
                }
            } else {

                log.info("authItems::::: " + authItems);
                //인증서 데이터 저장
                for (AuditItemPointDTO dto : authItems) {
                    dto.setCOM_CODE(comCode);
                    dto.setAUTH_TYPE(type); // SAFETY / QUALITY
                    dto.setAUTH_SEQ(authMgmtDTO.getAUTH_SEQ()); //제출 또는 저장
                    dto.setREG_COM_USER_IDX(comUserIdx); //업무자 IDX
                    log.info("insert dto::::: " + dto);
                    int rtCnt = auditMgmtRepository.insertItemPoint(dto);  // insert
                    log.info("데이터 저장 Count: " + rtCnt);
                }
            }
            log.info("회사별 Audit 데이터 저장 완료");
        }catch (Exception e){
            log.info("Error"+e.getMessage());
        }
    }

    @Transactional
    public void saveUploadData(@RequestParam(value="file") MultipartFile file, int auth_seq, String AUTH_TYPE) throws IOException {
        log.info("saveLabourUploadData====================="+auth_seq);
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int comUserIdx = user.getCOM_USER_IDX();
        String comCode = user.getCOM_CODE();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        List<AuditItemPointDTO> itemDTOList = new ArrayList<>();
        for(int i = 1; i<worksheet.getPhysicalNumberOfRows(); i++){
            AuditItemPointDTO auditItemDTO = new AuditItemPointDTO();

            DataFormatter formatter = new DataFormatter();
            XSSFRow row = worksheet.getRow(i);

            String AUDIT_ID = formatter.formatCellValue((row.getCell(1)));
            double POINT = Double.parseDouble(formatter.formatCellValue((row.getCell(5)))); //점수
            String AUDIT_COMMENT = formatter.formatCellValue((row.getCell(6))); //근거자료
            auditItemDTO.setCOM_CODE(comCode);
            auditItemDTO.setAUDIT_ID(AUDIT_ID);
            auditItemDTO.setAUTH_TYPE(AUTH_TYPE);
            auditItemDTO.setPOINT(POINT);
            auditItemDTO.setAUDIT_COMMENT(AUDIT_COMMENT);
            auditItemDTO.setREG_COM_USER_IDX(comUserIdx);
            auditItemDTO.setAUTH_SEQ(auth_seq);

            itemDTOList.add(auditItemDTO);
            log.info("itemDTOList====================="+auditItemDTO);
        }

        try {
            insertLabourItem(itemDTOList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("파일이 없습니다.");
        }

        // 저장할 파일의 경로 설정 (파일 이름에 타임스탬프를 추가하여 중복 방지)
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destinationFile  = new File(uploadPath  + File.separator +  fileName);
        file.transferTo(destinationFile );  // 파일 저장

        return destinationFile.getAbsolutePath();  // 저장된 파일 경로 반환
    }

    //업체별 심사 정보
    public AuditMgmtDTO getCompanyAuth(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return auditMgmtRepository.getCompanyAuth(params);
    }

    //업체별 심사 정보
    public AuditMgmtDTO getCompanyAuth(String type, String code, String AUTH_SEQ) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_SEQ", AUTH_SEQ);
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return auditMgmtRepository.getCompanyAuth(params);
    }

    //업체별 심사 히스토리 정보
    public List<AuditMgmtHistDTO> getCompanyAuthHistory(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return auditMgmtRepository.getCompanyAuthHistory(params);
    }
    public AuditMgmtHistDTO getCompanyAuthHistoryDetail(String type, String code, String AUTH_SEQ) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_SEQ", AUTH_SEQ);
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return auditMgmtRepository.getCompanyAuthHistoryDetail(params);
    }

    //업체별 인증 파일 정보
    public List<AuditMgmtDTO> getCompanyAuthFile(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return auditMgmtRepository.getCompanyAuthFile(params);
    }
    public List<AuditMgmtDTO> getCompanyAuthFile(String type, String code, String AUTH_SEQ) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_SEQ", AUTH_SEQ);
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return auditMgmtRepository.getCompanyAuthFile(params);
    }

    //업체별 평가항목 점수 정보
    public List<AuditItemPointDTO> getCompanyAuthItemPoint(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return auditMgmtRepository.getCompanyAuthItemPoint(params);
    }

    public void insertLabourItem(List<AuditItemPointDTO> itemDTOList) throws SQLException {
        log.info("test11111111111111111111111");
        try{
            for(AuditItemPointDTO dto : itemDTOList){
                log.info("test2-1");
                auditMgmtRepository.insertItemPoint(dto);
            }
        }catch (Exception e){
            log.info(e.toString());
        }
    }

    //업체별 만료일 정보 리스트
    public List<HashMap> getExpDateList(String code, String name, String type) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("AUTH_TYPE", type);
        return auditMgmtRepository.getExpDateList(params);
    }

    public  Map<String, String> getUserInfoMap(Map<String,Object> parameterMap) {
        return auditMgmtRepository.getUserInfo(parameterMap);
    }

    public  Map<String, String> getUserInfo(String code, String name, String type) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("AUTH_TYPE", type);
        return auditMgmtRepository.getUserInfo(params);
    }

    //업체별/메뉴별 전체 Auth 상태업데이트
    public int saveAuthResult(String com_code, String state, double totalPoint) {
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginIdx = user.getUSER_IDX();

        AuditMgmtDTO auditMgmtDTO = new AuditMgmtDTO();
        auditMgmtDTO.setCOM_CODE(com_code);
        auditMgmtDTO.setAUTH_TYPE("ISO");
        auditMgmtDTO.setAPPROVE_STATE(state);
        auditMgmtDTO.setPOINT(totalPoint);
        auditMgmtDTO.setREG_DW_USER_IDX(loginIdx);  // 생성자
        auditMgmtDTO.setUP_DW_USER_IDX(loginIdx);  // 수정자

        return auditMgmtRepository.saveAuthResult(auditMgmtDTO);
    }

    //Audit 공통 승인/반려
    public int updateStatus(String com_code, int auth_seq, String reason, String state, String auth_type, double point) {
        log.info("setAuthData22222222222222!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        AuditMgmtDTO auditMgmtDTO = new AuditMgmtDTO();
        auditMgmtDTO.setCOM_CODE(com_code);
        auditMgmtDTO.setAUTH_SEQ(auth_seq);
        auditMgmtDTO.setAUTH_TYPE(auth_type);
        auditMgmtDTO.setAPPROVE_STATE(state);
        auditMgmtDTO.setREASON(reason);

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginIdx = user.getUSER_IDX();
        auditMgmtDTO.setUP_DW_USER_IDX(loginIdx);

        //평가항목 Item 점수 가져오기
        List<AuditItemPointDTO> getItemList = getCompanyAuthItemPoint(auth_type, com_code);
        double totalPoint = 0;
        //심사종류별 최대점수 설정
        Map<String, Double> maxPoints = new HashMap<>();
        maxPoints.put("LABOUR", 20.0);
        maxPoints.put("SAFETY", 20.0);
        maxPoints.put("QUALITY", 50.0);
        maxPoints.put("CONFLICT", 6.0);
        maxPoints.put("ISO", 4.0);

        double maxPoint = maxPoints.getOrDefault(auth_type, 0.0); // auth_type이 없으면 0으로 설정
        if(auth_type.equals("CONFLICT")) {
            //입력받은 점수를 설정
            auditMgmtDTO.setPOINT(point);//합계 점수
        }else{
            for (AuditItemPointDTO item : getItemList) {
                totalPoint += item.getPOINT(); // 각 객체의 point 값을 더함
            }
            if(totalPoint >  maxPoint){ //심사종류별 최대점수
                totalPoint = maxPoint;
            }
            auditMgmtDTO.setPOINT(totalPoint);//합계 점수
        }



        return auditMgmtRepository.updateAuth(auditMgmtDTO);  // updateItem
    }
}
