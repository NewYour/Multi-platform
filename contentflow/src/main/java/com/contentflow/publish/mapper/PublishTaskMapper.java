package com.contentflow.publish.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contentflow.publish.entity.PublishTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PublishTaskMapper extends BaseMapper<PublishTask> {

    @Select("SELECT * FROM publish_task WHERE task_id = #{taskId}")
    PublishTask selectByTaskId(@Param("taskId") String taskId);
}