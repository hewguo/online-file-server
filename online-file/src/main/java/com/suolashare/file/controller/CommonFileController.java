package com.suolashare.file.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.suolashare.common.anno.MyLog;
import com.suolashare.common.result.RestResult;
import com.suolashare.common.util.security.JwtUser;
import com.suolashare.common.util.security.SessionUtil;
import com.suolashare.file.api.ICommonFileService;
import com.suolashare.file.api.IFilePermissionService;
import com.suolashare.file.api.IUserFileService;
import com.suolashare.file.domain.CommonFile;
import com.suolashare.file.domain.FilePermission;
import com.suolashare.file.domain.UserFile;
import com.suolashare.file.dto.commonfile.CommonFileDTO;
import com.suolashare.file.vo.commonfile.CommonFileListVo;
import com.suolashare.file.vo.commonfile.CommonFileUser;
import com.suolashare.file.vo.file.FileListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "common", description = "该接口为文件共享接口")
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonFileController {

    public static final String CURRENT_MODULE = "文件共享";

    @Resource
    ICommonFileService commonFileService;
    @Resource
    IFilePermissionService filePermissionService;
    @Resource
    IUserFileService userFileService;

    @Operation(summary = "共享文件", description = "共享文件统一接口", tags = {"common"})
    @PostMapping(value = "/commonfile")
    @MyLog(operation = "共享文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> commonFile( @RequestBody CommonFileDTO commonFileDTO) {
        CommonFile commonFile = new CommonFile();
        commonFile.setUserFileId(commonFileDTO.getUserFileId());
        commonFileService.save(commonFile);

       List<Long> list = JSON.parseArray(commonFileDTO.getCommonUserIds(), Long.class);
        List<FilePermission> filePermissionList = new ArrayList<>();
       for (Long userId : list) {
            FilePermission filePermission = new FilePermission();
            filePermission.setUserId(Long.parseLong(commonFileDTO.getCommonUserIds()));
            filePermission.setCommonFileId(commonFile.commonfileid);
            filePermission.setFilePermissionCode(commonFileDTO.getPermissionCode());
            filePermissionList.add(filePermission);
       }
        filePermissionService.saveBatch(filePermissionList);

        return RestResult.success();
    }

    @Operation(summary = "共享文件用户", description = "共享文件用户接口", tags = {"common"})
    @GetMapping(value = "/commonfileuser")
    @MyLog(operation = "共享文件用户", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<List<CommonFileUser>> commonFileUser() {

        JwtUser sessionUserBean =  SessionUtil.getSession();
        List<CommonFileUser> list = commonFileService.selectCommonFileUser(sessionUserBean.getUserId());
        return RestResult.success().data(list);
    }

    @Operation(summary = "获取共享用户文件列表", description = "用来做前台列表展示", tags = {"file"})
    @RequestMapping(value = "/getCommonFileByUser", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<CommonFileListVo> getCommonFileByUser(
            @Parameter(description = "用户id", required = true) Long userId,
            @Parameter(description = "用户文件路径", required = true) Long userFileId,
            @Parameter(description = "文件路径", required = true) String filePath,
            @Parameter(description = "当前页", required = true) long currentPage,
            @Parameter(description = "页面数量", required = true) long pageCount){

        List<CommonFileListVo> commonFileVo = commonFileService.selectCommonFileByUser(userId);

        return RestResult.success().data(commonFileVo);

    }

    @Operation(summary = "获取共享用户文件列表", description = "用来做前台列表展示", tags = {"file"})
    @RequestMapping(value = "/commonFileList", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<FileListVo> commonFileList(
            @Parameter(description = "用户id", required = true) Long commonFileId,
            @Parameter(description = "文件路径", required = true) String filePath,
            @Parameter(description = "当前页", required = true) long currentPage,
            @Parameter(description = "页面数量", required = true) long pageCount){

        CommonFile commonFile = commonFileService.getById(commonFileId);
        UserFile userFile = userFileService.getById(commonFile.getUserFileId());
        filePath = userFile.getFilePath() + filePath;
        IPage<FileListVo> fileList = userFileService.userFileList(userFile.getUserId(), filePath, currentPage, pageCount);

        return RestResult.success().data(fileList);

    }

}
