package com.suolashare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suolashare.file.domain.RecoveryFile;
import com.suolashare.file.domain.UserFile;
import com.suolashare.file.vo.file.RecoveryFileListVo;

import java.util.List;

public interface IRecoveryFileService extends IService<RecoveryFile> {
    void deleteUserFileByDeleteBatchNum(String deleteBatchNum);
    void restorefile(String deleteBatchNum, String filePath, Long sessionUserId);
    List<RecoveryFileListVo> selectRecoveryFileList(Long userId);
}
