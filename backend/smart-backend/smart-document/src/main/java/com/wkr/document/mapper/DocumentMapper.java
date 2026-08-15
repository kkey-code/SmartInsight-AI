package com.wkr.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wkr.document.entity.DocumentInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocumentMapper extends BaseMapper<DocumentInfo> {
}
