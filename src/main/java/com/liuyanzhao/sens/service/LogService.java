package com.liuyanzhao.sens.service;

import com.liuyanzhao.sens.common.base.BaseService;
import com.liuyanzhao.sens.entity.Log;
import java.util.List;

/**
 * <pre>
 *     日志业务逻辑接口
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/19
 */
public interface LogService extends BaseService<Log, Long> {

    /**
     * 移除所有日志
     */
    void removeAllLog();

    /**
     * 查询最新的日志
     * @param limit
     * @return List
     */
    List<Log> findLatestLog(Integer limit);

    /**
     * 查询最新的日志
     * @param logTypes
     * @param limit
     * @return List
     */
    List<Log> findLatestLogByLogTypes(List<String> logTypes,Integer limit);
    /**
     * 查询最新的日志
     * @param username
     * @param limit
     * @return List
     */
    List<Log> findLatestLogByUsername(String username, Integer limit);

    /**
     * 获得今日新增数量
     * @return
     */
    Integer getTodayCount();

}
