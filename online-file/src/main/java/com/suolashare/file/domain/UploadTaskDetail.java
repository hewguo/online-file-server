package com.suolashare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "uploadtaskdetail")
@Entity
@TableName("uploadtaskdetail")
public class UploadTaskDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint(20)")
    private Long uploadtaskdetailid;

    @Column(columnDefinition="varchar(500) ")
    private String filePath;

    @Column(columnDefinition="varchar(100) ")
    private String filename;

    @Column(columnDefinition="int ")
    private int chunkNumber;

    @Column(columnDefinition="bigint")
    private Integer chunkSize;
    @Column(columnDefinition="varchar(500) ")
    private String relativePath;

    @Column(columnDefinition="int")
    private Integer totalChunks;
    @Column(columnDefinition="bigint")
    private Integer totalSize;

    @Column(columnDefinition="varchar(32) ")
    private String identifier;
}
