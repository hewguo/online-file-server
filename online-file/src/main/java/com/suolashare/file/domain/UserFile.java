package com.suolashare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "userfile", uniqueConstraints = {
        @UniqueConstraint(name = "fileindex", columnNames = { "userId", "filePath", "fileName", "extendName", "deleteFlag"})}
)
@Entity
@TableName("userfile")
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition = "bigint(20)")
    private Long userfileid;

    @Column(columnDefinition = "bigint")
    private Long userId;

    @Column(columnDefinition="bigint")
    private Long fileId;

    @Column(columnDefinition="varchar(100)")
    private String fileName;

    @Column(columnDefinition="varchar(500) ")
    private String filePath;

    @Column(columnDefinition="varchar(100) ")
    private String extendName;

    @Column(columnDefinition="int")
    private Integer isDir;

    @Column(columnDefinition="varchar(25) ")
    private String uploadTime;

    @Column(columnDefinition="int ")
    private Integer deleteFlag;

    @Column(columnDefinition="varchar(25) ")
    private String deleteTime;

    @Column(columnDefinition = "varchar(50) ")
    private String deleteBatchNum;

}
