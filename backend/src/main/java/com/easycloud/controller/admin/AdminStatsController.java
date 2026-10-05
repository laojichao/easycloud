package com.easycloud.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easycloud.common.ClientIpUtil;
import com.easycloud.common.Result;
import com.easycloud.entity.App;
import com.easycloud.entity.AppFile;
import com.easycloud.entity.AppKm;
import com.easycloud.entity.Checkin;
import com.easycloud.entity.SysLog;
import com.easycloud.entity.Tixian;
import com.easycloud.entity.WorkOrder;
import com.easycloud.mapper.AppFileMapper;
import com.easycloud.mapper.AppKmMapper;
import com.easycloud.mapper.AppMapper;
import com.easycloud.mapper.CheckinMapper;
import com.easycloud.mapper.SysLogMapper;
import com.easycloud.mapper.TixianMapper;
import com.easycloud.mapper.WorkOrderMapper;
import com.easycloud.service.IpLocationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘统计 - 对应 PHP admin/index.php 统计与 ajax.php count/qdcount
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

    @GetMapping
    public Result<?> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("appCount", appMapper.selectCount(null));
        stats.put("kmCount", appKmMapper.selectCount(null));
        stats.put("fileCount", appFileMapper.selectCount(null));

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        stats.put("todayApps", appMapper.selectCount(
                new LambdaQueryWrapper<App>().ge(App::getDate, todayStart)));
        stats.put("todayKm", appKmMapper.selectCount(
                new LambdaQueryWrapper<AppKm>().ge(AppKm::getAddtime, todayStart)));
        stats.put("todayLogs", sysLogMapper.selectCount(
                new LambdaQueryWrapper<SysLog>().ge(SysLog::getDate, todayStart)));

        stats.put("kmUsed", appKmMapper.selectCount(
                new LambdaQueryWrapper<AppKm>().eq(AppKm::getKmUse, "y")));
        stats.put("kmUnused", appKmMapper.selectCount(
                new LambdaQueryWrapper<AppKm>().eq(AppKm::getKmUse, "n")));

        return Result.ok(stats);
    }

    /**
     * 签到统计 - 对应 PHP ajax.php qdcount
     */
    @GetMapping("/checkin-stats")
    public Result<Map<String, Object>> checkinStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        Long todayCount = checkinMapper.selectCount(
                new LambdaQueryWrapper<Checkin>().eq(Checkin::getDate, today));
        Long yesterdayCount = checkinMapper.selectCount(
                new LambdaQueryWrapper<Checkin>().eq(Checkin::getDate, yesterday));
        Long totalCount = checkinMapper.selectCount(null);

        BigDecimal todayReward = checkinMapper.selectList(
                        new LambdaQueryWrapper<Checkin>().eq(Checkin::getDate, today)).stream()
                .map(Checkin::getReward)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal yesterdayReward = checkinMapper.selectList(
                        new LambdaQueryWrapper<Checkin>().eq(Checkin::getDate, yesterday)).stream()
                .map(Checkin::getReward)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("todayCount", todayCount);
        stats.put("yesterdayCount", yesterdayCount);
        stats.put("totalCount", totalCount);
        stats.put("todayReward", todayReward);
        stats.put("yesterdayReward", yesterdayReward);
        return Result.ok(stats);
    }

    /**
     * 待处理数量 - 对应 PHP ajax.php count（提现 + 工单）
     */
    @GetMapping("/pending-counts")
    public Result<Map<String, Object>> pendingCounts() {
        Map<String, Object> data = new HashMap<>();
        data.put("tixian", tixianMapper.selectCount(
                new LambdaQueryWrapper<Tixian>().eq(Tixian::getStatus, Tixian.STATUS_PENDING)));
        data.put("workorder", workOrderMapper.selectCount(
                new LambdaQueryWrapper<WorkOrder>().eq(WorkOrder::getStatus, WorkOrder.STATUS_PENDING)));
        return Result.ok(data);
    }

    /**
     * IP 归属地查询
     */
    @GetMapping("/ip-info")
    public Result<Map<String, Object>> ipInfo(@RequestParam(required = false) String ip,
                                              HttpServletRequest request) {
        String target = (ip == null || ip.isBlank())
                ? ClientIpUtil.getClientIp(request)
                : ip;
        Map<String, Object> data = new HashMap<>();
        data.put("ip", target);
        data.put("city", ipLocationService.getCity(target));
        return Result.ok(data);
    }
}
