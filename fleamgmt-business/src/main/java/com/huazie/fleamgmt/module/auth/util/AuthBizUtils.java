package com.huazie.fleamgmt.module.auth.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> 授权管理模块业务工具 </p>
 *
 * <p> 页面上的日期统一以 {@code yyyy-MM-dd} 字符串提交，在此处集中解析，
 * 避免依赖 Spring MVC 默认的日期绑定行为（不同 Servlet 容器下表现不一致），
 * 也避免各 Controller 各写一份。 </p>
 *
 * @author huazie
 * @version 1.0.0
 * @since 1.0.0
 */
public final class AuthBizUtils {

    /**
     * <p> 页面日期格式 </p>
     */
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * <p> 未分组时的默认组编号 </p>
     *
     * <p> 角色、权限、用户等数据的 group_id 在库中为 NOT NULL，页面未选择分组时统一落 -1。 </p>
     */
    private static final Long DEFAULT_GROUP_ID = -1L;

    /**
     * <p> 默认失效日期（远期） </p>
     *
     * <p> flea_user / flea_account 的 effective_date、expiry_date 为 NOT NULL，
     * 页面未填写时给出默认值，避免插入失败。 </p>
     */
    private static final String DEFAULT_EXPIRY_DATE = "2099-12-31";

    private AuthBizUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * <p> 解析页面提交的日期字符串 </p>
     *
     * <p> 说明： {@code SimpleDateFormat} 非线程安全，此处按调用新建实例；
     * 业务页面提交频率极低，无需引入 ThreadLocal 优化。 </p>
     *
     * @param dateStr 日期字符串（yyyy-MM-dd）
     * @return 解析出的日期；入参为空或格式非法时返回 {@code null}
     * @since 1.0.0
     */
    public static Date parseDate(String dateStr) {

        if (dateStr == null || dateStr.trim().length() == 0) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        dateFormat.setLenient(false);

        try {
            return dateFormat.parse(dateStr.trim());
        } catch (ParseException e) {
            // 页面日期为非必填项，格式非法时按「未填写」处理，交由页面侧校验兜底
            return null;
        }
    }

    /**
     * <p> 将日期格式化为页面日期字符串 </p>
     *
     * <p> 页面日期控件（input[type=date]）要求 {@code yyyy-MM-dd}，
     * 而 JSON 对 Date 的默认序列化结果与之不符，故统一在此转换后再返回给页面。 </p>
     *
     * @param date 日期
     * @return 日期字符串；入参为空时返回空串
     * @since 1.0.0
     */
    public static String formatDate(Date date) {

        if (date == null) {
            return "";
        }

        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }

    /**
     * <p> 解析生效日期，未填写时取当前时间 </p>
     *
     * @param dateStr 日期字符串（yyyy-MM-dd）
     * @return 生效日期
     * @since 1.0.0
     */
    public static Date parseEffectiveDate(String dateStr) {
        Date effectiveDate = parseDate(dateStr);
        return (effectiveDate != null) ? effectiveDate : new Date();
    }

    /**
     * <p> 解析失效日期，未填写时取默认远期日期 </p>
     *
     * @param dateStr 日期字符串（yyyy-MM-dd）
     * @return 失效日期
     * @since 1.0.0
     */
    public static Date parseExpiryDate(String dateStr) {
        Date expiryDate = parseDate(dateStr);
        return (expiryDate != null) ? expiryDate : parseDate(DEFAULT_EXPIRY_DATE);
    }

    /**
     * <p> 判断编号是否为有效正数 </p>
     *
     * @param id 编号
     * @return true-有效; false-无效
     * @since 1.0.0
     */
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    /**
     * <p> 获取分组编号，未指定时返回默认值 </p>
     *
     * @param groupId 分组编号
     * @return 分组编号（未指定时为 -1）
     * @since 1.0.0
     */
    public static Long getGroupIdOrDefault(Long groupId) {
        return (groupId != null) ? groupId : DEFAULT_GROUP_ID;
    }

    /**
     * <p> 获取状态值，未指定时返回默认值 </p>
     *
     * @param state        状态值
     * @param defaultState 默认状态值
     * @return 状态值
     * @since 1.0.0
     */
    public static Integer getStateOrDefault(Integer state, Integer defaultState) {
        return (state != null) ? state : defaultState;
    }

    /**
     * <p> 明细行的在用状态值（1-正常） </p>
     */
    private static final int STATE_IN_USE = 1;

    /**
     * <p> 统计明细行的总数与在用数 </p>
     *
     * <p> 列表类页面的统计卡片（总数 / 正常 / 停用）由此统一产出，
     * 避免各 Controller 各写一份计数逻辑。 </p>
     *
     * @param rows     明细行集合
     * @param stateKey 状态字段名
     * @return 汇总统计（total-总数；enabled-在用数；disabled-非在用数）
     * @since 1.0.0
     */
    public static Map<String, Object> summarize(List<Map<String, Object>> rows, String stateKey) {

        int total = (rows == null) ? 0 : rows.size();
        int enabled = 0;

        if (total > 0) {
            for (Map<String, Object> row : rows) {
                if (row == null) {
                    continue;
                }
                Object state = row.get(stateKey);
                if (state instanceof Number && STATE_IN_USE == ((Number) state).intValue()) {
                    enabled++;
                }
            }
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("total", total);
        summary.put("enabled", enabled);
        summary.put("disabled", total - enabled);
        return summary;
    }

}
