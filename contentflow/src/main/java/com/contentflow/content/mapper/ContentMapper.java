package com.contentflow.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contentflow.content.entity.Content;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContentMapper extends BaseMapper<Content> {
}