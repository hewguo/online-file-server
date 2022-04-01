package com.suolashare.file.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.*;

/**
 * 存储信息类
 */
@Data
@Table(name = "storage")
@Entity
@TableName("storage")
public class StorageBean {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition="bigint")
    @TableId(type = IdType.AUTO)
    private Long storageid;

    @Column(columnDefinition="bigint")
    private Long userId;

    @Column(columnDefinition="bigint")
    private Long storageSize;

    @Column(columnDefinition="bigint")
    private Long totalStorageSize;

    @Column(columnDefinition="varchar(25)")
    private String modifyTime;
    @Column(columnDefinition="bigint")
    private Long modifyUserId;

    public StorageBean() {

    }

    public StorageBean(long userId) {
        this.userId = userId;
    }

}
