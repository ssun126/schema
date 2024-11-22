package com.dongwoo.SQM.auditMgmt.dto;

import com.dongwoo.SQM.board.dto.PageDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class AuditSearchResult {
    private List<AuditMgmtDTO> resultList;  // 검색된 항목들
    private PageDTO pageMaker;           // 페이지 네비게이션 정보
    // 기본 생성자
    public AuditSearchResult() {}

    // 검색된 항목들과 페이지네이션 정보를 받는 생성자
    public AuditSearchResult(List<AuditMgmtDTO> items, PageDTO pageMaker) {
        this.resultList = items;
        this.pageMaker = pageMaker;
    }
}
