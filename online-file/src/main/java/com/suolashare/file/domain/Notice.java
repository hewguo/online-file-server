package com.suolashare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author MAC
 * @version 1.0
 * @description: 公告
 * @date 2021/11/22 22:16
 */
@Data
@Table(name = "notice")
@Entity
@TableName("notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint", unique = true)
    private Long noticeid;

    @Column(columnDefinition="varchar(100)", nullable = false)
    private String title;
    @Column(columnDefinition="int")
    private Integer platform;

    @Column(columnDefinition = "text")
    private String markdownContent;
    @Column(columnDefinition = "text")
    private String content;
    @Column(columnDefinition="varchar(25) ")
    private String validDateTime;
    @Column(columnDefinition="int")
    private int isLongValidData;

    @Column(columnDefinition="varchar(25) ")
    private String createTime;
    @Column(columnDefinition="bigint ")
    private Long createUserId;
    @Column(columnDefinition="varchar(25) ")
    private String modifyTime;
    @Column(columnDefinition="bigint")
    private Long modifyUserId;
}
