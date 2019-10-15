package com.liuyanzhao.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuyanzhao.sens.entity.Options;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liuyanzhao
 */
@Mapper
public interface OptionsMapper extends BaseMapper<Options> {

    /**
     * 根据key查询单个option
     *
     * @param key key
     * @return Options
     */
    Options findOptionsByOptionName(String key);

}

