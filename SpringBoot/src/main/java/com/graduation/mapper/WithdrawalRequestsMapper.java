package com.graduation.mapper;

import com.graduation.entity.WithdrawalRequests;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * <p>
 * 游戏撤回审核表 Mapper 接口
 * </p>
 *
 * @author 张三
 * @since 2025-11-03
 */
@Mapper
public interface WithdrawalRequestsMapper extends BaseMapper<WithdrawalRequests> {

    /**
     * 查询所有待审核的撤回申请
     */
    @Select("SELECT * FROM withdrawal_requests WHERE status = 'pending' ORDER BY requested_at ASC")
    List<WithdrawalRequests> selectPendingRequests();

    /**
     * 根据游戏ID查询撤回申请
     */
    @Select("SELECT * FROM withdrawal_requests WHERE game_id = #{gameId} ORDER BY requested_at DESC")
    List<WithdrawalRequests> selectByGameId(@Param("gameId") Integer gameId);

    /**
     * 根据发布者ID查询撤回申请
     */
    @Select("SELECT * FROM withdrawal_requests WHERE publisher_id = #{publisherId} ORDER BY requested_at DESC")
    List<WithdrawalRequests> selectByPublisherId(@Param("publisherId") Integer publisherId);
}
