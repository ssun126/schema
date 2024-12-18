package com.dongwoo.SQM.board.repository;

import com.dongwoo.SQM.board.dto.BoardDTO;
import com.dongwoo.SQM.board.dto.BoardFileDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepository {
    private final SqlSessionTemplate sql;

    public void save(BoardDTO boardDTO) {
        sql.insert("Board.save", boardDTO);
    }

    public List<BoardDTO> findAll() {
        return sql.selectList("Board.findAll");
    }

    public void updateHits(int id) {
        sql.update("Board.updateHits", id);
    }

    public BoardDTO findById(int id) {
        return sql.selectOne("Board.findById", id);
    }

    public void update(BoardDTO boardDTO) {
        sql.update("Board.update", boardDTO);
    }

    public void delete(int id) {
        sql.delete("Board.delete", id);
    }

    public void saveFile(BoardFileDTO boardFileDTO) {
        sql.insert("Board.saveFile", boardFileDTO);
    }

    public List<BoardFileDTO> findFile(int id) {
        return sql.selectList("Board.findFile", id);
    }

    public List<BoardDTO> getList(Criteria criteria) {
        return sql.selectList("Board.getList", criteria);
    }

    public int getTotal(Criteria criteria){
        return sql.selectOne("Board.getTotal" , criteria);
    }
}
