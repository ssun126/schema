package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.AuditItemPointDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.ConflictMineralsDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.repository.AuditMgmtRepository;
import com.dongwoo.SQM.auditMgmt.repository.ConflictMineralsRepository;
import com.dongwoo.SQM.companyInfo.service.CompanyInfoService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
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
public class ConflictMineralsService {
    private final AuditMgmtRepository auditMgmtRepository;
    private final ConflictMineralsRepository conflictMineralsRepository;
    private final CompanyInfoService companyInfoService;

    @Value("${Upload.path.audit}")
    private String uploadPath;

    public void saveAuthData(String tableData, String warranty, String modify, String type, MultipartFile[] fileNames) throws IOException {
        // JSON 문자열을 DTO 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        List<ConflictMineralsDTO> conflictMinerals = objectMapper.readValue(tableData, new TypeReference<List<ConflictMineralsDTO>>() {});

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();
        int loginIdx = user.getUSER_IDX();

        //회사별 Audit 데이터 저장
        AuditMgmtDTO authDTO = new AuditMgmtDTO();
        authDTO.setCOM_CODE(comCode);
        authDTO.setAUTH_TYPE("CONFLICT");
        authDTO.setAPPROVE_STATE("SEND"); //제출
        authDTO.setSEND_USER_IDX(loginIdx); //제출자
        authDTO.setREG_DW_USER_IDX(loginIdx);
        authDTO.setUP_DW_USER_IDX(loginIdx);
        log.info("authDTO::::::::::"+authDTO);

        int comCnt = auditMgmtRepository.selectAuthCnt(authDTO);
        if(comCnt > 0) {
            int rsltCnt = auditMgmtRepository.updateAuth(authDTO);  // updateItem
        }else{
            int rsltCnt = auditMgmtRepository.insertAuth(authDTO); //저장
        }

        AuditMgmtDTO authMgmtDTO = auditMgmtRepository.selectAuth(authDTO);
        // 파일이 존재하면 처리
        if (fileNames != null && fileNames.length > 0 && authMgmtDTO != null) {

            // 각 파일을 저장하고 경로를 DTO에 추가
            for(int i = 0; i < fileNames.length; i++) {
                saveUploadData(fileNames[i], authMgmtDTO.getAUTH_SEQ(), type);//파일 내용 저장

                String filePath = saveFile(fileNames[i]);

                // Paths 클래스를 사용하여 파일명 추출
                Path path = Paths.get(filePath);
                String fileName = path.getFileName().toString();  // 경로에서 파일명만 추출

                log.info("원본 파일명: " + fileNames[i].getOriginalFilename());
                log.info("data: " + conflictMinerals);
                for (ConflictMineralsDTO dto : conflictMinerals) {
                    log.info("파일명: " + dto.getFILE_NAME());
                    if (dto.getFILE_NAME().equals(fileNames[i].getOriginalFilename())) {
                        dto.setCOM_CODE(comCode);
                        dto.setAUTH_TYPE("CONFLICT");
                        dto.setAUTH_SEQ(authMgmtDTO.getAUTH_SEQ());
                        dto.setFILE_NAME(fileName);  // 파일명 추가
                        dto.setFILE_PATH(filePath);  // 파일 경로 추가

                        log.info("dto: " + dto+"////dto: " + dto.getFILE_TYPE());
                        int rtCnt = conflictMineralsRepository.insertFileInfo(dto);  // insert file
                        log.info("파일저장 Count: " + rtCnt);
                    }
                }
            }
        }

        // 데이터 저장
        if(modify.equals("Y") && authMgmtDTO != null) {
            for (ConflictMineralsDTO dto : conflictMinerals) {
                dto.setCOM_CODE(comCode);
                dto.setAUTH_SEQ(authMgmtDTO.getAUTH_SEQ());
                //기존 정보가 있는지 확인
                if (dto.getPART_CODE() != null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("PART_CODE", dto.getPART_CODE());
                    params.put("COM_CODE", comCode);
                    ConflictMineralsDTO ItemDTO = conflictMineralsRepository.findByPartItem(params);
                    if (ItemDTO != null) {
                        if (ItemDTO != dto) {
                            conflictMineralsRepository.updateItem(dto);  // updateItem
                        }
                    } else {
                        conflictMineralsRepository.insertItem(dto);  // insert
                    }
                }
            }
        }
        log.info("회사별 Audit 데이터 저장 완료");

    }

    @Transactional
    public void saveUploadData(@RequestParam(value="file") MultipartFile file, int auth_seq, String AUTH_TYPE) throws IOException {
        log.info("saveLabourUploadData====================="+auth_seq);
        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int comUserIdx = user.getCOM_USER_IDX();
        String comCode = user.getCOM_CODE();

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet4 = workbook.getSheetAt(3); // 4번째 시트는 0-based index이므로 3번째

        // 병합된 셀 찾기 (32번째 행부터 4개의 행) >> CMRT이면 4개, EMRT이면 2개
        int mergedRegions = sheet4.getNumMergedRegions();
        for (int i = 0; i < mergedRegions; i++) {
            CellRangeAddress mergedRegion = sheet4.getMergedRegion(i);

            // 병합된 셀이 32번째 행부터 시작하는지 확인
            if (mergedRegion.getFirstRow() >= 31 && mergedRegion.getLastRow() < 35) {
                // 병합된 셀이 D, E열에 걸쳐 있는지 확인
                if (mergedRegion.getFirstColumn() == 3 && mergedRegion.getLastColumn() == 4) {
                    // 병합된 셀의 첫 번째 셀에서 값 가져오기
                    int firstRow = mergedRegion.getFirstRow();
                    int firstCol = mergedRegion.getFirstColumn();
                    Row row = sheet4.getRow(firstRow);
                    Cell cell = row.getCell(firstCol);

                    // 병합된 셀의 값 확인
                    if (cell != null && cell.toString().equals("YES")) {
                        System.out.println("병합된 셀 값이 'YES'입니다. 이제 7번째 시트의 데이터를 가져옵니다.");

                        // 7번째 시트 가져오기
                        XSSFSheet sheet7 = workbook.getSheetAt(6); // 7번째 시트는 0-based index이므로 6번째

                        // 6번째 행(0-based index로 5번째)부터 시작
                        for (int j = 5; j < sheet7.getPhysicalNumberOfRows(); j++) {
                            Row row7 = sheet7.getRow(j);
                            if (row7 == null) {
                                continue; // 빈 행이면 건너뜀
                            }

                            // B열(2번째 열) 데이터 읽기
                            Cell cellB = row7.getCell(1); // B열은 1번 인덱스
                            if (cellB != null) {
                                // B열의 값 출력
                                System.out.println("7번째 시트의 " + (j + 1) + "번째 행 B열 데이터: " + cellB.toString());
                            }
                        }
                    }
                }
            }
        }

        // 엑셀 파일을 닫기
        workbook.close();

       /* try {
            for (ConflictMineralsDTO dto : conflictMinerals) {
                dto.setCOM_CODE(comCode);
                dto.setAUTH_SEQ(authMgmtDTO.getAUTH_SEQ());
                //기존 정보가 있는지 확인
                if (dto.getPART_CODE() != null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("PART_CODE", dto.getPART_CODE());
                    params.put("COM_CODE", comCode);
                    ConflictMineralsDTO ItemDTO = conflictMineralsRepository.findByPartItem(params);
                    if (ItemDTO != null) {
                        if (ItemDTO != dto) {
                            conflictMineralsRepository.updateItem(dto);  // updateItem
                        }
                    } else {
                        conflictMineralsRepository.insertItem(dto);  // insert
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/
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

    //업체별 인증서 정보
    public AuditMgmtDTO getCompanyAuth(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return auditMgmtRepository.getCompanyAuth(params);
    }

    //업체별 분쟁광물 정보 가져오기
    public List<ConflictMineralsDTO> getConflictData(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return conflictMineralsRepository.getConflictData(params);
    }
    public List<ConflictMineralsDTO> getConflictData(String type, String code, String AUTH_SEQ) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_SEQ", AUTH_SEQ);
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return conflictMineralsRepository.getConflictData(params);
    }

}
