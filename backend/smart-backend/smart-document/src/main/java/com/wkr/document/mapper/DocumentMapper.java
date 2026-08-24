package com.wkr.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wkr.document.entity.Document;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocumentMapper extends BaseMapper<Document> {

    int updateProcessSuccess(
            Long id,
            String taskId
    );

    int updateProcessFailed(
            Long id,
            String taskId,
            String errorMessage
    );
}