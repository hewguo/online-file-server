package com.suolashare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suolashare.file.api.ICommonFileService;
import com.suolashare.file.domain.CommonFile;
import com.suolashare.file.mapper.CommonFileMapper;
import com.suolashare.file.vo.commonfile.CommonFileListVo;
import com.suolashare.file.vo.commonfile.CommonFileUser;
import com.suolashare.file.vo.file.FileListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class CommonFileService extends ServiceImpl<CommonFileMapper, CommonFile> implements ICommonFileService {

    @Resource
    CommonFileMapper commonFileMapper;

    @Override
    public List<CommonFileUser> selectCommonFileUser(Long userId) {
        return commonFileMapper.selectCommonFileUser(userId);
    }

    @Override
    public List<CommonFileListVo> selectCommonFileByUser(Long userId) {
        return commonFileMapper.selectCommonFileByUser(userId);
    }


}
