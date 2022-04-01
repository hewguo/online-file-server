package com.suolashare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Table(name = "share")
@Entity
@TableName("share")
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long shareid;
    @Column(columnDefinition="bigint")
    private Long userId;
    @Column(columnDefinition="varchar(30) ")
    private String shareTime;
    @Column(columnDefinition="varchar(30)")
    private String endTime;
    @Column(columnDefinition="varchar(10) ")
    private String extractionCode;
    @Column(columnDefinition="varchar(40) ")
    private String shareBatchNum;
    @Column(columnDefinition="int ")
    private Integer shareType;
    @Column(columnDefinition="int")
    private Integer shareStatus;

}
