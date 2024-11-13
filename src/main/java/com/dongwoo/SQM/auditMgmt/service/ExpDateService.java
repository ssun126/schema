package com.dongwoo.SQM.auditMgmt.service;

import com.dongwoo.SQM.auditMgmt.dto.ExpDateDTO;
import com.dongwoo.SQM.auditMgmt.dto.ExpDateDTO;
import com.dongwoo.SQM.auditMgmt.repository.ExpDateRepository;
import com.dongwoo.SQM.board.dto.Criteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: shhan
 */
@Service
@RequiredArgsConstructor
public class ExpDateService {
    private final ExpDateRepository expDateRepository;

    public int save(ExpDateDTO expDateItemDTO) {
        return expDateRepository.save(expDateItemDTO);
    }

    //업체별 만료일 리스트- 조건을 처리하는 검색 메서드
    public List<ExpDateDTO> isoSearchComList(String code, String name, String expDate, Criteria criteria) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("EXP_DATE", expDate);
        params.put("criteria", criteria);
        return expDateRepository.isoSearchComList(params);
    }

    //업체별 만료일 리스트-검색 조건에 맞는 총 개수를 반환
    public int getTotalByKeyword(String code, String name, String state) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", code);
        params.put("COM_NAME", name);
        params.put("COM_STATUS", state);
        return expDateRepository.countByKeyword(params);
    }

    //업체별 만료일 정보 리스트
    public List<ExpDateDTO> getList(Criteria cri) {
        return expDateRepository.getList(cri);
    }
    public int getTotal() {
        return expDateRepository.getTotal();
    }

    //업체별 만료일 정보-상세보기
    public ExpDateDTO findByExpDateItem(String auth_id,String com_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("AUTH_CODE", auth_id);
        params.put("COM_CODE", com_id);
        return expDateRepository.findByExpDate(params);
    }

    //업체별 만료일 리스트-상세보기
    public List<ExpDateDTO> findByCompanyId(String com_id) {
        Map<String, Object> params = new HashMap<>();
        params.put("COM_CODE", com_id);
        return expDateRepository.findByCompanyId(params);
    }
}
