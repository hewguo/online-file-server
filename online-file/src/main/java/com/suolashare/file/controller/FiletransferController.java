package com.suolashare.file.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.suolashare.common.anno.MyLog;
import com.suolashare.common.result.RestResult;
import com.suolashare.common.util.MimeUtils;
import com.suolashare.common.util.security.JwtUser;
import com.suolashare.common.util.security.SessionUtil;
import com.suolashare.file.api.*;
import com.suolashare.file.component.FileDealComp;
import com.suolashare.file.domain.FileBean;
import com.suolashare.file.domain.Image;
import com.suolashare.file.domain.StorageBean;
import com.suolashare.file.domain.UserFile;
import com.suolashare.file.dto.file.DownloadFileDTO;
import com.suolashare.file.dto.file.PreviewDTO;
import com.suolashare.file.dto.file.UploadFileDTO;
import com.suolashare.file.mapper.ImageMapper;
import com.suolashare.file.service.StorageService;
import com.suolashare.file.vo.file.UploadFileVo;
import com.suolashare.ufop.factory.UFOPFactory;
import com.suolashare.ufop.operation.download.Downloader;
import com.suolashare.ufop.operation.download.domain.DownloadFile;
import com.suolashare.ufop.util.UFOPUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@Tag(name = "filetransfer", description = "该接口为文件传输接口，主要用来做文件的上传、下载和预览")
@RestController
@RequestMapping("/filetransfer")
public class FiletransferController {

    @Resource
    IFiletransferService filetransferService;

    @Resource
    IFileService fileService;
    @Resource
    IUserService userService;
    @Resource
    IUserFileService userFileService;
    @Resource
    FileDealComp fileDealComp;
    @Resource
    StorageService storageService;
    @Resource
    IUploadTaskService uploadTaskService;
    @Resource
    ImageMapper imageMapper;
    @Resource
    UFOPFactory ufopFactory;

    @Resource
    IUploadTaskDetailService uploadTaskDetailService;

    public static final String CURRENT_MODULE = "文件传输接口";

    @Operation(summary = "极速上传", description = "校验文件MD5判断文件是否存在，如果存在直接上传成功并返回skipUpload=true，如果不存在返回skipUpload=false需要再次调用该接口的POST方法", tags = {"filetransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.GET)
    @MyLog(operation = "极速上传", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFileSpeed(UploadFileDTO uploadFileDto) {

        JwtUser sessionUserBean = SessionUtil.getSession();

        boolean isCheckSuccess = storageService.checkStorage(sessionUserBean.getUserId(), uploadFileDto.getTotalSize());
        if (!isCheckSuccess) {
            return RestResult.fail().message("存储空间不足");
        }
        UploadFileVo uploadFileVo = filetransferService.uploadFileSpeed(uploadFileDto);
        return RestResult.success().data(uploadFileVo);

    }

    @Operation(summary = "上传文件", description = "真正的上传文件接口", tags = {"filetransfer"})
    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    @MyLog(operation = "上传文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<UploadFileVo> uploadFile(HttpServletRequest request, UploadFileDTO uploadFileDto) {

        JwtUser sessionUserBean = SessionUtil.getSession();

        filetransferService.uploadFile(request, uploadFileDto, sessionUserBean.getUserId());

        UploadFileVo uploadFileVo = new UploadFileVo();
        return RestResult.success().data(uploadFileVo);

    }


    @Operation(summary = "下载文件", description = "下载文件接口", tags = {"filetransfer"})
    @MyLog(operation = "下载文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/downloadfile", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, DownloadFileDTO downloadFileDTO) {
        Cookie[] cookieArr = httpServletRequest.getCookies();
        String token = "";
        if (cookieArr != null) {
            for (Cookie cookie : cookieArr) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }
        boolean authResult = fileDealComp.checkAuthDownloadAndPreview(downloadFileDTO.getShareBatchNum(),
                downloadFileDTO.getExtractionCode(),
                token,
                downloadFileDTO.getUserFileId(), null);
        if (!authResult) {
            log.error("没有权限下载！！！");
            return;
        }
        httpServletResponse.setContentType("application/force-download");// 设置强制下载不打开
        UserFile userFile = userFileService.getById(downloadFileDTO.getUserFileId());
        String fileName = "";
        if (userFile.getIsDir() == 1) {
            fileName = userFile.getFileName() + ".zip";
        } else {
            fileName = userFile.getFileName() + "." + userFile.getExtendName();

        }
        try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpServletResponse.addHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名

        filetransferService.downloadFile(httpServletResponse, downloadFileDTO);
    }

    @Operation(summary="预览文件", description="用于文件预览", tags = {"filetransfer"})
    @GetMapping("/preview")
    public void preview(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,  PreviewDTO previewDTO){

        if (previewDTO.getPlatform() != null && previewDTO.getPlatform() == 2) {
            filetransferService.previewPictureFile(httpServletResponse, previewDTO);
            return ;
        }
        String token = "";
        if (StringUtils.isNotEmpty(previewDTO.getToken())) {
            token = previewDTO.getToken();
        } else {
            Cookie[] cookieArr = httpServletRequest.getCookies();
            for (Cookie cookie : cookieArr) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        UserFile userFile = userFileService.getById(previewDTO.getUserFileId());
        boolean authResult = fileDealComp.checkAuthDownloadAndPreview(previewDTO.getShareBatchNum(),
                previewDTO.getExtractionCode(),
                token,
                previewDTO.getUserFileId(),
                previewDTO.getPlatform());

        if (!authResult) {
            log.error("没有权限预览！！！");
            return;
        }

        FileBean fileBean = fileService.getById(userFile.getFileId());

        String mime= MimeUtils.getMime(userFile.getExtendName());
        httpServletResponse.setHeader("Content-Type", mime);
        String rangeString = httpServletRequest.getHeader("Range");//如果是video标签发起的请求就不会为null
        if (StringUtils.isNotEmpty(rangeString)) {
            long range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
            httpServletResponse.setContentLength(Math.toIntExact(fileBean.getFileSize()));
            httpServletResponse.setHeader("Content-Range", String.valueOf(range + (Math.toIntExact(fileBean.getFileSize()) - 1)));
        }
        httpServletResponse.setHeader("Accept-Ranges", "bytes");

        String fileName = userFile.getFileName() + "." + userFile.getExtendName();
        try {
            fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        httpServletResponse.addHeader("Content-Disposition", "fileName=" + fileName);// 设置文件名

        filetransferService.previewFile(httpServletResponse, previewDTO);

    }

    @Operation(summary = "获取存储信息", description = "获取存储信息", tags = {"filetransfer"})
    @RequestMapping(value = "/getstorage", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<StorageBean> getStorage() {

        JwtUser sessionUserBean = SessionUtil.getSession();
        StorageBean storageBean = new StorageBean();

        storageBean.setUserId(sessionUserBean.getUserId());


        Long storageSize = filetransferService.selectStorageSizeByUserId(sessionUserBean.getUserId());
        StorageBean storage = new StorageBean();
        storage.setUserId(sessionUserBean.getUserId());
        storage.setStorageSize(storageSize);
        Long totalStorageSize = storageService.getTotalStorageSize(sessionUserBean.getUserId());
        storage.setTotalStorageSize(totalStorageSize);
        return RestResult.success().data(storage);

    }


}
