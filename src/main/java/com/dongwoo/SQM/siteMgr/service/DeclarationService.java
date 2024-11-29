package com.dongwoo.SQM.siteMgr.service;

import com.dongwoo.SQM.siteMgr.dto.DeclarationDTO;
import com.dongwoo.SQM.siteMgr.repository.DeclarationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeclarationService
{
    private final DeclarationRepository declarationRepository;

    public List<DeclarationDTO> findAll(){
        return declarationRepository.findAll();
    }

    public void deleteAll(){
        declarationRepository.deleteAll();
    }

    public void insert_deClarationBulk(List<DeclarationDTO> declarationDTOList) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try{
            String sql = "INSERT INTO DECLARATION_DATA(DECL_IDX,DECL_NUM,DECL_SUB_NUM,DECL_NAME,DECL_CASNUM,DECL_WEIGHT,DECL_CLASS,DECL_GROUND) VALUES (SEQ_DECL_DATA.NEXTVAL,?,?,?,?,?,?,?) ";
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@175.126.38.80:1521/ORCL","c##NSQM","12345");
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);

            int count = 0;

            for(DeclarationDTO dto: declarationDTOList){
                count++;

                pstmt.setString(1, dto.getDECL_NUM());
                pstmt.setString(2, dto.getDECL_SUB_NUM());
                pstmt.setString(3, dto.getDECL_NAME());
                pstmt.setString(4, dto.getDECL_CASNUM());
                pstmt.setString(5, dto.getDECL_WEIGHT());
                pstmt.setString(6, dto.getDECL_CLASS());
                pstmt.setString(7, dto.getDECL_GROUND());

                pstmt.addBatch();
                pstmt.clearParameters();

                if((count%100)==0){
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                    conn.commit();
                }

            }

            pstmt.executeBatch();
            conn.commit();

        } catch(Exception e){
            log.info(e.toString());
            conn.rollback();
        }finally {
            if(pstmt != null) pstmt.close();
            if(conn != null) conn.close();
        }
    }

}
