package com.suolashare.file.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author MAC
 * @version 1.0
 * @description: TODO
 * @date 2021/12/30 16:14
 */
@Data
@Table(name = "role_permission")
@Entity
@TableName("role_permission")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;
    @Column(columnDefinition="bigint")
    private Long roleId;
    @Column(columnDefinition="bigint")
    private Long permissionId;
}
