package com.suolashare.file.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.suolashare.common.util.DateUtil;
import com.suolashare.common.util.MimeUtils;
import com.suolashare.common.util.security.JwtUser;
import com.suolashare.common.util.security.SessionUtil;
import com.suolashare.file.api.IFiletransferService;
import com.suolashare.file.component.FileDealComp;
import com.suolashare.file.domain.*;
import com.suolashare.file.dto.file.DownloadFileDTO;
import com.suolashare.file.dto.file.PreviewDTO;
import com.suolashare.file.dto.file.UploadFileDTO;
import com.suolashare.file.mapper.*;
import com.suolashare.file.vo.file.UploadFileVo;
import com.suolashare.ufop.constant.StorageTypeEnum;
import com.suolashare.ufop.constant.UploadFileStatusEnum;
import com.suolashare.ufop.exception.operation.DownloadException;
import com.suolashare.ufop.exception.operation.UploadException;
import com.suolashare.ufop.factory.UFOPFactory;
import com.suolashare.ufop.operation.delete.Deleter;
import com.suolashare.ufop.operation.delete.domain.DeleteFile;
import com.suolashare.ufop.operation.download.Downloader;
import com.suolashare.ufop.operation.download.domain.DownloadFile;
import com.suolashare.ufop.operation.preview.Previewer;
import com.suolashare.ufop.operation.preview.domain.PreviewFile;
import com.suolashare.ufop.operation.upload.Uploader;
import com.suolashare.ufop.operation.upload.domain.UploadFile;
import com.suolashare.ufop.operation.upload.domain.UploadFileResult;
import com.suolashare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class FiletransferService implements IFiletransferService {

    @Resource
    FileMapper fileMapper;

    @Resource
    UserFileMapper userFileMapper;

    @Resource
    UFOPFactory ufopFactory;
    @Resource
    FileDealComp fileDealComp;
    @Resource
    UploadTaskDetailMapper uploadTaskDetailMapper;
    @Resource
    UploadTaskMapper uploadTaskMapper;
    @Resource
    ImageMapper imageMapper;

    @Resource
    PictureFileMapper pictureFileMapper;


    @Override
    public UploadFileVo uploadFileSpeed(UploadFileDTO uploadFileDTO) {
        UploadFileVo uploadFileVo = new UploadFileVo();
        JwtUser sessionUserBean = SessionUtil.getSession();
        Map<String, Object> param = new HashMap<>();
        param.put("identifier", uploadFileDTO.getIdentifier());
        List<FileBean> list = fileMapper.selectByMap(param);

        if (list != null && !list.isEmpty()) {
            FileBean file = list.get(0);

            UserFile userFile = new UserFile();

            userFile.setUserId(sessionUserBean.getUserId());
            String relativePath = uploadFileDTO.getRelativePath();
            if (relativePath.contains("/")) {
                userFile.setFilePath(uploadFileDTO.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                fileDealComp.restoreParentFilePath(uploadFileDTO.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/", sessionUserBean.getUserId());
                fileDealComp.deleteRepeatSubDirFile(uploadFileDTO.getFilePath(), sessionUserBean.getUserId());
            } else {
                userFile.setFilePath(uploadFileDTO.getFilePath());
            }

            String fileName = uploadFileDTO.getFilename();
            userFile.setFileName(UFOPUtils.getFileNameNotExtend(fileName));
            userFile.setExtendName(UFOPUtils.getFileExtendName(fileName));
            userFile.setDeleteFlag(0);

            List<UserFile> userFileList = userFileMapper.selectList(new QueryWrapper<>(userFile));
            if (userFileList.size() <= 0) {

                userFile.setIsDir(0);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFile.setFileId(file.getFileid());
                userFileMapper.insert(userFile);
                fileDealComp.uploadESByUserFileId(userFile.getUserfileid());
            }

            uploadFileVo.setSkipUpload(true);

        } else {
            uploadFileVo.setSkipUpload(false);

            List<Integer> uploaded = uploadTaskDetailMapper.selectUploadedChunkNumList(uploadFileDTO.getIdentifier());
            if (uploaded != null && !uploaded.isEmpty()) {
                uploadFileVo.setUploaded(uploaded);
            } else {

                LambdaQueryWrapper<UploadTask> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTask::getIdentifier, uploadFileDTO.getIdentifier());
                List<UploadTask> rslist = uploadTaskMapper.selectList(lambdaQueryWrapper);
                if (rslist == null || rslist.isEmpty()) {
                    UploadTask uploadTask = new UploadTask();
                    uploadTask.setIdentifier(uploadFileDTO.getIdentifier());
                    uploadTask.setUploadTime(DateUtil.getCurrentTime());
                    uploadTask.setUploadStatus(UploadFileStatusEnum.UNCOMPLATE.getCode());
                    uploadTask.setFileName(uploadFileDTO.getFilename());
                    String relativePath = uploadFileDTO.getRelativePath();
                    if (relativePath.contains("/")) {
                        uploadTask.setFilePath(uploadFileDTO.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                    } else {
                        uploadTask.setFilePath(uploadFileDTO.getFilePath());
                    }
                    uploadTask.setExtendName(uploadTask.getExtendName());
                    uploadTask.setUserId(sessionUserBean.getUserId());
                    uploadTaskMapper.insert(uploadTask);
                }
            }

        }
        return uploadFileVo;
    }

    @Override
    public void uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto, Long userId) {

        UploadFile uploadFile = new UploadFile();
        uploadFile.setChunkNumber(uploadFileDto.getChunkNumber());
        uploadFile.setChunkSize(uploadFileDto.getChunkSize());
        uploadFile.setTotalChunks(uploadFileDto.getTotalChunks());
        uploadFile.setIdentifier(uploadFileDto.getIdentifier());
        uploadFile.setTotalSize(uploadFileDto.getTotalSize());
        uploadFile.setCurrentChunkSize(uploadFileDto.getCurrentChunkSize());

        Uploader uploader = ufopFactory.getUploader();
        if (uploader == null) {
            log.error("上传失败，请检查storageType是否配置正确");
            throw new UploadException("上传失败");
        }
        List<UploadFileResult> uploadFileResultList;
        try {
            uploadFileResultList = uploader.upload(request, uploadFile);
        } catch (Exception e) {
            log.error("上传失败，请检查UFOP连接配置是否正确");
            throw new UploadException("上传失败");
        }
        for (int i = 0; i < uploadFileResultList.size(); i++){
            UploadFileResult uploadFileResult = uploadFileResultList.get(i);
            FileBean fileBean = new FileBean();
            BeanUtil.copyProperties(uploadFileDto, fileBean);
            String relativePath = uploadFileDto.getRelativePath();

            if (UploadFileStatusEnum.SUCCESS.equals(uploadFileResult.getStatus())){
                fileBean.setFileUrl(uploadFileResult.getFileUrl());
                fileBean.setFileSize(uploadFileResult.getFileSize());
                fileBean.setStorageType(uploadFileResult.getStorageType().getCode());
                fileBean.setFileStatus(1);
                fileBean.setCreateTime(DateUtil.getCurrentTime());
                fileBean.setCreateUserId(userId);
                fileMapper.insert(fileBean);
                UserFile userFile = new UserFile();

                if (relativePath.contains("/")) {
                    userFile.setFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                    fileDealComp.restoreParentFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/", userId);
                    fileDealComp.deleteRepeatSubDirFile(uploadFileDto.getFilePath(), userId);

                } else {
                    userFile.setFilePath(uploadFileDto.getFilePath());
                }
                userFile.setUserId(userId);
                userFile.setFileName(uploadFileResult.getFileName());
                userFile.setExtendName(uploadFileResult.getExtendName());
                userFile.setDeleteFlag(0);
                userFile.setIsDir(0);
                List<UserFile> userFileList = userFileMapper.selectList(new QueryWrapper<>(userFile));
                if (userFileList.size() > 0) {
                    String fileName = fileDealComp.getRepeatFileName(userFile, uploadFileDto.getFilePath());
                    userFile.setFileName(fileName);
                }
                userFile.setFileId(fileBean.getFileid());

                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFileMapper.insert(userFile);
                fileDealComp.uploadESByUserFileId(userFile.getUserfileid());


                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.delete(lambdaQueryWrapper);

                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.SUCCESS.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);
                try {
                    if (UFOPUtils.isImageFile(uploadFileResult.getExtendName())) {
                        BufferedImage src = uploadFileResult.getBufferedImage();
                        Image image = new Image();
                        image.setImageWidth(src.getWidth());
                        image.setImageHeight(src.getHeight());
                        image.setFileId(fileBean.getFileid());
                        imageMapper.insert(image);
                    }
                } catch (Exception e) {
                    log.error("生成图片缩略图失败！");
                }

            } else if (UploadFileStatusEnum.UNCOMPLATE.equals(uploadFileResult.getStatus())) {
                UploadTaskDetail uploadTaskDetail = new UploadTaskDetail();
                uploadTaskDetail.setFilePath(uploadFileDto.getFilePath());
                if (relativePath.contains("/")) {
                    uploadTaskDetail.setFilePath(uploadFileDto.getFilePath() + UFOPUtils.getParentPath(relativePath) + "/");
                } else {
                    uploadTaskDetail.setFilePath(uploadFileDto.getFilePath());
                }
                uploadTaskDetail.setFilename(uploadFileDto.getFilename());
                uploadTaskDetail.setChunkNumber(uploadFileDto.getChunkNumber());
                uploadTaskDetail.setChunkSize((int)uploadFileDto.getChunkSize());
                uploadTaskDetail.setRelativePath(uploadFileDto.getRelativePath());
                uploadTaskDetail.setTotalChunks(uploadFileDto.getTotalChunks());
                uploadTaskDetail.setTotalSize((int)uploadFileDto.getTotalSize());
                uploadTaskDetail.setIdentifier(uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.insert(uploadTaskDetail);

            } else if (UploadFileStatusEnum.FAIL.equals(uploadFileResult.getStatus())) {
                LambdaQueryWrapper<UploadTaskDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UploadTaskDetail::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskDetailMapper.delete(lambdaQueryWrapper);

                LambdaUpdateWrapper<UploadTask> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.set(UploadTask::getUploadStatus, UploadFileStatusEnum.FAIL.getCode())
                        .eq(UploadTask::getIdentifier, uploadFileDto.getIdentifier());
                uploadTaskMapper.update(null, lambdaUpdateWrapper);
            }
        }

    }

    @Override
    public void downloadFile(HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        UserFile userFile = userFileMapper.selectById(downloadFileDTO.getUserFileId());

        if (userFile.getIsDir() == 0) {

            FileBean fileBean = fileMapper.selectById(userFile.getFileId());
            Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
            if (downloader == null) {
                log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
                throw new DownloadException("下载失败");
            }
            DownloadFile downloadFile = new DownloadFile();

            downloadFile.setFileUrl(fileBean.getFileUrl());
            downloadFile.setFileSize(fileBean.getFileSize());
            httpServletResponse.setContentLengthLong(fileBean.getFileSize());
            downloader.download(httpServletResponse, downloadFile);
        } else {
            LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.likeRight(UserFile::getFilePath, userFile.getFilePath() + userFile.getFileName() + "/")
                    .eq(UserFile::getUserId, userFile.getUserId())
                    .eq(UserFile::getIsDir, 0)
                    .eq(UserFile::getDeleteFlag, 0);
            List<UserFile> userFileList = userFileMapper.selectList(lambdaQueryWrapper);

            String staticPath = UFOPUtils.getStaticPath();
            String tempPath = staticPath + "temp" + File.separator;
            File tempDirFile = new File(tempPath);
            if (!tempDirFile.exists()) {
                tempDirFile.mkdirs();
            }

            FileOutputStream f = null;
            try {
                f = new FileOutputStream(tempPath + userFile.getFileName() + ".zip");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            CheckedOutputStream csum = new CheckedOutputStream(f, new Adler32());
            ZipOutputStream zos = new ZipOutputStream(csum);
            BufferedOutputStream out = new BufferedOutputStream(zos);

            try {
                for (UserFile userFile1 : userFileList) {
                    FileBean fileBean = fileMapper.selectById(userFile1.getFileId());
                    Downloader downloader = ufopFactory.getDownloader(fileBean.getStorageType());
                    if (downloader == null) {
                        log.error("下载失败，文件存储类型不支持下载，storageType:{}", fileBean.getStorageType());
                        throw new UploadException("下载失败");
                    }
                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.setFileUrl(fileBean.getFileUrl());
                    downloadFile.setFileSize(fileBean.getFileSize());
                    InputStream inputStream = downloader.getInputStream(downloadFile);
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    try {
                        zos.putNextEntry(new ZipEntry(userFile1.getFilePath().replace(userFile.getFilePath(), "/") + userFile1.getFileName() + "." + userFile1.getExtendName()));

                        byte[] buffer = new byte[1024];
                        int i = bis.read(buffer);
                        while (i != -1) {
                            out.write(buffer, 0, i);
                            i = bis.read(buffer);
                        }
                    } catch (IOException e) {
                        log.error("" + e);
                        e.printStackTrace();
                    } finally {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                log.error("压缩过程中出现异常:"+ e);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String zipPath = "";
            try {
                Downloader downloader = ufopFactory.getDownloader(StorageTypeEnum.LOCAL.getCode());
                DownloadFile downloadFile = new DownloadFile();
                downloadFile.setFileUrl("temp" + File.separator + userFile.getFileName() + ".zip");
                File tempFile = new File(UFOPUtils.getStaticPath() + downloadFile.getFileUrl());
                httpServletResponse.setContentLengthLong(tempFile.length());
                downloader.download(httpServletResponse, downloadFile);
                zipPath = UFOPUtils.getStaticPath() + "temp" + File.separator + userFile.getFileName() + ".zip";
            } catch (Exception e) {
                //org.apache.catalina.connector.ClientAbortException: java.io.IOException: Connection reset by peer
                if (e.getMessage().contains("ClientAbortException")) {
                    //该异常忽略不做处理
                } else {
                    log.error("下传zip文件出现异常：{}", e.getMessage());
                }

            } finally {
                File file = new File(zipPath);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public void previewFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO) {
        UserFile userFile = userFileMapper.selectById(previewDTO.getUserFileId());
        FileBean fileBean = fileMapper.selectById(userFile.getFileId());
        Previewer previewer = ufopFactory.getPreviewer(fileBean.getStorageType());
        if (previewer == null) {
            log.error("预览失败，文件存储类型不支持预览，storageType:{}", fileBean.getStorageType());
            throw new UploadException("预览失败");
        }
        PreviewFile previewFile = new PreviewFile();
        previewFile.setFileUrl(fileBean.getFileUrl());
//        previewFile.setFileSize(fileBean.getFileSize());
        try {
            if ("true".equals(previewDTO.getIsMin())) {
                previewer.imageThumbnailPreview(httpServletResponse, previewFile);
            } else {
                previewer.imageOriginalPreview(httpServletResponse, previewFile);
            }
        } catch (Exception e){
                //org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
                if (e.getMessage().contains("ClientAbortException")) {
                //该异常忽略不做处理
            } else {
                log.error("预览文件出现异常：{}", e.getMessage());
            }

        }

    }

    @Override
    public void previewPictureFile(HttpServletResponse httpServletResponse, PreviewDTO previewDTO) {
        byte[] bytesUrl = Base64.getDecoder().decode(previewDTO.getUrl());
        PictureFile pictureFile = new PictureFile();
        pictureFile.setFileUrl(new String(bytesUrl));
        pictureFile = pictureFileMapper.selectOne(new QueryWrapper<>(pictureFile));
        Previewer previewer = ufopFactory.getPreviewer(pictureFile.getStorageType());
        if (previewer == null) {
            log.error("预览失败，文件存储类型不支持预览，storageType:{}", pictureFile.getStorageType());
            throw new UploadException("预览失败");
        }
        PreviewFile previewFile = new PreviewFile();
        previewFile.setFileUrl(pictureFile.getFileUrl());
//        previewFile.setFileSize(pictureFile.getFileSize());
        try {

            String mime= MimeUtils.getMime(pictureFile.getExtendName());
            httpServletResponse.setHeader("Content-Type", mime);

            String fileName = pictureFile.getFileName() + "." + pictureFile.getExtendName();
            try {
                fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            httpServletResponse.addHeader("Content-Disposition", "fileName=" + fileName);// 设置文件名

            previewer.imageOriginalPreview(httpServletResponse, previewFile);
        } catch (Exception e){
            //org.apache.catalina.connector.ClientAbortException: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。
            if (e.getMessage().contains("ClientAbortException")) {
                //该异常忽略不做处理
            } else {
                log.error("预览文件出现异常：{}", e.getMessage());
            }

        }
    }

    @Override
    public void deleteFile(FileBean fileBean) {
        Deleter deleter = null;

        deleter = ufopFactory.getDeleter(fileBean.getStorageType());
        DeleteFile deleteFile = new DeleteFile();
        deleteFile.setFileUrl(fileBean.getFileUrl());
        deleter.delete(deleteFile);
    }



    @Override
    public Long selectStorageSizeByUserId(Long userId){
        return userFileMapper.selectStorageSizeByUserId(userId);
    }
}
