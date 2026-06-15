package com.easycloud.controller;

import com.easycloud.common.ClientIpUtil;
import com.easycloud.common.JwtUtil;
import com.easycloud.common.Result;
import com.easycloud.entity.User;
import com.easycloud.service.CheckinService;
import com.easycloud.service.InviteService;
import com.easycloud.service.PointService;
import com.easycloud.service.RateLimiterService;
import com.easycloud.service.TixianService;
import com.easycloud.service.UserService;
import com.easycloud.service.WorkOrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户端 API 控制器
 * <p>
 * 提供登录用户可访问的个人功能接口，包括：
 * <ul>
 *   <li>签到：每日签到获取余额奖励，查看签到历史</li>
 *   <li>工单：提交工单咨询，查看工单列表</li>
 *   <li>邀请：查看邀请返利记录</li>
 *   <li>积分：查看积分变动记录</li>
 *   <li>提现：申请提现到支付宝/微信，查看提现记录</li>
 * </ul>
 * 所有接口需要通过 UserAuthInterceptor 进行用户身份认证（JWT Token）。
 * 用户 ID 从请求属性 "uid" 中获取（由拦截器注入）。
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final CheckinService checkinService;
    private final WorkOrderService workOrderService;
    private final InviteService inviteService;
    private final PointService pointService;
    private final TixianService tixianService;
    private final UserService userService;
    private final RateLimiterService rateLimiterService;
    private final JwtUtil jwtUtil;

    // ==================== 登录/注册 ====================

    /**
     * 用户登录
     * <p>
     * 验证用户名和密码，成功后生成 JWT Token 返回。
     * Token 的 subject 为用户 UID（字符串形式），与管理员登录（subject 为用户名）区分。
     *
     * @param body 请求体，包含 username（用户名）和 password（密码）
     * @return 成功返回 {token, uid, username}，失败返回错误消息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        // 速率限制：同一 IP 5分钟内最多10次登录尝试
        String clientIp = ClientIpUtil.getClientIp(request);
        if (!rateLimiterService.isAllowed("user_login:" + clientIp, 10, 300)) {
            return Result.fail("登录尝试过于频繁，请5分钟后再试");
        }

        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.isEmpty()) {
            return Result.fail("用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            return Result.fail("密码不能为空");
        }

        User user = userService.login(username, password);
        if (user == null) {
            return Result.fail("用户名或密码错误");
        }

        // 生成 JWT Token，subject = uid.toString()
        String token = jwtUtil.generateToken(user.getUid().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("uid", user.getUid());
        data.put("username", user.getUser());

        return Result.ok("登录成功", data);
    }

    /**
     * 用户注册
     * <p>
     * 注册新用户，校验用户名唯一性，密码使用 MD5 加盐加密存储。
     * 注册成功后返回用户信息（不含密码）。
     *
     * @param body 请求体，包含 username（用户名）、password（密码）、qq（QQ号）、email（邮箱，可选）
     * @return 成功返回用户信息，失败返回错误消息
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, String> body, HttpServletRequest request) {
        // 速率限制：同一 IP 10分钟内最多5次注册
        String clientIp = ClientIpUtil.getClientIp(request);
        if (!rateLimiterService.isAllowed("user_register:" + clientIp, 5, 600)) {
            return Result.fail("注册过于频繁，请10分钟后再试");
        }

        String username = body.get("username");
        String password = body.get("password");
        String qq = body.get("qq");
        String email = body.get("email");

        if (username == null || username.isEmpty()) {
            return Result.fail("用户名不能为空");
        }
        if (username.length() < 3 || username.length() > 20) {
            return Result.fail("用户名长度应为 3-20 位");
        }
        if (password == null || password.length() < 6) {
            return Result.fail("密码长度不能小于 6 位");
        }
        if (qq == null || qq.isEmpty()) {
            return Result.fail("QQ 号不能为空");
        }

        try {
            // 获取客户端 IP
            String ip = ClientIpUtil.getClientIp(request);
            User user = userService.register(username, password, ip,
                    (qq != null && !qq.isEmpty()) ? qq : null,
                    (email != null && !email.isEmpty()) ? email : null);

            // 清除密码后返回
            user.setPwd(null);
            return Result.ok("注册成功", user);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // ==================== 签到 ====================

    /**
     * 用户签到
     */
    @GetMapping("/checkin")
    public Result<?> checkin(HttpServletRequest request) {
        try {
            Long uid = getUid(request);
            BigDecimal reward = checkinService.checkin(uid);
            Map<String, Object> data = new HashMap<>();
            data.put("reward", reward);
            data.put("checkedIn", true);
            return Result.ok("签到成功", data);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 签到记录列表
     */
    @GetMapping("/checkin/list")
    public Result<?> checkinList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long uid = getUid(request);
            return Result.ok(checkinService.getCheckinList(uid, page, size));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // ==================== 工单 ====================

    /**
     * 创建工单
     */
    @PostMapping("/workorder")
    public Result<?> createWorkOrder(
            HttpServletRequest request,
            @RequestBody Map<String, String> body) {
        try {
            Long uid = getUid(request);
            return Result.ok("创建成功", workOrderService.create(uid, body.get("title"), body.get("content")));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 用户工单列表
     */
    @GetMapping("/workorder/list")
    public Result<?> workOrderList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long uid = getUid(request);
            return Result.ok(workOrderService.getList(uid, page, size));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // ==================== 邀请返利 ====================

    /**
     * 邀请记录列表
     */
    @GetMapping("/invite/list")
    public Result<?> inviteList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long uid = getUid(request);
            return Result.ok(inviteService.getList(uid, page, size));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // ==================== 积分 ====================

    /**
     * 积分记录列表
     */
    @GetMapping("/point/list")
    public Result<?> pointList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long uid = getUid(request);
            return Result.ok(pointService.getList(uid, page, size));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    // ==================== 提现 ====================

    /**
     * 申请提现
     */
    @PostMapping("/tixian")
    public Result<?> applyTixian(
            HttpServletRequest request,
            @RequestBody Map<String, Object> body) {
        try {
            Long uid = getUid(request);
            if (body.get("money") == null) {
                return Result.fail("提现金额不能为空");
            }
            String account = (String) body.get("account");
            String name = (String) body.get("name");
            BigDecimal money = new BigDecimal(body.get("money").toString());
            String type = (String) body.get("type");
            return Result.ok("申请成功", tixianService.apply(uid, account, name, money, type));
        } catch (NumberFormatException e) {
            return Result.fail("金额格式不正确");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 用户提现记录列表
     */
    @GetMapping("/tixian/list")
    public Result<?> tixianList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long uid = getUid(request);
            return Result.ok(tixianService.getList(uid, page, size));
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 从请求属性中获取用户 uid（由 UserAuthInterceptor 注入）
     * 如果为空则抛出未登录异常
     */
    private Long getUid(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        if (uid == null) {
            throw new RuntimeException("未登录");
        }
        return uid;
    }

}
