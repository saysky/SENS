package com.liuyanzhao.sens.utils;

import com.liuyanzhao.sens.entity.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *     拼装分类目录，
 * </pre>
 *
 * @author : saysky
 * @date : 2018/7/12
 */
public class CategoryUtil {

    /**
     * 获取组装好的分类,
     *
     * @param categoriesRoot categoriesRoot
     * @return List
     */
    public static List<Category> getCategoryList(List<Category> categoriesRoot) {
        List<Category> categoriesResult = new ArrayList<>();

        for (Category category : categoriesRoot) {
            if (category.getCatePid() == 0) {
                categoriesResult.add(category);
                categoriesResult.addAll(getChildList(category.getId(), categoriesRoot));
            }
        }
        return categoriesResult;
    }

    /**
     * 获取分类的子分类
     *
     * @param id        分类编号
     * @param categoriesRoot categoriesRoot
     * @return List
     */
    private static List<Category> getChildList(Long id, List<Category> categoriesRoot) {
        List<Category> categoriesChild = new ArrayList<>();
        for (Category category : categoriesRoot) {
            if (category.getCatePid() != 0) {
                if (category.getCatePid().equals(id)) {
                    categoriesChild.add(category);
                    List<Category> tempList = getChildList(category.getId(), categoriesRoot);
                    tempList.sort((a, b) -> b.getCateSort() - a.getCateSort());
                    categoriesChild.addAll(tempList);
                }
            }
        }
        if (categoriesChild.size() == 0) {
            return Collections.emptyList();
        }
        return categoriesChild;
    }

}
