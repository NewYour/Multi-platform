package com.contentflow.publish.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contentflow.publish.entity.PublishRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PublishRecordMapper extends BaseMapper<PublishRecord> {

    @Select("SELECT * FROM publish_record WHERE task_id = #{taskId}")
    List<PublishRecord> selectByTaskId(@Param("taskId") String taskId);
}