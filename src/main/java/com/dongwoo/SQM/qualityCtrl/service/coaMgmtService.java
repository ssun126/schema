package com.dongwoo.SQM.qualityCtrl.service;



import com.dongwoo.SQM.qualityCtrl.dto.coaMgmtDTO;
import com.dongwoo.SQM.qualityCtrl.repository.coaMgmtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class coaMgmtService {
    private final coaMgmtRepository coaMgmtRepository;

    public List<coaMgmtDTO> getCOAList(coaMgmtDTO coaMgmtDTO) {
        return coaMgmtRepository.getCOAList(coaMgmtDTO);
    }


}
