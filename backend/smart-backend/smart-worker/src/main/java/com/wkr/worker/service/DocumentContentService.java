package com.wkr.worker.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class DocumentContentService {


    private final JdbcTemplate jdbcTemplate;

    public DocumentContentService(
            JdbcTemplate jdbcTemplate
    ){
        this.jdbcTemplate = jdbcTemplate;
    }


    public void save(
            Long documentId,
            String content
    ){

        String sql = """
                INSERT INTO document_content
                (
                    document_id,
                    content,
                    create_time
                )
                VALUES
                (
                    ?,
                    ?,
                    NOW()
                )
                """;

        jdbcTemplate.update(
                sql,
                documentId,
                content
        );

    }

}