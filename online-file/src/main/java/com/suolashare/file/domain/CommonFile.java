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
 * @date 2022/1/12 14:41
 */
@Data
@Table(name = "commonfile")
@Entity
@TableName("commonfile")
public class CommonFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint")
    public Long commonfileid;
    @Column(columnDefinition="bigint")
    public Long userFileId;
//    @Column(columnDefinition="int(2) comment '文件权限'")
//    public Integer filePermission;
}
