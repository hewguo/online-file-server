package com.suolashare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suolashare.file.domain.CommonFile;
import com.suolashare.file.vo.commonfile.CommonFileListVo;
import com.suolashare.file.vo.commonfile.CommonFileUser;

import java.util.List;

public interface ICommonFileService extends IService<CommonFile> {
    List<CommonFileUser> selectCommonFileUser(Long userId);
    List<CommonFileListVo> selectCommonFileByUser(Long userId);
}