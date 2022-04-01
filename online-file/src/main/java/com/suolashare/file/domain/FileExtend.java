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
 * @date 2021/12/20 20:16
 */
@Data
@Table(name = "fileextend")
@Entity
@TableName("fileextend")
public class FileExtend {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="varchar(25)")
    private String fileextendname;
    @Column(columnDefinition="varchar(25) ")
    private String fileExtendDesc;
    @Column(columnDefinition="varchar(100) ")
    private String fileExtendImgUrl;
}
