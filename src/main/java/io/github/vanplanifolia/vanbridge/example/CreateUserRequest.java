package io.github.vanplanifolia.vanbridge.example;

import io.github.vanplanifolia.vanbridge.model.rrmodel.ApiRequestModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 示例：创建用户请求模型。
 * 实现 ApiRequestModel 后自动获得 toRequestJSON() / toRequestMap() 序列化能力。
 */
@Data
@Accessors(chain = true)
public class CreateUserRequest implements ApiRequestModel {

    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;
}
