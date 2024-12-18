package com.dongwoo.SQM.board.service;

import com.dongwoo.SQM.board.dto.BoardDTO;
import com.dongwoo.SQM.board.dto.BoardFileDTO;
import com.dongwoo.SQM.board.dto.Criteria;
import com.dongwoo.SQM.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public void save(BoardDTO boardDTO) {
        boardRepository.save(boardDTO);
    }

    public List<BoardDTO> findAll() {
        return boardRepository.findAll();
    }

    public void updateHits(int id) {
        boardRepository.updateHits(id);
    }

    public BoardDTO findById(int id) {
        return boardRepository.findById(id);
    }

    public void update(BoardDTO boardDTO) {
        boardRepository.update(boardDTO);
    }

    public void delete(int id) {
        boardRepository.delete(id);
    }

    public List<BoardFileDTO> findFile(int id) {
        return boardRepository.findFile(id);
    }

    public List<BoardDTO> getList(Criteria cri) {
        return boardRepository.getList(cri);
    }

    public int getTotal(Criteria cri) {
        return boardRepository.getTotal(cri);
    }

}
