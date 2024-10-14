package com.dongwoo.SQM.common.service;

import com.dongwoo.SQM.common.dto.ExcelDTO;
import com.dongwoo.SQM.common.repository.ExcelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {
    private final ExcelRepository excelRepository;

    public int save(ExcelDTO excelDTO) {
        return excelRepository.save(excelDTO);
    }

    public List<ExcelDTO> findAll() {
        List<ExcelDTO> excelList = excelRepository.findAll();
        //전체 조회 후 리스트가 비어있다면, 예외를 던진다.
        if(CollectionUtils.isEmpty(excelList)) {
            log.error("조회된 학생이 없어서 예외 발생!");
            throw new IllegalStateException("조회된 학생 데이터가 없습니다. 확인 후 다시 진행해주시기 바랍니다.");
        }

        return excelList;
    }

}
