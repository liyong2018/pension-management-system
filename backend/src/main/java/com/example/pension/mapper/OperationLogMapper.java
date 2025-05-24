package com.example.pension.mapper;

import com.example.pension.model.OperationLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OperationLogMapper {
    
    /**
     * 插入操作日志
     */
    @Insert("""
        INSERT INTO operation_log (username, user_id, operation_type, operation_desc, module, 
                                 log_level, ip_address, user_agent, request_url, request_method, 
                                 request_params, response_data, request_time, error_message, 
                                 error_stack, create_time)
        VALUES (#{username}, #{userId}, #{operationType}, #{operationDesc}, #{module}, 
                #{logLevel}, #{ipAddress}, #{userAgent}, #{requestUrl}, #{requestMethod}, 
                #{requestParams}, #{responseData}, #{requestTime}, #{errorMessage}, 
                #{errorStack}, #{createTime})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(OperationLog operationLog);
    
    /**
     * 根据ID查询操作日志
     */
    @Select("SELECT * FROM operation_log WHERE id = #{id}")
    OperationLog findById(Long id);
    
    /**
     * 查询操作日志列表（支持分页和条件查询）
     */
    @Select("""
        <script>
        SELECT * FROM operation_log 
        WHERE 1=1
        <if test="username != null and username != ''">
            AND username LIKE CONCAT('%', #{username}, '%')
        </if>
        <if test="operationType != null and operationType != ''">
            AND operation_type = #{operationType}
        </if>
        <if test="logLevel != null and logLevel != ''">
            AND log_level = #{logLevel}
        </if>
        <if test="module != null and module != ''">
            AND module LIKE CONCAT('%', #{module}, '%')
        </if>
        <if test="startTime != null">
            AND create_time >= #{startTime}
        </if>
        <if test="endTime != null">
            AND create_time &lt;= #{endTime}
        </if>
        ORDER BY create_time DESC
        LIMIT #{offset}, #{limit}
        </script>
    """)
    List<OperationLog> findByConditions(@Param("username") String username,
                                      @Param("operationType") String operationType,
                                      @Param("logLevel") String logLevel,
                                      @Param("module") String module,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);
    
    /**
     * 统计总数
     */
    @Select("""
        <script>
        SELECT COUNT(*) FROM operation_log 
        WHERE 1=1
        <if test="username != null and username != ''">
            AND username LIKE CONCAT('%', #{username}, '%')
        </if>
        <if test="operationType != null and operationType != ''">
            AND operation_type = #{operationType}
        </if>
        <if test="logLevel != null and logLevel != ''">
            AND log_level = #{logLevel}
        </if>
        <if test="module != null and module != ''">
            AND module LIKE CONCAT('%', #{module}, '%')
        </if>
        <if test="startTime != null">
            AND create_time >= #{startTime}
        </if>
        <if test="endTime != null">
            AND create_time &lt;= #{endTime}
        </if>
        </script>
    """)
    long countByConditions(@Param("username") String username,
                          @Param("operationType") String operationType,
                          @Param("logLevel") String logLevel,
                          @Param("module") String module,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据ID删除操作日志
     */
    @Delete("DELETE FROM operation_log WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 批量删除操作日志
     */
    @Delete("""
        <script>
        DELETE FROM operation_log WHERE id IN
        <foreach collection="ids" item="id" open="(" close=")" separator=",">
            #{id}
        </foreach>
        </script>
    """)
    int deleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 获取统计数据
     */
    @Select("""
        SELECT 
            COUNT(*) as totalLogs,
            COUNT(CASE WHEN DATE(create_time) = CURDATE() THEN 1 END) as todayLogs,
            COUNT(CASE WHEN log_level = 'ERROR' THEN 1 END) as errorLogs,
            COUNT(DISTINCT username) as activeUsers
        FROM operation_log
    """)
    Map<String, Object> getStatistics();
    
    /**
     * 按日期统计日志数量
     */
    @Select("""
        SELECT DATE(create_time) as date, COUNT(*) as count
        FROM operation_log 
        WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
        GROUP BY DATE(create_time)
        ORDER BY date DESC
    """)
    List<Map<String, Object>> getLogCountByDate();
    
    /**
     * 按操作类型统计
     */
    @Select("""
        SELECT operation_type as type, COUNT(*) as count
        FROM operation_log 
        GROUP BY operation_type
        ORDER BY count DESC
    """)
    List<Map<String, Object>> getLogCountByOperationType();
    
    /**
     * 按模块统计
     */
    @Select("""
        SELECT module, COUNT(*) as count
        FROM operation_log 
        WHERE module IS NOT NULL
        GROUP BY module
        ORDER BY count DESC
    """)
    List<Map<String, Object>> getLogCountByModule();
} 