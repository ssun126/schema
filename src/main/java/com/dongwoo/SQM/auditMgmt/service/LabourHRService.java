package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.LabourHRDTO;
import com.dongwoo.SQM.auditMgmt.repository.AuditMgmtRepository;
import com.dongwoo.SQM.auditMgmt.repository.IsoAuthRepository;
import com.dongwoo.SQM.auditMgmt.repository.LabourHRRepository;
import com.dongwoo.SQM.config.security.UserCustom;
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
public class LabourHRService {
    private final LabourHRRepository labourHRRepository;
    private final AuditMgmtRepository auditMgmtRepository;
    private final IsoAuthRepository isoAuthRepository;

    @Value("${Upload.path.attach}")
    private String uploadPath;

    public int insertFileInfo(LabourHRDTO labourHRDTO) {
        return labourHRRepository.insertFileInfo(labourHRDTO);
    }

    public void saveAuthData(String type, MultipartFile fileNames) throws IOException {

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();
        int loginIdx = user.getUSER_IDX();


        //회사별 Audit 데이터 저장
        AuditMgmtDTO authDTO = new AuditMgmtDTO();
        authDTO.setCOM_CODE(comCode);
        authDTO.setAUTH_TYPE(type);
        authDTO.setREG_DW_USER_IDX(loginIdx);  // 파일 경로 추가
        authDTO.setUP_DW_USER_IDX(loginIdx);  // 파일 경로 추가
        authDTO.setAPPROVE_STATE("SEND"); //제출

        int comCnt = auditMgmtRepository.selectAuthCnt(authDTO);
        if(comCnt > 0) {
            int rsltCnt = auditMgmtRepository.updateAuth(authDTO);  // updateItem
        }else{
            int rsltCnt = auditMgmtRepository.insertAuth(authDTO); //저장
        }
        AuditMgmtDTO authMgmtDTO = auditMgmtRepository.selectAuth(authDTO);

        LabourHRDTO dto = new LabourHRDTO();
        log.info("fileNames.length??????"+fileNames);
        // 파일이 존재하면 처리
        if (fileNames != null && authMgmtDTO.getAUTH_SEQ() != null) {

            String filePath = saveFile(fileNames);

            // Paths 클래스를 사용하여 파일명 추출
            Path path = Paths.get(filePath);
            String fileName = path.getFileName().toString();  // 경로에서 파일명만 추출
            dto.setCOM_CODE(comCode);
            dto.setAUTH_TYPE("LABOUR");
            dto.setAUTH_SEQ(authMgmtDTO.getAUTH_SEQ());
            dto.setFILE_NAME(fileName);  // 파일명 추가
            dto.setFILE_PATH(filePath);  // 파일 경로 추가
        }
        int rtCnt = labourHRRepository.insertFileInfo(dto);  // insert

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
}
