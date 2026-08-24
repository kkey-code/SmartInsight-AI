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
                create_time,
                update_time
            )
            VALUES
            (
                ?,
                ?,
                NOW(),
                NOW()
            )
            ON DUPLICATE KEY UPDATE
                content = VALUES(content),
                update_time = NOW()
            """;

        jdbcTemplate.update(
                sql,
                documentId,
                content
        );

    }

}