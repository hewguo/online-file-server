package com.suolashare.file.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.suolashare.common.exception.NotLoginException;
import com.suolashare.common.result.RestResult;
import com.suolashare.common.util.DateUtil;
import com.suolashare.common.util.security.JwtUser;
import com.suolashare.common.util.security.SessionUtil;
import com.suolashare.file.api.IFileService;
import com.suolashare.file.api.IUserFileService;
import com.suolashare.file.api.IUserService;
import com.suolashare.file.domain.FileBean;
import com.suolashare.file.domain.UserFile;
import com.suolashare.file.dto.file.CreateOfficeFileDTO;
import com.suolashare.file.dto.file.EditOfficeFileDTO;
import com.suolashare.file.dto.file.PreviewOfficeFileDTO;
import com.suolashare.file.helper.ConfigManager;
import com.suolashare.file.util.FileModel;
import com.suolashare.ufop.factory.UFOPFactory;
import com.suolashare.ufop.operation.copy.Copier;
import com.suolashare.ufop.operation.copy.domain.CopyFile;
import com.suolashare.ufop.operation.download.domain.DownloadFile;
import com.suolashare.ufop.operation.write.Writer;
import com.suolashare.ufop.operation.write.domain.WriteFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Tag(name = "office", description = "该接口为Onlyoffice文件操作接口，主要用来做一些文档的编辑，浏览等。")
@RestController
@Slf4j
@RequestMapping({"/office"})
public class OfficeController {
    public static final String CURRENT_MODULE = "Onlyoffice文件操作接口";
    @Resource
    IUserService userService;
    @Resource
    UFOPFactory ufopFactory;

    @Value("${deployment.host}")
    private String deploymentHost;
    @Value("${server.port}")
    private String port;
    @Value("${ufop.storage-type}")
    private Integer storageType;


    @Resource
    IFileService fileService;
    @Resource
    IUserFileService userFileService;

    @Operation(summary = "创建office文件", description = "创建office文件", tags = {"office"})
    @ResponseBody
    @RequestMapping(value = "/createofficefile", method = RequestMethod.POST)
    public RestResult<Object> createOfficeFile(@RequestBody CreateOfficeFileDTO createOfficeFileDTO) {
        RestResult<Object> result = new RestResult<>();
        try{

            JwtUser loginUser = SessionUtil.getSession();
            String fileName = createOfficeFileDTO.getFileName();
            String filePath = createOfficeFileDTO.getFilePath();
            String extendName = createOfficeFileDTO.getExtendName();
            List<UserFile> userFiles = userFileService.selectSameUserFile(fileName, filePath, extendName, loginUser.getUserId());
            if (userFiles != null && !userFiles.isEmpty()) {
                return RestResult.fail().message("同名文件已存在");
            }
            String uuid = UUID.randomUUID().toString().replaceAll("-","");

            String templateFilePath = "";
            if ("docx".equals(extendName)) {
                templateFilePath = "template/Word.docx";
            } else if ("xlsx".equals(extendName)) {
                templateFilePath = "template/Excel.xlsx";
            } else if ("pptx".equals(extendName)) {
                templateFilePath = "template/PowerPoint.pptx";
            }
            String url2 = ClassUtils.getDefaultClassLoader().getResource("static/" + templateFilePath).getPath();
            FileInputStream fileInputStream = new FileInputStream(url2);
            Copier copier = ufopFactory.getCopier();
            CopyFile copyFile = new CopyFile();
            copyFile.setExtendName(extendName);
            String fileUrl = copier.copy(fileInputStream, copyFile);

            FileBean fileBean = new FileBean();
            fileBean.setFileSize(0L);
            fileBean.setFileUrl(fileUrl);
            fileBean.setStorageType(storageType);
            fileBean.setIdentifier(uuid);
            fileBean.setCreateTime(DateUtil.getCurrentTime());
            fileBean.setCreateUserId(loginUser.getUserId());
            fileBean.setFileStatus(1);
            boolean saveFlag = fileService.save(fileBean);
            UserFile userFile = new UserFile();
            if(saveFlag) {
                userFile.setUserId(loginUser.getUserId());
                userFile.setFileName(fileName);
                userFile.setFilePath(filePath);
                userFile.setDeleteFlag(0);
                userFile.setIsDir(0);
                userFile.setExtendName(extendName);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFile.setFileId(fileBean.getFileid());
                userFileService.save(userFile);
            }

            result.success();
            result.setMessage("文件创建成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setCode(500);
            result.setMessage("服务器错误！");
        }
        return result;
    }

    @Operation(summary = "预览office文件", description = "预览office文件", tags = {"office"})
    @RequestMapping(value = "/previewofficefile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<Object> previewOfficeFile(HttpServletRequest request, @RequestBody PreviewOfficeFileDTO previewOfficeFileDTO, @RequestHeader("token") String token) {
        RestResult<Object> result = new RestResult<>();
        try {

            JwtUser loginUser = SessionUtil.getSession();
            UserFile userFile = userFileService.getById(previewOfficeFileDTO.getUserFileId());

            String baseUrl = request.getScheme()+"://"+ deploymentHost + ":" + port + request.getContextPath();

            FileModel file = new FileModel(userFile.getFileName() + "." + userFile.getExtendName(),
                    previewOfficeFileDTO.getPreviewUrl(),
                    userFile.getUploadTime(),
                    String.valueOf(loginUser.getUserId()),
                    loginUser.getUsername(),
                    "view");

            String query = "?type=show&token="+token;
            file.editorConfig.callbackUrl= baseUrl + "/office/IndexServlet" + query;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file",file);
            jsonObject.put("docserviceApiUrl", ConfigManager.GetProperty("files.docservice.url.site") + ConfigManager.GetProperty("files.docservice.url.api"));
            jsonObject.put("reportName",userFile.getFileName());
            result.setData(jsonObject);
            result.setCode(200);
            result.setMessage("获取报告成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setCode(500);
            result.setMessage("服务器错误！");
        }
        return result;
    }
    @Operation(summary = "编辑office文件", description = "编辑office文件", tags = {"office"})
    @ResponseBody
    @RequestMapping(value = "/editofficefile", method = RequestMethod.POST)
    public RestResult<Object> editOfficeFile(HttpServletRequest request, @RequestBody EditOfficeFileDTO editOfficeFileDTO, @RequestHeader("token") String token) {
        RestResult<Object> result = new RestResult<>();
        log.info("editOfficeFile");
        try {

            JwtUser loginUser = SessionUtil.getSession();
            UserFile userFile = userFileService.getById(editOfficeFileDTO.getUserFileId());

            String baseUrl = request.getScheme()+"://"+ deploymentHost + ":" + port + request.getContextPath();

            log.info("回调地址baseUrl：" + baseUrl);

            FileModel file = new FileModel(userFile.getFileName() + "." + userFile.getExtendName(),
                    editOfficeFileDTO.getPreviewUrl(),
                    userFile.getUploadTime(),
                    String.valueOf(loginUser.getUserId()),
                    loginUser.getUsername(),
                    "edit");
            file.changeType(request.getParameter("mode"), "edit");

            String query = "?type=edit&userFileId="+userFile.getUserfileid()+"&token="+token;
            file.editorConfig.callbackUrl= baseUrl + "/office/IndexServlet" + query;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file",file);
            jsonObject.put("docserviceApiUrl",ConfigManager.GetProperty("files.docservice.url.site") + ConfigManager.GetProperty("files.docservice.url.api"));
            jsonObject.put("reportName",userFile.getFileName());
            result.setData(jsonObject);
            result.setCode(200);
            result.setMessage("编辑报告成功！");
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setCode(500);
            result.setMessage("服务器错误！");
        }
        return result;
    }


    @RequestMapping(value = "/IndexServlet", method = RequestMethod.POST)
    @ResponseBody
    public void IndexServlet(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String token = request.getParameter("token");
        Long userId = userService.getUserIdByToken(token);
        if (userId == null) {
            throw new NotLoginException();
        }

        PrintWriter writer = response.getWriter();
        Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A");
        String body = scanner.hasNext() ? scanner.next() : "";

        JSONObject jsonObj = JSON.parseObject(body);
        log.info("===saveeditedfile:" + jsonObj.get("status")); ;
        String status = jsonObj != null ? jsonObj.get("status").toString() : "";
        if ("2".equals(status) || "6".equals(status)) {
            String type = request.getParameter("type");
            String downloadUri = (String) jsonObj.get("url");

            if("edit".equals(type)){//修改报告
                String userFileId = request.getParameter("userFileId");
                UserFile userFile = userFileService.getById(Integer.valueOf(userFileId).intValue());
                FileBean fileBean = fileService.getById(userFile.getFileId());
                Long pointCount = fileService.getFilePointCount(userFile.getFileId());
                if (pointCount > 1) {
                    //该场景，暂不支持编辑修改
                    writer.write("{\"error\":1}");
                    return ;
                }

                URL url = new URL(downloadUri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                int fileLength = 0;
                try {
                    InputStream stream = connection.getInputStream();

                    Writer writer1 = ufopFactory.getWriter(fileBean.getStorageType());
                    WriteFile writeFile = new WriteFile();
                    writeFile.setFileUrl(fileBean.getFileUrl());

                    writeFile.setFileSize(connection.getContentLength());
                    writer1.write(stream, writeFile);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    if ("2".equals(status)) {
                        LambdaUpdateWrapper<UserFile> userFileUpdateWrapper = new LambdaUpdateWrapper<>();
                        userFileUpdateWrapper
                                .set(UserFile::getUploadTime, DateUtil.getCurrentTime())
                                .eq(UserFile::getUserfileid, Long.parseLong(userFileId));
                        userFileService.update(userFileUpdateWrapper);
                    }
                    LambdaUpdateWrapper<FileBean> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    fileLength = connection.getContentLength();
                    log.info("当前修改文件大小为：" + Long.valueOf(fileLength));

                    DownloadFile downloadFile = new DownloadFile();
                    downloadFile.setFileUrl(fileBean.getFileUrl());
                    InputStream inputStream = ufopFactory.getDownloader(fileBean.getStorageType()).getInputStream(downloadFile);
                    String md5Str = DigestUtils.md5Hex(inputStream);
                    lambdaUpdateWrapper
                            .set(FileBean::getIdentifier, md5Str)
                            .set(FileBean::getFileSize, Long.valueOf(fileLength))
                            .set(FileBean::getModifyTime, DateUtil.getCurrentTime())
                            .set(FileBean::getModifyUserId, userId)
                            .eq(FileBean::getFileid, fileBean.getFileid());
                    fileService.update(lambdaUpdateWrapper);

                    connection.disconnect();
                }
            }
        }

        if("3".equals(status)||"7".equals(status)) {//不强制手动保存时为6,"6".equals(status)
            log.debug("====保存失败:");
            writer.write("{\"error\":1}");
        }else {
            log.debug("状态为：0") ;
            writer.write("{\"error\":" + "0" + "}");
        }
    }

}