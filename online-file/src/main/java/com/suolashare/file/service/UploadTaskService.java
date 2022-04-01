package com.suolashare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suolashare.file.api.IUploadTaskDetailService;
import com.suolashare.file.api.IUploadTaskService;
import com.suolashare.file.domain.UploadTask;
import com.suolashare.file.domain.UploadTaskDetail;
import com.suolashare.file.mapper.UploadTaskDetailMapper;
import com.suolashare.file.mapper.UploadTaskMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UploadTaskService extends ServiceImpl<UploadTaskMapper, UploadTask> implements IUploadTaskService {


}
