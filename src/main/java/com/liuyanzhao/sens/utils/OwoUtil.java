package com.liuyanzhao.sens.utils;

import com.liuyanzhao.sens.model.dto.SensConst;
import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 *     OwO表情工具类
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/22
 */
@Slf4j
public class OwoUtil {

    /**
     * 将表情标志转化为图片地址
     *
     * @param mark 表情标志
     * @return 表情图片地址
     */
    public static String markToImg(String mark) {
        for (String key : SensConst.OWO.keySet()) {
            mark = mark.replace(key, SensConst.OWO.get(key));
        }
        return mark;
    }
}
