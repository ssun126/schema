package com.dongwoo.SQM.partMgmt.service;

import com.dongwoo.SQM.partMgmt.dto.PartMgmtDTO;
import com.dongwoo.SQM.partMgmt.repository.PartMgmtRepository;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartMgmtService {
    private final PartMgmtRepository partMgmtRepository;

    public List<HashMap> getPlantList(){
        return partMgmtRepository.getPlantList();
    }

    public List<HashMap> getApprovalStatus(){
        return partMgmtRepository.getApprovalStatus();
    }

    public List<PartMgmtDTO> searchPartMgmt(PartMgmtDTO parmDTO) {

        return partMgmtRepository.searchPartMgmt(parmDTO);
    }

    public PartMgmtDTO getPartMgmtData(String idx) {
        return partMgmtRepository.getPartMgmtData(idx);
    }

    public List<HashMap> getpartCodeList(){ return partMgmtRepository.getpartCodeList();}

    public int save(PartMgmtDTO partMgmtDTO){
        return partMgmtRepository.save(partMgmtDTO);
    }
    public int updatePartMgmt(PartMgmtDTO partMgmtDTO) { return partMgmtRepository.updatePartMgmt(partMgmtDTO);}
}
