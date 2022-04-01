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
 * @date 2022/1/12 14:44
 */
@Data
@Table(name = "filepermission")
@Entity
@TableName("filepermission")
public class FilePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint")
    public Long filepermissionid;
    @Column(columnDefinition="bigint")
    public Long commonFileId;
    @Column(columnDefinition="bigint")
    public Long userId;
    @Column(columnDefinition="int ")
    public Integer filePermissionCode;

}
