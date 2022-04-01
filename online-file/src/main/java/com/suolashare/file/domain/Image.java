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
 * @date 2021/12/7 22:05
 */
@Data
@Table(name = "image")
@Entity
@TableName("image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint")
    private Long imageid;
    @Column(columnDefinition = "bigint")
    private Long fileId;
    @Column(columnDefinition="int")
    private Integer imageWidth;
    @Column(columnDefinition="int ")
    private Integer imageHeight;
}
