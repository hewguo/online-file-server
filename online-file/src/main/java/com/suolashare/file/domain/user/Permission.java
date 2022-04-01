package com.suolashare.file.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * 权限实体类
 */
@Data
@Table(name = "permission")
@Entity
@TableName("permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long permissionid;//主键

    @Column(columnDefinition="bigint")
    private Long parentId;

    @Column(columnDefinition="varchar")
    private String permissionName;//名称.

    @Column(columnDefinition="int")
    private Integer resourceType;//资源类型

    @Column(columnDefinition="varchar(30) ")
    private String permissionCode;

    @Column(columnDefinition="int ")
    private Integer orderNum;

    @Column(columnDefinition="varchar(30)")
    private String createTime;
    @Column(columnDefinition="bigint ")
    private Long createUserId;
    @Column(columnDefinition="varchar(30) ")
    private String modifyTime;
    @Column(columnDefinition="bigint")
    private Long modifyUserId;



}