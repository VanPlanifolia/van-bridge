package io.github.vanplanifolia.vanbridge.example;

import com.alibaba.fastjson2.annotation.JSONField;
import io.github.vanplanifolia.vanbridge.model.rrmodel.ApiResultModel;
import lombok.Data;

import java.util.List;

/**
 * 通用分页结果包装类。
 * <p>
 * 框架泛型约束 {@code R extends ApiResultModel} 禁止直接使用 {@code List<T>} 作为响应类型，
 * 因此用此类将列表数据与分页信息一并包装。对应常见的分页接口返回结构：
 * </p>
 * <pre>{@code
 * {
 *   "total_pages": 10,
 *   "page_no": 1,
 *   "total_count": 98,
 *   "list": [ {...}, {...} ]
 * }
 * }</pre>
 *
 * <p>
 * 使用时配合 {@link com.alibaba.fastjson2.TypeReference} 捕获完整泛型：
 * <pre>{@code
 * new TypeReference<PageResult<UserInfoResult>>() {};
 * }</pre>
 * </p>
 *
 * @param <T> 列表元素类型
 */
@Data
public class PageResult<T> implements ApiResultModel {

    @JSONField(name = "total_pages")
    private Integer totalPages;

    @JSONField(name = "page_no")
    private Integer pageNo;

    @JSONField(name = "total_count")
    private Integer totalCount;

    private List<T> list;
}
