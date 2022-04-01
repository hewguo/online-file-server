package com.suolashare.file.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suolashare.common.exception.OnlineException;
import com.suolashare.common.operation.FileOperation;
import com.suolashare.file.api.IFileService;
import com.suolashare.file.component.AsyncTaskComp;
import com.suolashare.file.domain.FileBean;
import com.suolashare.file.domain.UserFile;
import com.suolashare.file.mapper.FileMapper;
import com.suolashare.file.mapper.UserFileMapper;
import com.suolashare.file.util.OnlineFileUtil;
import com.suolashare.ufop.factory.UFOPFactory;
import com.suolashare.ufop.operation.download.Downloader;
import com.suolashare.ufop.operation.download.domain.DownloadFile;
import com.suolashare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FileService extends ServiceImpl<FileMapper, FileBean> implements IFileService {
    public static Executor executor = Executors.newFixedThreadPool(20);
    @Resource
    FileMapper fileMapper;
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    UFOPFactory ufopFactory;

    @Value("${ufop.storage-type}")
    private Integer storageType;

    @Resource
    AsyncTaskComp asyncTaskComp;

    @Override
    public Long getFilePointCount(Long fileId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileId, fileId);
        long count = userFileMapper.selectCount(lambdaQueryWrapper);
        return count;
    }

    @Override
    public void unzipFile(long userFileId, int unzipMode, String filePath) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        FileBean fileBean = fileMapper.selectById(userFile.getFileId());
        File destFile = new File(UFOPUtils.getStaticPath() + "temp" + File.separator + fileBean.getFileUrl());


        Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFileUrl(fileBean.getFileUrl());
        downloadFile.setFileSize(fileBean.getFileSize());
        InputStream inputStream = downloader.getInputStream(downloadFile);

        try {
            FileUtils.copyInputStreamToFile(inputStream, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        String extendName = userFile.getExtendName();

        String unzipUrl = UFOPUtils.getTempFile(fileBean.getFileUrl()).getAbsolutePath().replace("." + extendName, "");

        List<String> fileEntryNameList = new ArrayList<>();
        if ("zip".equals(extendName)) {
            fileEntryNameList = FileOperation.unzip(destFile, unzipUrl);
        } else if ("rar".equals(extendName)) {
            try {
                fileEntryNameList = FileOperation.unrar(destFile, unzipUrl);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("rar解压失败" + e);
                throw new OnlineException(500001, "rar解压异常：" + e.getMessage());
            }
        } else {
            throw new OnlineException(500002, "不支持的文件格式！");
        }
        if (destFile.exists()) {
            destFile.delete();
        }

        if (!fileEntryNameList.isEmpty() && unzipMode == 1) {
            UserFile onlineDir = OnlineFileUtil.getOnlineDir(userFile.getUserId(), userFile.getFilePath(), userFile.getFileName());
            userFileMapper.insert(onlineDir);
        }
        for (int i = 0; i < fileEntryNameList.size(); i++){
            String entryName = fileEntryNameList.get(i);
            asyncTaskComp.saveUnzipFile(userFile, fileBean, unzipMode, entryName, filePath);

        }
    }


}
