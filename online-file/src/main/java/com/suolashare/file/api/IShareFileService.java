package com.suolashare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suolashare.file.domain.Share;
import com.suolashare.file.domain.ShareFile;
import com.suolashare.file.vo.share.ShareFileListVO;

import java.util.List;

public interface IShareFileService extends IService<ShareFile> {
    void batchInsertShareFile(List<ShareFile> shareFiles);
    List<ShareFileListVO> selectShareFileList(String shareBatchNum, String filePath);
}
