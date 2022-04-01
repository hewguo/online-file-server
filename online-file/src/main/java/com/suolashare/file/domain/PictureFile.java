package com.suolashare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * @author MAC
 * @version 1.0
 * @description: TODO
 * @date 2022/1/1 19:06
 */
@Data
@Table(name = "picturefile")
@Entity
@TableName("picturefile")
public class PictureFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint")
    private Long picturefileid;

    @Column(columnDefinition="varchar(500)")
    private String fileUrl;

    @Column(columnDefinition="bigint ")
    private Long fileSize;

    @Column(columnDefinition="int ")
    private Integer storageType;

    @Column(columnDefinition = "bigint")
    private Long userId;

    @Column(columnDefinition="varchar(100)")
    private String fileName;

    @Column(columnDefinition="varchar(100)")
    private String extendName;

    @Column(columnDefinition="varchar(25)")
    private String createTime;

    @Column(columnDefinition="bigint")
    private Long createUserId;

    @Column(columnDefinition="varchar(25) ")
    private String modifyTime;

    @Column(columnDefinition="bigint")
    private Long modifyUserId;



}
