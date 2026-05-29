package io.github.vanplanifolia.vanbridge.example;

import io.github.vanplanifolia.vanbridge.model.ApiResponse;

import java.util.List;

/**
 * 示例：调用方视角的使用演示。
 * <p>
 * 展示 van-bridge 的核心优势 —— 调用方代码无需任何 try/catch，
 * 所有异常已被 ApiTemplate 转换为标准化的 ApiResponse。
 * </p>
 */
public class QuickStartExample {

    public static void main(String[] args) {
        UserCenterService service = new UserCenterService();

        // ---- 场景1：单对象响应 ----
        ApiResponse<UserInfoResult> response = service.getUserById(1L);
        if (response.isSuccess()) {
            UserInfoResult user = response.getResult();
            System.out.println("用户名: " + user.getName());
            System.out.println("邮箱:   " + user.getEmail());
        } else {
            System.err.println("获取用户失败: " + response.getMessage());
        }

        // ---- 场景2：创建用户 ----
        CreateUserRequest newUser = new CreateUserRequest()
                .setName("张三")
                .setUsername("zhangsan")
                .setEmail("zhangsan@example.com");

        ApiResponse<UserInfoResult> createResp = service.createUser(newUser);
        if (createResp.isSuccess()) {
            System.out.println("创建成功，新用户ID: " + createResp.getResult().getId());
        } else {
            System.err.println("创建失败: " + createResp.getMessage());
        }

        // ---- 场景3：分页列表响应（PageResult<T> 包装类绕过 R extends ApiResultModel 约束）----
        ApiResponse<PageResult<UserInfoResult>> listResp = service.listUsers();
        if (listResp.isSuccess()) {
            PageResult<UserInfoResult> page = listResp.getResult();
            System.out.println("第 " + page.getPageNo() + " 页，"
                    + "共 " + page.getTotalPages() + " 页 / "
                    + page.getTotalCount() + " 条记录");
            List<UserInfoResult> users = page.getList();
            for (UserInfoResult user : users) {
                System.out.println("  - " + user.getName());
            }
        } else {
            System.err.println("获取列表失败: " + listResp.getMessage());
        }

        System.out.println("\n所有调用完成——无论成功或失败，都不会有未捕获的异常。");
    }
}
