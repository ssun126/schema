package com.dongwoo.SQM.qualityCtrl.service;



import com.dongwoo.SQM.qualityCtrl.dto.coaMgmtDTO;
import com.dongwoo.SQM.qualityCtrl.repository.coaMgmtRepository;
import com.dongwoo.SQM.siteMgr.dto.BaseCodeDTO;
import com.dongwoo.SQM.system.dto.ComPanyCodeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class coaMgmtService {
    private final coaMgmtRepository coaMgmtRepository;

    public List<BaseCodeDTO> GetBaseCode(String group_code) {
        return coaMgmtRepository.GetBaseCode(group_code);
    }

    public List<BaseCodeDTO> GetBaseCodePLANT(String group_code) {
        return coaMgmtRepository.GetBaseCodePLANT(group_code);
    }
    public List<HashMap> getUserList(String code, String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("USER_ID", code);
        params.put("USER_NAME", name);
        return coaMgmtRepository.getUserList(params);
    }


    public List<coaMgmtDTO> getCOAList(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.getCOAList(coaMgmtDTO);
    }

    public coaMgmtDTO getCOADetailTitle(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.getCOADetailTitle(coaMgmtDTO);
    }

    public String getCOANumber() {
        return coaMgmtRepository.getCOANumber();
    }


    public List<coaMgmtDTO> getCOADetailSpec(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.getCOADetailSpec(coaMgmtDTO);
    }

    public int updateVendorComment(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.updateVendorComment(coaMgmtDTO);
    }


    public int copyCOAMaster(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.copyCOAMaster(coaMgmtDTO);
    }

    public int copyCOADetail(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.copyCOADetail(coaMgmtDTO);
    }

}
