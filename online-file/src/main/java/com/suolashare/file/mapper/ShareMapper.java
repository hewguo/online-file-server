package com.suolashare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suolashare.file.domain.Share;
import com.suolashare.file.vo.share.ShareListVO;

import java.util.List;

public interface ShareMapper  extends BaseMapper<Share> {

    List<ShareListVO> selectShareList(String shareFilePath,String shareBatchNum, Long beginCount, Long pageCount, Long userId);
    int selectShareListTotalCount(String shareFilePath,String shareBatchNum, Long userId);
}
