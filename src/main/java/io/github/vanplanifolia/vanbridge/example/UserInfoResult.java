package io.github.vanplanifolia.vanbridge.example;

import io.github.vanplanifolia.vanbridge.model.rrmodel.ApiResultModel;
import lombok.Data;

/**
 * 示例：用户信息响应模型。
 * 实现 ApiResultModel（标记接口），字段与第三方返回的JSON结构对应。
 * <p>
 * 对应 JSON 结构（以 jsonplaceholder.typicode.com 为例）：
 * <pre>{@code
 * {
 *   "id": 1,
 *   "name": "Leanne Graham",
 *   "username": "Bret",
 *   "email": "Sincere@april.biz",
 *   "phone": "1-770-736-8031 x56442",
 *   "website": "hildegard.org"
 * }
 * }</pre>
 */
@Data
public class UserInfoResult implements ApiResultModel {

    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;
}
