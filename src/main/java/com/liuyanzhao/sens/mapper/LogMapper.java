package com.liuyanzhao.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.entity.Log;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 日志Mapper
 * @author liuyanzhao
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {

    /**
     * 查询最新的数据
     *
     * @param limit
     * @return List
     */
    List<Log> findLatestLog(Integer limit);

    /**
     * 查询最新的数据
     *
     * @param limit
     * @return List
     */
    List<Log> findLatestLogByLogTypes(@Param("logTypes") List<String> logTypes,
                                      @Param("limit") Integer limit);

    /**
     * 根据用户ID获得最新日志
     *
     * @param limit
     * @param username
     * @return List
     */
    List<Log> findLatestLogByUsername(String username, Integer limit);
    /**
     * 删除所有的记录
     * @return 影响行数
     */
    Integer deleteAll();

    /**
     * 获得今日新增数量
     * @return
     */
    Integer getTodayCount();
}

