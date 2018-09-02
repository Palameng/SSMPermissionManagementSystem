package com.mengyuan.util;

import org.apache.commons.lang3.StringUtils;

public class LevelUtil {
    public final static String SEPARATOR = ".";
    public final static String ROOT = "0";

    /**
     * 部门level层级计算规则
     * @param parentLevel 父部门level
     * @param parentId 父部门id
     * @return string
     */
    public static String calculateLevel(String parentLevel, int parentId) {
        if (StringUtils.isBlank(parentLevel)) {
            return ROOT;
        } else {
            return StringUtils.join(parentLevel, SEPARATOR, parentId);
        }

    }
}
