package com.suolashare.file.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 角色实体信息类
 */
@Data
@Table(name = "role")
@Entity
@TableName("role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long roleid; // 编号

    @Column(columnDefinition="varchar(20)")
    private String roleName;

    @Column(columnDefinition="varchar(100)")
    private String description;

    @Column(columnDefinition="int")
    private Integer available; // 是否可用,如果不可用将不会添加给用户

    @Column(columnDefinition="varchar(30)")
    private String createTime;
    @Column(columnDefinition="bigint")
    private Long createUserId;
    @Column(columnDefinition="varchar(30) ")
    private String modifyTime;
    @Column(columnDefinition="bigint")
    private Long modifyUserId;

//    /**
//     * 权限列表
//     */
//    @ManyToMany(fetch = FetchType.EAGER)//立即从数据库中进行加载数据
//    @JoinTable(name = "role_permission",
//            joinColumns = {@JoinColumn(name = "roleid")},
//            inverseJoinColumns = {@JoinColumn(name = "permissionid")})
//    @TableField(exist = false)
//    private List<Permission> permissions;

}