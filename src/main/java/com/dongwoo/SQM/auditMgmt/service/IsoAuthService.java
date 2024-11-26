package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.repository.IsoAuthRepository;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.config.security.UserCustom;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class IsoAuthService {
    private final IsoAuthRepository isoAuthRepository;

    public int saveAuth(AuditMgmtDTO isoAuthDTO) {
        return isoAuthRepository.saveAuth(isoAuthDTO);
    }

    public int saveItems(IsoAuthItemDTO isoAuthItemDTO) {
        return isoAuthRepository.saveItems(isoAuthItemDTO);
    }
    @Value("${Upload.path.attach}")
    private String uploadPath;

    public void saveIsoAuthData(String tableData, MultipartFile[] fileNames, UserCustom user) throws IOException {
        // JSON 문자열을 DTO 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        List<IsoAuthItemDTO> isoAuthItems = objectMapper.readValue(tableData, new TypeReference<List<IsoAuthItemDTO>>() {});

        // 파일이 존재하면 처리
        if (fileNames != null && fileNames.length > 0) {
            if (fileNames.length != isoAuthItems.size()) {
                throw new IOException("파일의 개수와 데이터의 개수가 일치하지 않습니다.");
            }

            // 각 파일을 저장하고 경로를 DTO에 추가
            for (int i = 0; i < fileNames.length; i++) {
                String filePath = saveFile(fileNames[i]);

                // Paths 클래스를 사용하여 파일명 추출
                Path path = Paths.get(filePath);
                String fileName = path.getFileName().toString();  // 경로에서 파일명만 추출

                log.info("파일명: " + fileName);
                isoAuthItems.get(i).setFILE_NAME(fileName);  // 파일명 추가
                isoAuthItems.get(i).setFILE_PATH(filePath);  // 파일 경로 추가
            }
        }
        String comCode = user.getCOM_CODE();
        int loginIdx = user.getUSER_IDX();
        //인증서 데이터 저장
        for (IsoAuthItemDTO dto : isoAuthItems) {
            dto.setCOM_CODE(comCode);
            dto.setREG_DW_USER_IDX(loginIdx);
            dto.setUP_DW_USER_IDX(loginIdx);
            isoAuthRepository.saveItems(dto);  // 데이터베이스에 저장
        }

        //회사별 Audit 데이터 저장
        AuditMgmtDTO isoAuthDTO = new AuditMgmtDTO();
        isoAuthDTO.setAUTH_TYPE("ISO");
        isoAuthDTO.setAPPROVE_STATE("SEND"); //제출
        isoAuthDTO.setCOM_CODE(comCode);
        isoAuthDTO.setREG_DW_USER_IDX(loginIdx);
        isoAuthDTO.setUP_DW_USER_IDX(loginIdx);
        saveAuth(isoAuthDTO); //저장

        // 데이터를 POVIS에 전송
    }

    private String saveFile(MultipartFile file) throws IOException {
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
        return isoAuthRepository.getCompanyAuth(params);
    }

    // ISO 인증 업체 리스트- 조건을 처리하는 검색 메서드
    public List<AuditMgmtDTO> searchCompanies(String type, String code, String name, String state, Criteria criteria) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("COM_STATUS", state);
        params.put("criteria", criteria);
        return isoAuthRepository.searchCompanies(params);
    }

    // ISO 인증 업체 리스트-검색 조건에 맞는 총 개수를 반환
    public int getTotalByKeyword(String type, String code, String name, String state) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("COM_STATUS", state);
        return isoAuthRepository.countByKeyword(params);
    }

    //업체별 ISO 인증서 정보 리스트
    public List<IsoAuthItemDTO> getList(Criteria criteria, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("criteria", criteria);
        return isoAuthRepository.getList(params);
    }

    //업체별 ISO 인증서 만료일 정보 리스트
    public List<IsoAuthItemDTO> getExpDateList(String code, String name, String expDate, Criteria criteria) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("EXP_DATE", expDate);
        params.put("criteria", criteria);
        return isoAuthRepository.getExpDateList(params);
    }
    public int getTotal() {
        return isoAuthRepository.getTotal();
    }

    //업체별 ISO 인증서 정보-상세보기
    public IsoAuthItemDTO findByIsoAuthItem(String auth_id,String com_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_CODE", auth_id);
        params.put("COM_CODE", com_id);
        return isoAuthRepository.findByIsoAuthItem(params);
    }

    //업체별 ISO 인증서 리스트-상세보기
    public List<IsoAuthItemDTO> findByCompanyId(String com_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", com_id);
        return isoAuthRepository.findByCompanyId(params);
    }

    //상태업데이트
    public void updateStatus(IsoAuthItemDTO isoAuthItemDTO) {
        isoAuthRepository.updateStatus(isoAuthItemDTO);
    }

}
