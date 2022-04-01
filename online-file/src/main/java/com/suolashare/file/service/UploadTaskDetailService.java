package com.suolashare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suolashare.file.api.IUploadTaskDetailService;
import com.suolashare.file.domain.UploadTaskDetail;
import com.suolashare.file.mapper.UploadTaskDetailMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UploadTaskDetailService extends ServiceImpl<UploadTaskDetailMapper, UploadTaskDetail> implements IUploadTaskDetailService {

    @Resource
    UploadTaskDetailMapper uploadTaskDetailMapper;

    @Override
    public List<Integer> getUploadedChunkNumList(String identifier) {
        return uploadTaskDetailMapper.selectUploadedChunkNumList(identifier);
    }
}
