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

    public List<HashMap> getMaterialListCoa(String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        return coaMgmtRepository.getMaterialListCoa(params);
    }

    public List<HashMap> getMaterialFactoryList(String vendorId , String materialId  , String selLang) {
        Map<String, Object> params = new HashMap<>();
        params.put("vendorId", vendorId);
        params.put("materialId", materialId);
        params.put("TOKEN_USER_LANG", selLang);
        return coaMgmtRepository.getMaterialFactoryList(params);
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


    public int delCOAMaster(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.delCOAMaster(coaMgmtDTO);
    }

    public int delCOADetail(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.delCOADetail(coaMgmtDTO);
    }

    public coaMgmtDTO regCheck(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.regCheck(coaMgmtDTO);
    }

    public coaMgmtDTO regSpecCheck(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.regSpecCheck(coaMgmtDTO);
    }


    public String getStatusCOAMasterByPK(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.getStatusCOAMasterByPK(coaMgmtDTO);
    }

    public coaMgmtDTO getMaterial(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.getMaterial(coaMgmtDTO);
    }

    public coaMgmtDTO interfaceLimsCOAMasterData(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.interfaceLimsCOAMasterData(coaMgmtDTO);
    }

    public List<coaMgmtDTO> interfaceLimsCOADetailData(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.interfaceLimsCOADetailData(coaMgmtDTO);
    }



    public int interfaceCOAMaster(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.interfaceCOAMaster(coaMgmtDTO);
    }

    public int interfaceCOADetail(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.interfaceCOADetail(coaMgmtDTO);
    }


    public coaMgmtDTO interfaceDqmsCOAMasterData(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.interfaceDqmsCOAMasterData(coaMgmtDTO);
    }

    public List<coaMgmtDTO> interfaceDqmsCOADetailData(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.interfaceDqmsCOADetailData(coaMgmtDTO);
    }

    public int interfaceCOAProcedure(String DB_LINK_TARGET_APPLY) {
        return coaMgmtRepository.interfaceCOAProcedure(DB_LINK_TARGET_APPLY);
    }

    public int insertCOAListDqms(List<coaMgmtDTO> masterList , List<coaMgmtDTO> detailList ) {
        //테이블 또는 뷰가 존재하지 않습니다
        int testSeq = 0 ;
        for (int i = 0 ; i < masterList.size() ; i ++) {
          //  coaMgmtRepository.interfaceDqmsCOAMaster(masterList.get(i));
            testSeq++;

        }
        //테이블 또는 뷰가 존재하지 않습니다
        for (int i = 0 ; i < detailList.size() ; i ++) {
            //coaMgmtRepository.interfaceDqmsCOADetail(detailList.get(i));

            testSeq++;
        }

        return  testSeq ;
    }


    //메일 발송자 가져오기
    public List<HashMap> getEmailTargetUser(Map<String, Object> params) {
        return coaMgmtRepository.getEmailTargetUser(params);
    }


    public int updateCOAStatus(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.updateCOAStatus(coaMgmtDTO);
    }

}
