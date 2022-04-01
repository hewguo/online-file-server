package com.suolashare.file.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * 用户基础信息类
 *
 * @author ma116
 */
@Data
@Table(name = "userinfo")
@Entity
@TableName("userinfo")
public class UserBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigint")
    @TableId(type = IdType.AUTO)
    private Long userid;

    @Column(columnDefinition = "varchar(30) ")
    private String username;

    @Column(columnDefinition = "varchar(35) ")
    private String password;

    @Column(columnDefinition = "varchar(15) ")
    private String telephone;

    @Column(columnDefinition = "varchar(100) ")
    private String email;

    @Column(columnDefinition = "varchar(3) ")
    private String sex;

    @Column(columnDefinition = "varchar(30) ")
    private String birthday;

    @Column(columnDefinition = "varchar(10) ")
    private String addrProvince;

    @Column(columnDefinition = "varchar(10) ")
    private String addrCity;

    @Column(columnDefinition = "varchar(10) ")
    private String addrArea;

    @Column(columnDefinition = "varchar(50) ")
    private String industry;

    @Column(columnDefinition = "varchar(50) ")
    private String position;

    @Column(columnDefinition = "varchar(5000) ")
    private String intro;

    @Column(columnDefinition = "varchar(20) ")
    private String salt;

    @Column(columnDefinition = "varchar(100) ")
    private String imageUrl;

    @Column(columnDefinition = "varchar(30) ")
    private String registerTime;

    @Column(columnDefinition = "varchar(30) ")
    private String lastLoginTime;

    @Column(columnDefinition = "int ")
    private Integer available;
    @Column(columnDefinition = "varchar(30) ")
    private String modifyTime;
    @Column(columnDefinition = "bigint")
    private Long modifyUserId;


}
