package com.dongwoo.SQM.adminPartMgmt.service;

import com.dongwoo.SQM.adminPartMgmt.dto.AdminPartMgmtDTO;
import com.dongwoo.SQM.adminPartMgmt.repository.AdminPartMgmtRepository;
import com.dongwoo.SQM.partMgmt.dto.PartMgmtDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPartMgmtService {

    private final AdminPartMgmtRepository adminPartMgmtRepository;

    public List<AdminPartMgmtDTO> searchAdminPartMgmt(AdminPartMgmtDTO parmDTO) {

        return adminPartMgmtRepository.searchAdminPartMgmt(parmDTO);
    }


    public List<HashMap> getPartMSDSExpList(String EXP_DATE, String COM_CODE, String COM_NAME) {
        return adminPartMgmtRepository.getPartMSDSExpList(EXP_DATE, COM_CODE, COM_NAME);
    }
}
