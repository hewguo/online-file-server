package com.suolashare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suolashare.file.domain.RecoveryFile;
import com.suolashare.file.vo.file.RecoveryFileListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface RecoveryFileMapper extends BaseMapper<RecoveryFile> {
    List<RecoveryFileListVo> selectRecoveryFileList(@Param("userId") Long userId);
}
