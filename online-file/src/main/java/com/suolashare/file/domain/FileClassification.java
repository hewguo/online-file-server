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
 * @date 2021/12/23 19:48
 */
@Data
@Table(name = "fileclassification")
@Entity
@TableName("fileclassification")
public class FileClassification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Column(columnDefinition="bigint")
    private Long fileclassificationid;
    @Column(columnDefinition="bigint")
    private Integer fileTypeId;
    @Column(columnDefinition="varchar(25) ")
    private String fileExtendName;
}
