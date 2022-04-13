package com.quick.common.utils.validator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.security.spec.InvalidParameterSpecException;
import java.util.*;

/**
 * 对象属性检查
 * 注解	适用的数据类型	说明
 *
 * @AssertFalse Boolean, boolean	验证注解的元素值是false
 * @AssertTrue Boolean, boolean	验证注解的元素值是true
 * @DecimalMax（value=x） BigDecimal，BigInteger，String，byte，short，int，long和原始类型的相应包装。HV额外支持：Number和CharSequence的任何子类型。	验证注解的元素值小于等于@ DecimalMax指定的value值
 * @DecimalMin（value=x） BigDecimal，BigInteger，String，byte，short，int，long和原始类型的相应包装。HV额外支持：Number和CharSequence的任何子类型。	验证注解的元素值大于等于@ DecimalMin指定的value值
 * @Digits(integer=整数位数, fraction=小数位数)	BigDecimal，BigInteger，String，byte，short，int，long和原始类型的相应包装。HV额外支持：Number和CharSequence的任何子类型。	验证注解的元素值的整数位数和小数位数上限
 * @Future java.util.Date，java.util.Calendar; 如果类路径上有Joda Time日期/时间API ，则由HV附加支持：ReadablePartial和ReadableInstant的任何实现。
 * 验证注解的元素值（日期类型）比当前时间晚
 * @Max（value=x） BigDecimal，BigInteger，byte，short，int，long和原始类型的相应包装。HV额外支持：CharSequence的任何子类型（评估字符序列表示的数字值），Number的任何子类型。	验证注解的元素值小于等于@Max指定的value值
 * @Min（value=x） BigDecimal，BigInteger，byte，short，int，long和原始类型的相应包装。HV额外支持：CharSequence的任何子类型（评估char序列表示的数值），Number的任何子类型。	验证注解的元素值大于等于@Min指定的value值
 * @NotNull 任意种类    验证注解的元素值不是null
 * @Null 任意种类    验证注解的元素值是null
 * @Past java.util.Date，java.util.Calendar; 如果类路径上有Joda Time日期/时间API ，则由HV附加支持：ReadablePartial和ReadableInstant的任何实现。
 * 验证注解的元素值（日期类型）比当前时间早
 * @Pattern(regex=正则表达式, flag=)	串。HV额外支持：CharSequence的任何子类型。	验证注解的元素值与指定的正则表达式匹配
 * @Size(min=最小值, max=最大值)	字符串，集合，映射和数组。HV额外支持：CharSequence的任何子类型。	验证注解的元素值的在min和max（包含）指定区间之内，如字符长度、集合大小
 * @Valid Any non-primitive type（引用类型）	验证关联的对象，如账户对象里有一个订单对象，指定验证订单对象
 * @NotEmpty CharSequence, Collection, Map and Arrays	验证注解的元素值不为null且不为空（字符串长度不为0、集合大小不为0）
 * @Range(min=最小值, max=最大值)	CharSequence, Collection, Map and Arrays,BigDecimal, BigInteger, CharSequence, byte, short, int, long 以及原始类型各自的包装	验证注解的元素值在最小值和最大值之间
 * @NotBlank CharSequence    验证注解的元素值不为空（不为null、去除首位空格后长度为0），不同于@NotEmpty，@NotBlank只应用于字符串且在比较时会去除字符串的空格
 * @Length(min=下限, max=上限)	CharSequence	验证注解的元素值长度在min和max区间内
 * @Email CharSequence    验证注解的元素值是Email，也可以通过正则表达式和flag指定自定义的email格式
 */
public class BeanValidatorUtil {

    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();

    /**
     * 验证对象
     *
     * @param t 目标对象
     */
    public static <T> Map<String, String> validate(T t, Class... groups) {
        Validator validator = VALIDATOR_FACTORY.getValidator();
        Set validateResult = validator.validate(t, groups);
        // 如果为空
        if (validateResult.isEmpty()) {
            return Collections.emptyMap();
        } else {
            // 不为空时表示有错误
            LinkedHashMap errors = Maps.newLinkedHashMap();
            // 遍历
            Iterator iterator = validateResult.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation violation = (ConstraintViolation) iterator.next();
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return errors;
        }
    }

    /**
     * 校验某一对象是否合法
     *
     * @return 错误信息说明 k:属性名 v:错误原因
     */
    public static Map<String, String> validateObject(Object first, Object... objects) {
        if (objects != null && objects.length > 0) {
            return validateList(Lists.asList(first, objects));
        } else {
            return validate(first);
        }
    }

    /**
     * 验证列表
     *
     * @param collection 目标对象列表
     * @return 错误信息说明 k:属性名 v:错误原因
     */
    public static Map<String, String> validateList(Collection<?> collection) {
        // 基础校验collection是否为空
        Preconditions.checkNotNull(collection);
        // 遍历collection
        Iterator iterator = collection.iterator();
        Map errors;
        do {
            // 如果循环下一个为空直接返回空
            if (!iterator.hasNext()) {
                return Collections.emptyMap();
            }
            Object object = iterator.next();
            errors = validate(object);
        } while (errors.isEmpty());
        return errors;
    }

    /**
     * 校验参数方法
     *
     * @param param 检查目标对象
     * @throws InvalidParameterSpecException 无效的参数规范异常，出现该异常时通过 e.getMessage
     *                                       获取不符合规定的属性信息如："{content.cId=cId 不能为空, id=id 不能为空, name=name 不能为空}"
     */
    public static void check(Object param) throws InvalidParameterSpecException {
        Map<String, String> map = BeanValidatorUtil.validateObject(param);
        if (MapUtils.isNotEmpty(map)) {
            // 存在错误集合
            throw new InvalidParameterSpecException(map.toString());
        }
    }
}

