package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.common.Result;
import com.easycloud.entity.App;
import com.easycloud.entity.AppKm;
import com.easycloud.entity.AppFile;
import com.easycloud.entity.SysLog;
import com.easycloud.mapper.AppMapper;
import com.easycloud.mapper.AppKmMapper;
import com.easycloud.mapper.AppFileMapper;
import com.easycloud.mapper.SysLogMapper;
import com.easycloud.mapper.CheckinMapper;
import com.easycloud.mapper.TixianMapper;
import com.easycloud.mapper.WorkOrderMapper;
import com.easycloud.entity.Checkin;
import com.easycloud.entity.Tixian;
import com.easycloud.entity.WorkOrder;
import com.easycloud.service.IpLocationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘统计控制器
 * <p>
 * 提供管理后台仪表盘所需的各种统计数据，包括：
 * <ul>
 *   <li>应用、卡密、文件的总数和今日新增</li>
 *   <li>签到统计（今日/昨日签到人数、总奖励）</li>
 *   <li>待处理计数（待处理提现、待处理工单）</li>
 *   <li>IP 信息检测（X-Forwarded-For、X-Real-IP、Remote-Addr 及归属地）</li>
 * </ul>
 * <p>
 * 对应原 PHP 文件: admin/index.php 中的统计部分
 *
 * @author EasyCloud
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AppMapper appMapper;
    private final AppKmMapper appKmMapper;
    private final AppFileMapper appFileMapper;
    private final SysLogMapper sysLogMapper;
    private final CheckinMapper checkinMapper;
    private final TixianMapper tixianMapper;
    private final WorkOrderMapper workOrderMapper;
    private final IpLocationService ipLocationService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping
    public Result<?> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总数统计
        stats.put("appCount", appMapper.selectCount(null));
        stats.put("kmCount", appKmMapper.selectCount(null));
        stats.put("fileCount", appFileMapper.selectCount(null));

        // 今日新增统计
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        stats.put("todayApps", appMapper.selectCount(
                new LambdaQueryWrapper<App>().ge(App::getDate, todayStart)));
        stats.put("todayKm", appKmMapper.selectCount(
                new LambdaQueryWrapper<AppKm>().ge(AppKm::getAddtime, todayStart)));

        // 今日日志数
        stats.put("todayLogs", sysLogMapper.selectCount(
                new LambdaQueryWrapper<SysLog>().ge(SysLog::getDate, todayStart)));

        // 卡密使用统计
        stats.put("kmUsed", appKmMapper.selectCount(
                new LambdaQueryWrapper<AppKm>().eq(AppKm::getKmUse, "y")));
        stats.put("kmUnused", appKmMapper.selectCount(
                new LambdaQueryWrapper<AppKm>().eq(AppKm::getKmUse, "n")));

        return Result.ok(stats);
    }

    /**
     * 签到统计 - 查询今日/昨日签到人数及签到总奖励
     *
     * @return {todayCount, yesterdayCount, totalReward}
     */
    @GetMapping("/checkin-stats")
    public Result<Map<String, Object>> checkinStats() {
        Map<String, Object> data = new HashMap<>();

        // 今日签到人数
        LocalDate today = LocalDate.now();
        Long todayCount = checkinMapper.selectCount(
                new LambdaQueryWrapper<Checkin>().eq(Checkin::getDate, today));
        data.put("todayCount", todayCount);

        // 昨日签到人数
        LocalDate yesterday = today.minusDays(1);
        Long yesterdayCount = checkinMapper.selectCount(
                new LambdaQueryWrapper<Checkin>().eq(Checkin::getDate, yesterday));
        data.put("yesterdayCount", yesterdayCount);

        // 签到总奖励（使用SQL SUM聚合，避免加载全部记录到内存）
        BigDecimal totalReward = checkinMapper.sumAllReward();
        data.put("totalReward", totalReward);

        return Result.ok(data);
    }

    /**
     * 待处理计数 - 查询待处理提现和待处理工单数量
     *
     * @return {pendingTixian, pendingWorkorders}
     */
    @GetMapping("/pending-counts")
    public Result<Map<String, Object>> pendingCounts() {
        Map<String, Object> data = new HashMap<>();

        // 待处理提现: status = 0
        Long pendingTixian = tixianMapper.selectCount(
                new LambdaQueryWrapper<Tixian>().eq(Tixian::getStatus, 0));
        data.put("pendingTixian", pendingTixian);

        // 待处理工单: status = 0
        Long pendingWorkorders = workOrderMapper.selectCount(
                new LambdaQueryWrapper<WorkOrder>().eq(WorkOrder::getStatus, 0));
        data.put("pendingWorkorders", pendingWorkorders);

        return Result.ok(data);
    }

    /**
     * IP 类型检测 - 获取三种 IP 地址及归属地信息
     * <p>
     * 分别检测 X-Forwarded-For、X-Real-IP、Remote-Addr 三种 IP 来源，
     * 并通过 IpLocationService 查询各 IP 的归属地。
     *
     * @param request HTTP 请求
     * @return {forwarded, forwardedCity, realIp, realIpCity, remoteAddr, remoteAddrCity}
     */
    @GetMapping("/ip-info")
    public Result<Map<String, String>> ipInfo(HttpServletRequest request) {
        Map<String, String> data = new HashMap<>();

        // X-Forwarded-For（可能包含多个 IP，取第一个）
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && forwarded.contains(",")) {
            forwarded = forwarded.split(",")[0].trim();
        }
        data.put("forwarded", forwarded != null ? forwarded : "");
        data.put("forwardedCity", ipLocationService.getCity(forwarded));

        // X-Real-IP
        String realIp = request.getHeader("X-Real-IP");
        data.put("realIp", realIp != null ? realIp : "");
        data.put("realIpCity", ipLocationService.getCity(realIp));

        // Remote-Addr
        String remoteAddr = request.getRemoteAddr();
        data.put("remoteAddr", remoteAddr != null ? remoteAddr : "");
        data.put("remoteAddrCity", ipLocationService.getCity(remoteAddr));

        return Result.ok(data);
    }
}
