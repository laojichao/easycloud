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
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘统计 - 对应 PHP admin/index.php 统计部分
 */
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AppMapper appMapper;
    private final AppKmMapper appKmMapper;
    private final AppFileMapper appFileMapper;
    private final SysLogMapper sysLogMapper;

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
}
