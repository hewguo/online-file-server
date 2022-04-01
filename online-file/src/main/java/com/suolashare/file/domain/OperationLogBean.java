package com.suolashare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * 操作日志基础信息类
 *
 * @author ma116
 */
@Data
@Table(name = "operationlog")
@Entity
@TableName("operationlog")
public class OperationLogBean {
    /**
     * 操作日志id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long operationlogid;

    /**
     * 用户id
     */
    @Column(columnDefinition="bigint")
    private Long userId;

    /**
     * 操作
     */
    @Column(columnDefinition="varchar(50) ")
    private String operation;

    /**
     * 操作对象
     */
    private String operationObj;

    /**
     * 终端IP
     */
    @Column(columnDefinition="varchar(20)")
    private String terminal;

    /**
     * 操作结果
     */
    @Column(columnDefinition="varchar(20)")
    private String result;

    /**
     * 操作详情
     */
    @Column(columnDefinition="varchar(100)")
    private String detail;

    /**
     * 操作源
     */
    private String source;

    /**
     * 时间
     */
    @Column(columnDefinition="varchar(25)")
    private String time;

    /**
     * 日志级别
     */
    private String logLevel;

    @Column(columnDefinition="int")
    private Integer platform;


}
