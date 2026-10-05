package com.easycloud.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.easycloud.common.ClientIpUtil;
import com.easycloud.common.JwtUtil;
import com.easycloud.common.Result;
import com.easycloud.entity.Checkin;
import com.easycloud.entity.InviteLog;
import com.easycloud.entity.Point;
import com.easycloud.entity.Tixian;
import com.easycloud.entity.User;
import com.easycloud.entity.WorkOrder;
import com.easycloud.service.CaptchaService;
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
 * 用户中心接口 - 注册/登录/签到/工单/邀请/积分/提现
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
    private final CaptchaService captchaService;
    private final JwtUtil jwtUtil;

    @GetMapping("/info")
    public Result<?> getUserInfo(HttpServletRequest request) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("uid", user.getUid());
        data.put("username", user.getUser());
        data.put("rmb", user.getRmb());
        data.put("qq", user.getQq());
        data.put("email", user.getEmail());
        data.put("inviteCode", user.getInvitecode());
        data.put("points", pointService.getTotal(uid));
        data.put("todayChecked", checkinService.getTodayCheckin(uid));
        return Result.ok(data);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String clientIp = ClientIpUtil.getClientIp(request);
        if (!rateLimiterService.isAllowed("user_login:" + clientIp, 10, 60)) {
            return Result.fail(429, "尝试次数过多，请稍后再试");
        }
        Result<?> captchaError = checkCaptcha(body);
        if (captchaError != null) {
            return Result.fail(400, captchaError.getMsg());
        }
        User user;
        try {
            user = userService.login(body.get("username"), body.get("password"));
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
        String token = jwtUtil.generateUserToken(user.getUid());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUser());
        data.put("role", "user");
        data.put("uid", user.getUid());
        return Result.ok(data);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String clientIp = ClientIpUtil.getClientIp(request);
        if (!rateLimiterService.isAllowed("user_register:" + clientIp, 5, 300)) {
            return Result.fail(429, "注册过于频繁，请稍后再试");
        }
        Result<?> captchaError = checkCaptcha(body);
        if (captchaError != null) {
            return Result.fail(400, captchaError.getMsg());
        }
        User user;
        try {
            user = userService.register(body.get("username"), body.get("password"),
                    body.get("qq"), body.get("email"), body.get("invitecode"));
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
        user.setIp(ClientIpUtil.getClientIp(request));
        userService.updateUser(user);
        return Result.ok("注册成功", user);
    }

    @GetMapping("/checkin")
    public Result<?> checkin(HttpServletRequest request) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        if (checkinService.getTodayCheckin(uid)) {
            return Result.fail(400, "今日已签到");
        }
        try {
            BigDecimal reward = checkinService.checkin(uid);
            Map<String, Object> data = new HashMap<>();
            data.put("reward", reward);
            return Result.ok("签到成功", data);
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    @GetMapping("/checkin/list")
    public Result<?> checkinList(HttpServletRequest request,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "20") int size) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        Page<Checkin> result = checkinService.getCheckinList(uid, page, size);
        return Result.ok(result);
    }

    @PostMapping("/workorder")
    public Result<?> createWorkOrder(HttpServletRequest request, @RequestBody Map<String, String> body) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        try {
            WorkOrder order = workOrderService.create(uid, body.get("title"), body.get("content"));
            return Result.ok("工单创建成功", order);
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    @GetMapping("/workorder/list")
    public Result<?> workOrderList(HttpServletRequest request,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        Page<WorkOrder> result = workOrderService.getList(uid, page, size);
        return Result.ok(result);
    }

    @GetMapping("/invite/list")
    public Result<?> inviteList(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "20") int size) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        Page<InviteLog> result = inviteService.getList(uid, page, size);
        return Result.ok(result);
    }

    @GetMapping("/point/list")
    public Result<?> pointList(HttpServletRequest request,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "20") int size) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        Page<Point> result = pointService.getList(uid, page, size);
        return Result.ok(result);
    }

    @PostMapping("/tixian")
    public Result<?> applyTixian(HttpServletRequest request, @RequestBody Map<String, Object> body) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        BigDecimal money;
        try {
            money = new BigDecimal(String.valueOf(body.get("money")));
        } catch (NumberFormatException e) {
            return Result.fail(400, "金额无效");
        }
        String type = String.valueOf(body.getOrDefault("type", "alipay"));
        try {
            Tixian tixian = tixianService.apply(uid,
                    String.valueOf(body.get("account")),
                    String.valueOf(body.get("name")),
                    money, type);
            return Result.ok("提现申请已提交", tixian);
        } catch (RuntimeException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    @GetMapping("/tixian/list")
    public Result<?> tixianList(HttpServletRequest request,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "20") int size) {
        Long uid = getUid(request);
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        Page<Tixian> result = tixianService.getList(uid, page, size);
        return Result.ok(result);
    }

    /**
     * 验证码校验；未开启验证码时返回 null
     */
    private Result<?> checkCaptcha(Map<String, String> body) {
        if (!captchaService.isLoginCaptchaEnabled()) {
            return null;
        }
        String captchaId = body.get("captchaId");
        String captchaCode = body.get("captchaCode");
        if (captchaCode == null || captchaCode.isBlank()) {
            return Result.fail(400, "请输入验证码");
        }
        if (!captchaService.verifyCaptcha(captchaId, captchaCode)) {
            return Result.fail(400, "验证码错误");
        }
        return null;
    }

    private Long getUid(HttpServletRequest request) {
        Object uid = request.getAttribute("userId");
        return uid instanceof Long ? (Long) uid : null;
    }
}
