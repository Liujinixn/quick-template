package com.quick.common.utils.collection;

import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Collections工具集.
 * 在JDK的Collections和Guava的Collections2后, 命名为Collections3.
 * 函数主要由两部分组成，一是自反射提取元素的功能，二是源自Apache Commons Collection, 争取不用在项目里引入它。
 *
 * @author Liujinxin
 */
public class CollectionsUtil {

    /**
     * 转换Collection所有元素(通过toString())为String, 中间以 separator分隔。
     */
    public static String convertToString(final Collection<?> collection, final String separator) {
        return StringUtils.join(collection, separator);
    }

    /**
     * 判断是否为空.
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null) || collection.isEmpty();
    }

    /**
     * 判断是否为空.
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null) || map.isEmpty();
    }

    /**
     * 判断是否为空.
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return (collection != null) && !(collection.isEmpty());
    }

    /**
     * 取得Collection的第一个元素，如果collection为空返回null.
     */
    public static <T> T getFirst(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }

        return collection.iterator().next();
    }

}
