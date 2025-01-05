package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.AuditMgmtDTO;
import com.dongwoo.SQM.auditMgmt.dto.IsoAuthItemDTO;
import com.dongwoo.SQM.auditMgmt.repository.AuditMgmtRepository;
import com.dongwoo.SQM.auditMgmt.repository.IsoAuthRepository;
import com.dongwoo.SQM.common.service.FileStorageService;
import com.dongwoo.SQM.config.security.UserCustom;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
    private final AuditMgmtRepository auditMgmtRepository;

    @Value("${Upload.path.audit}")
    private String uploadPath;

    private final FileStorageService fileStorageService;


    public int saveAuth(AuditMgmtDTO isoAuthDTO) {
        return auditMgmtRepository.insertAuth(isoAuthDTO);
    }

    public int insertItem(IsoAuthItemDTO isoAuthItemDTO) {
        return isoAuthRepository.insertItem(isoAuthItemDTO);
    }

    public void saveIsoAuthData(String tableData, String type, MultipartFile[] fileNames) throws IOException {
        // JSON 문자열을 DTO 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        List<IsoAuthItemDTO> isoAuthItems = objectMapper.readValue(tableData, new TypeReference<List<IsoAuthItemDTO>>() {});

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String comCode = user.getCOM_CODE();
        int loginIdx = user.getUSER_IDX();

        //회사별 Audit 데이터 저장
        AuditMgmtDTO authDTO = new AuditMgmtDTO();
        authDTO.setCOM_CODE(comCode);
        authDTO.setAUTH_TYPE("ISO");
        authDTO.setAPPROVE_STATE(type); //제출 또는 저장
        authDTO.setSEND_USER_IDX(loginIdx);  //저장/제출자 저장
        authDTO.setREG_DW_USER_IDX(loginIdx);  // 생성자
        authDTO.setUP_DW_USER_IDX(loginIdx);  // 수정자

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
                String filePath = saveFile(fileNames[i]);

                // Paths 클래스를 사용하여 파일명 추출
                Path path = Paths.get(filePath);
                String fileName = path.getFileName().toString();  // 경로에서 파일명만 추출

                log.info("원본 파일명: " + fileNames[i].getOriginalFilename());
                log.info("DATA : " + isoAuthItems);
                for (IsoAuthItemDTO dto : isoAuthItems) {
                    if (dto.getFILE_NAME().equals(fileNames[i].getOriginalFilename())) {
                        dto.setFILE_NAME(fileName);  // 파일명 추가
                        dto.setFILE_PATH(filePath);  // 파일 경로 추가
                    }
                }
            }
        }

        //인증서 데이터 저장
        for (IsoAuthItemDTO dto : isoAuthItems) {
            dto.setCOM_CODE(comCode);
            dto.setITEM_STATE(type); //제출 또는 저장
            dto.setREG_DW_USER_IDX(loginIdx);
            dto.setUP_DW_USER_IDX(loginIdx);

            log.info(dto.getAUTH_DATE());
            if(!dto.getAUTH_DATE().isEmpty() && !dto.getEXP_DATE().isEmpty() && !dto.getREG_INPUT_DATE().isEmpty() && !dto.getFILE_NAME().isEmpty()) {
                Map<String, Object> params = new HashMap<>();
                params.put("AUTH_CODE", dto.getAUTH_CODE());
                params.put("COM_CODE", comCode);
                IsoAuthItemDTO ItemDTO = isoAuthRepository.findByIsoAuthItem(params);
                if(ItemDTO != null){
                    log.info(ItemDTO.getITEM_STATE());
                    log.info(dto.getITEM_STATE());
                    if(ItemDTO != dto) {
                        isoAuthRepository.updateItem(dto);  // updateItem
                    }
                }else {
                    isoAuthRepository.insertItem(dto);  // insert
                }
            }
        }

    }

    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("파일이 없습니다.");
        }

        // 저장할 파일의 경로 설정 (파일 이름에 타임스탬프를 추가하여 중복 방지)
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destinationFile  = new File(fileStorageService.getISOUploadDirectory() + File.separator +  fileName);
        file.transferTo(destinationFile );  // 파일 저장

        return destinationFile.getAbsolutePath();  // 저장된 파일 경로 반환
    }

    //업체별 인증 정보
    public AuditMgmtDTO getCompanyAuth(String type, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        return isoAuthRepository.getCompanyAuth(params);
    }

    // ISO 인증 업체 리스트- 조건을 처리하는 검색 메서드
    public List<IsoAuthItemDTO> searchCompanies(String type, String code, String name, String state) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_TYPE", type);
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("COM_STATUS", state);
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
    public List<IsoAuthItemDTO> getList(String code) {
        return isoAuthRepository.getList(code);
    }

    //업체별 ISO 인증서 만료일 정보 리스트
    public List<HashMap> getExpDateList(String code, String name, String expDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("EXP_DATE", expDate);
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
    public List<IsoAuthItemDTO> getIsoAuthItems(String com_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", com_id);
        return isoAuthRepository.getIsoAuthItems(params);
    }

    //업체별/메뉴별 전체 Auth 상태업데이트
    public int saveAuthResult(String com_code, String state, double totalPoint) {

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginIdx = user.getUSER_IDX();

        AuditMgmtDTO auditMgmtDTO = new AuditMgmtDTO();
        auditMgmtDTO.setCOM_CODE(com_code);
        auditMgmtDTO.setAUTH_TYPE("ISO");
        auditMgmtDTO.setAPPROVE_STATE(state);
        auditMgmtDTO.setREG_DW_USER_IDX(loginIdx);  // 생성자
        auditMgmtDTO.setUP_DW_USER_IDX(loginIdx);  // 수정자
        auditMgmtDTO.setPOINT(totalPoint);

        return isoAuthRepository.saveAuthResult(auditMgmtDTO);
    }

    //ISO 인증서별 상태업데이트
    public int updateStatus(String com_code, String auth_code, String reason, String state) {
        IsoAuthItemDTO isoAuthItemDTO = new IsoAuthItemDTO();
        isoAuthItemDTO.setCOM_CODE(com_code);
        isoAuthItemDTO.setAUTH_CODE(auth_code);
        isoAuthItemDTO.setITEM_STATE(state);
        isoAuthItemDTO.setREASON(reason);
        double  setPoint = 0;
        if(auth_code.equals("ISO 9001") ||auth_code.equals("IATF 16949") ){
            setPoint = 1;
        }else{
            setPoint = 0.5;
        }
        isoAuthItemDTO.setPOINT(setPoint); // 항목별 점수 고정됨. 조건 추가 필요

        UserCustom user = (UserCustom) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int loginIdx = user.getUSER_IDX();
        isoAuthItemDTO.setUP_DW_USER_IDX(loginIdx);

        //ISO Item 정보 가져오기
        List<IsoAuthItemDTO> getIsoItemsList = getIsoAuthItems(com_code);
        double totalPoint = 0;

        for (IsoAuthItemDTO item : getIsoItemsList) {
            totalPoint += item.getPOINT(); // 각 객체의 point 값을 더함
        }
        if(totalPoint > 4.0){ //ISO 인증은 최대 점수가 4점
            totalPoint = 4.0;
        }

        int rsltCnt = 0;
        try {
            log.info("isoAuthItemDTO??"+isoAuthItemDTO);
            rsltCnt = isoAuthRepository.updateStatus(isoAuthItemDTO);
            if(rsltCnt > 0 ){
                rsltCnt += saveAuthResult(com_code, state, totalPoint);
            }

            //승인이면  >> POVIS 전송
            if(state.equals("SEND")){
                log.info("POVIS에 ISO정보 제출");
            }
        }catch(Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return rsltCnt;//인증서에도 상태 정보 업데이트 (반려시 업데이트/승인시는 전체 승인만.. 업데이트 해야 함)
    }


}
