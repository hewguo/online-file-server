package com.suolashare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suolashare.file.domain.Share;
import com.suolashare.file.domain.ShareFile;
import com.suolashare.file.vo.share.ShareFileListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShareFileMapper extends BaseMapper<ShareFile> {
    void batchInsertShareFile(List<ShareFile> shareFiles);
    List<ShareFileListVO> selectShareFileList(@Param("shareBatchNum") String shareBatchNum, @Param("shareFilePath") String filePath);
}
