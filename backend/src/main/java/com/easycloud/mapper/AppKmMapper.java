package com.easycloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycloud.entity.AppKm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AppKmMapper extends BaseMapper<AppKm> {

    /**
     * 更新使用者信息（首次登录）
     */
    @Update("UPDATE yixi_appkm SET user = #{user}, user_ip = #{userIp}, km_use = #{kmUse} WHERE id = #{id}")
    int updateUser(@Param("id") Long id, @Param("user") String user, @Param("userIp") String userIp, @Param("kmUse") String kmUse);

    /**
     * 时长卡登录 - 更新使用时间、结束时间、使用者信息
     */
    @Update("UPDATE yixi_appkm SET use_time = #{useTime}, user = #{user}, end_time = #{endTime}, user_ip = #{userIp}, km_use = #{kmUse} WHERE id = #{id}")
    int updateKmLogin(@Param("id") Long id, @Param("useTime") long useTime, @Param("user") String user, @Param("endTime") String endTime, @Param("userIp") String userIp, @Param("kmUse") String kmUse);

    /**
     * 次数卡登录 - 更新使用时间、扣减次数、使用者信息
     */
    @Update("UPDATE yixi_appkm SET use_time = #{useTime}, amount = #{amount}, user = #{user}, user_ip = #{userIp}, km_use = #{kmUse} WHERE id = #{id}")
    int updateSingleLogin(@Param("id") Long id, @Param("useTime") long useTime, @Param("amount") int amount, @Param("user") String user, @Param("userIp") String userIp, @Param("kmUse") String kmUse);

    /**
     * 扣减次数卡 amount
     */
    @Update("UPDATE yixi_appkm SET amount = amount - 1 WHERE id = #{id}")
    int decreaseAmount(@Param("id") Long id);

    /**
     * 解绑卡密（永久卡/无限次卡）
     */
    @Update("UPDATE yixi_appkm SET user = '', user_ip = '', km_change = km_change + 1, km_change_time = #{changeTime} WHERE id = #{id}")
    int unbindKm(@Param("id") Long id, @Param("changeTime") long changeTime);

    /**
     * 解绑卡密（普通时长卡 - 扣除时长）
     */
    @Update("UPDATE yixi_appkm SET user = '', user_ip = '', use_time = #{useTime}, end_time = #{endTime}, km_change = km_change + 1, km_change_time = #{changeTime} WHERE id = #{id}")
    int unbindKmWithDuration(@Param("id") Long id, @Param("useTime") long useTime, @Param("endTime") long endTime, @Param("changeTime") long changeTime);

    /**
     * 解绑卡密（普通次数卡 - 扣减次数）
     */
    @Update("UPDATE yixi_appkm SET user = '', user_ip = '', use_time = #{useTime}, amount = #{amount}, km_change = km_change + 1, km_change_time = #{changeTime} WHERE id = #{id}")
    int unbindKmWithAmount(@Param("id") Long id, @Param("useTime") long useTime, @Param("amount") int amount, @Param("changeTime") long changeTime);
}
