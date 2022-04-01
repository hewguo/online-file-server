package com.suolashare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "sharefile")
@Entity
@TableName("sharefile")
public class ShareFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long sharefileid;

    @Column(columnDefinition="varchar(50) ")
    private String shareBatchNum;
    @Column(columnDefinition="bigint")
    private Long userFileId;
    @Column(columnDefinition="varchar(100) ")
    private String shareFilePath;

}
