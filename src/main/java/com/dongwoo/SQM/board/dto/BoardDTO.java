package com.dongwoo.SQM.board.dto;

import com.dongwoo.SQM.board.repository.BoardRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class BoardDTO {
    @JsonProperty("BOARD_TYPE")
    private String BOARD_TYPE;
    @JsonProperty("BOARD_IDX")
    private int BOARD_IDX;
    @JsonProperty("INPUT_USER_ID")
    private String INPUT_USER_ID;
    @JsonProperty("MODIFY_USER_ID")
    private String MODIFY_USER_ID;
    @JsonProperty("BOARD_PASS")
    private String BOARD_PASS;
    @JsonProperty("BOARD_TITLE")
    private String BOARD_TITLE;
    @JsonProperty("BOARD_DESC")
    private String BOARD_DESC;
    @JsonProperty("BOARD_HITS")
    private int BOARD_HITS;
    @JsonProperty("INPUT_DATE")
    private String INPUT_DATE;
    @JsonProperty("MODIFY_DATE")
    private String MODIFY_DATE;
    @JsonProperty("FILE_NAME")
    private String FILE_NAME; // 파일 이름 full
    @JsonProperty("FILE_SHOTNAME")
    private String FILE_SHOTNAME; // 파일 이름 이름만.
    @JsonProperty("FILE_PATH")
    private String FILE_PATH; // 파일이 저장된 경로

    @JsonProperty("keyword")
    private String keyword;
    @JsonProperty("type")
    private String type;

    @JsonProperty("FIXED_YN")
    private String FIXED_YN;

    @JsonProperty("USER_IDX")
    private int USER_IDX;


    /*public List<BoardDTO> getList(Criteria criteria){
        return BoardRepository.getList(criteria);
    }*/
}
