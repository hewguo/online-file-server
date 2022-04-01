package com.suolashare.file.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suolashare.file.api.IShareFileService;
import com.suolashare.file.api.IShareService;
import com.suolashare.file.domain.Share;
import com.suolashare.file.domain.ShareFile;
import com.suolashare.file.domain.UserFile;
import com.suolashare.file.mapper.ShareFileMapper;
import com.suolashare.file.mapper.ShareMapper;
import com.suolashare.file.mapper.UserFileMapper;
import com.suolashare.file.vo.share.ShareFileListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class ShareFileService extends ServiceImpl<ShareFileMapper, ShareFile> implements IShareFileService {
    @Resource
    ShareFileMapper shareFileMapper;
    @Resource
    UserFileMapper userFileMapper;
    @Override
    public void batchInsertShareFile(List<ShareFile> shareFiles) {
        shareFileMapper.batchInsertShareFile(shareFiles);
    }

    @Override
    public List<ShareFileListVO> selectShareFileList(String shareBatchNum, String filePath) {
        return shareFileMapper.selectShareFileList(shareBatchNum, filePath);
    }

}
