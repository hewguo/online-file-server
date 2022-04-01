package com.suolashare.file.controller;

import com.alibaba.fastjson.JSON;
import com.suolashare.common.anno.MyLog;
import com.suolashare.common.result.RestResult;
import com.suolashare.common.util.security.JwtUser;
import com.suolashare.common.util.security.SessionUtil;
import com.suolashare.file.api.*;
import com.suolashare.file.component.AsyncTaskComp;
import com.suolashare.file.domain.RecoveryFile;
import com.suolashare.file.dto.file.DeleteRecoveryFileDTO;
import com.suolashare.file.dto.recoveryfile.BatchDeleteRecoveryFileDTO;
import com.suolashare.file.dto.recoveryfile.RestoreFileDTO;
import com.suolashare.file.vo.file.RecoveryFileListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Tag(name = "recoveryfile", description = "文件删除后会进入回收站，该接口主要是对回收站文件进行管理")
@RestController
@Slf4j
@RequestMapping("/recoveryfile")
public class RecoveryFileController {
    @Resource
    IRecoveryFileService recoveryFileService;
    @Resource
    IUserFileService userFileService;
    @Resource
    IUserService userService;
    @Resource
    IFileService fileService;
    @Resource
    IFiletransferService filetransferService;
    @Resource
    AsyncTaskComp asyncTaskComp;


    public static final String CURRENT_MODULE = "回收站文件接口";

    @Operation(summary = "删除回收文件", description = "删除回收文件", tags = {"recoveryfile"})
    @MyLog(operation = "删除回收文件", module = CURRENT_MODULE)
    @RequestMapping(value = "/deleterecoveryfile", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<String> deleteRecoveryFile(@RequestBody DeleteRecoveryFileDTO deleteRecoveryFileDTO) {
        JwtUser sessionUserBean = SessionUtil.getSession();
        RecoveryFile recoveryFile = recoveryFileService.getById(deleteRecoveryFileDTO.getRecoveryFileId());

        asyncTaskComp.deleteUserFile(recoveryFile.getUserFileId());

        recoveryFileService.removeById(deleteRecoveryFileDTO.getRecoveryFileId());
        return RestResult.success().data("删除成功");
    }

    @Operation(summary = "批量删除回收文件", description = "批量删除回收文件", tags = {"recoveryfile"})
    @RequestMapping(value = "/batchdelete", method = RequestMethod.POST)
    @MyLog(operation = "批量删除回收文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> batchDeleteRecoveryFile(@RequestBody BatchDeleteRecoveryFileDTO batchDeleteRecoveryFileDTO) {
        JwtUser sessionUserBean = SessionUtil.getSession();
        List<RecoveryFile> recoveryFileList = JSON.parseArray(batchDeleteRecoveryFileDTO.getRecoveryFileIds(), RecoveryFile.class);
        for (RecoveryFile recoveryFile : recoveryFileList) {
            RecoveryFile recoveryFile1 = recoveryFileService.getById(recoveryFile.getRecoverfileid());

            asyncTaskComp.deleteUserFile(recoveryFile1.getUserFileId());

            recoveryFileService.removeById(recoveryFile1.getRecoverfileid());

        }
        return RestResult.success().data("批量删除成功");
    }

    @Operation(summary = "回收文件列表", description = "回收文件列表", tags = {"recoveryfile"})
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public RestResult<List<RecoveryFileListVo>> getRecoveryFileList() {
        JwtUser sessionUserBean = SessionUtil.getSession();
        RestResult<List<RecoveryFileListVo>> restResult = new RestResult<List<RecoveryFileListVo>>();
        List<RecoveryFileListVo> recoveryFileList = recoveryFileService.selectRecoveryFileList(sessionUserBean.getUserId());
        restResult.setData(recoveryFileList);
        restResult.setSuccess(true);

        return restResult;
    }

    @Operation(summary = "还原文件", description = "还原文件", tags = {"recoveryfile"})
    @RequestMapping(value = "/restorefile", method = RequestMethod.POST)
    @MyLog(operation = "还原文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult restoreFile(@RequestBody RestoreFileDTO restoreFileDto) {
        JwtUser sessionUserBean = SessionUtil.getSession();
        recoveryFileService.restorefile(restoreFileDto.getDeleteBatchNum(), restoreFileDto.getFilePath(), sessionUserBean.getUserId());
        return RestResult.success().message("还原成功！");
    }

}









