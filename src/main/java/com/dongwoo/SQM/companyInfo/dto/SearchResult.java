package com.dongwoo.SQM.companyInfo.dto;

import com.dongwoo.SQM.board.dto.PageDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SearchResult {
    private List<CompanyInfoDTO> resultList;  // 검색된 항목들
    private PageDTO pageMaker;           // 페이지 네비게이션 정보

    // 기본 생성자
    public SearchResult() {}

    // 검색된 항목들과 페이지네이션 정보를 받는 생성자
    public SearchResult(List<CompanyInfoDTO> items, PageDTO pageMaker) {
        this.resultList = items;
        this.pageMaker = pageMaker;
    }
}

