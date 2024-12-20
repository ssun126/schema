package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.AuditItemPointDTO;
import com.dongwoo.SQM.auditMgmt.repository.AuditMgmtRepository;
import com.dongwoo.SQM.config.security.UserCustom;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        authDTO.setPOINT(total);  // 점수
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
        log.info(" total:::::::::"+  total);

        try {
            // 파일이 있는 경우 파일 정보 저장
            if (fileNames != null && fileNames.length > 0) {
                // 각 파일을 저장하고 경로를 DTO에 추가
                for (int i = 0; i < fileNames.length; i++) {
                    String filePath = saveFile(fileNames[i]);

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

    //업체별 인증 파일 정보
    public List<AuditMgmtDTO> getCompanyAuthFile(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return auditMgmtRepository.getCompanyAuthFile(params);
    }

    //업체별 만료일 정보 리스트
    public List<HashMap> getExpDateList(String code, String name, String type) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("AUTH_TYPE", type);
        return auditMgmtRepository.getExpDateList(params);
    }

    public  Map<String, String> getUserInfo(String code, String name, String type) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("AUTH_TYPE", type);
        return auditMgmtRepository.getUserInfo(params);
    }

}
