package com.suolashare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suolashare.file.domain.CommonFile;
import com.suolashare.file.vo.commonfile.CommonFileListVo;
import com.suolashare.file.vo.commonfile.CommonFileUser;
import com.suolashare.file.vo.file.FileListVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommonFileMapper extends BaseMapper<CommonFile> {
    List<CommonFileUser> selectCommonFileUser(@Param("userId") Long userId);
    List<CommonFileListVo> selectCommonFileByUser(@Param("userId") Long userId);

}
