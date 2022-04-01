package com.suolashare.file.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suolashare.common.constant.FileConstant;
import com.suolashare.common.exception.OnlineException;
import com.suolashare.common.result.RestResult;
import com.suolashare.common.util.DateUtil;
import com.suolashare.common.util.security.JwtUser;
import com.suolashare.common.util.security.SessionUtil;
import com.suolashare.file.api.IUserFileService;
import com.suolashare.file.domain.RecoveryFile;
import com.suolashare.file.domain.UserFile;
import com.suolashare.file.mapper.FileMapper;
import com.suolashare.file.mapper.FileTypeMapper;
import com.suolashare.file.mapper.RecoveryFileMapper;
import com.suolashare.file.mapper.UserFileMapper;
import com.suolashare.file.vo.file.FileListVo;
import com.suolashare.ufop.util.UFOPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class UserFileService  extends ServiceImpl<UserFileMapper, UserFile> implements IUserFileService {
    @Resource
    UserFileMapper userFileMapper;
    @Resource
    FileMapper fileMapper;
    @Resource
    RecoveryFileMapper recoveryFileMapper;
    @Resource
    FileTypeMapper fileTypeMapper;

    public static Executor executor = Executors.newFixedThreadPool(20);


    @Override
    public List<UserFile> selectUserFileByNameAndPath(String fileName, String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public boolean isDirExist(String fileName, String filePath, long userId){
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0)
                .eq(UserFile::getIsDir, 1);
        List<UserFile> list = userFileMapper.selectList(lambdaQueryWrapper);
        if (list != null && !list.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public List<UserFile> selectSameUserFile(String fileName, String filePath, String extendName, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getFileName, fileName)
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getExtendName, extendName)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public IPage<FileListVo> userFileList(Long userId, String filePath, Long currentPage, Long pageCount) {
        Page<FileListVo> page = new Page<>(currentPage, pageCount);
        UserFile userFile = new UserFile();
        JwtUser sessionUserBean = SessionUtil.getSession();
        if (userId == null) {
            userFile.setUserId(sessionUserBean.getUserId());
        } else {
            userFile.setUserId(userId);
        }

        userFile.setFilePath(UFOPUtils.urlDecode(filePath));

        return userFileMapper.selectPageVo(page, userFile, null);
    }

    @Override
    public void updateFilepathByFilepath(String oldfilePath, String newfilePath, String fileName, String extendName, long userId) {
        List<UserFile> userFileList = selectUserFileListByPath(newfilePath, userId);
        List<UserFile> userFileNameList = userFileList.stream().filter(o -> o.getFileName().equals(fileName) && o.getExtendName().equals(extendName)).collect(Collectors.toList());

        if (userFileNameList != null && userFileNameList.size() > 0) {
            throw new OnlineException(200000, "目的路径同名文件已存在，不能移动");
        }

        //移动根目录
        userFileMapper.updateFilepathByPathAndName(oldfilePath, newfilePath, fileName, extendName, userId);

        //移动子目录
        oldfilePath = oldfilePath + fileName + "/";
        newfilePath = newfilePath + fileName + "/";

        oldfilePath = oldfilePath.replace("\\", "\\\\\\\\");
        oldfilePath = oldfilePath.replace("'", "\\'");
        oldfilePath = oldfilePath.replace("%", "\\%");
        oldfilePath = oldfilePath.replace("_", "\\_");

        if (StringUtils.isEmpty(extendName)) { //为空说明是目录，则需要移动子目录
            userFileMapper.updateFilepathByFilepath(oldfilePath, newfilePath, userId);
        }

    }

    @Override
    public void userFileCopy(String oldfilePath, String newfilePath, String fileName, String extendName, long userId) {



        if ("null".equals(extendName)){
            extendName = null;
        }

        userFileMapper.batchInsertByPathAndName(oldfilePath, newfilePath, fileName, extendName, userId);


        //移动子目录
        oldfilePath = oldfilePath + fileName + "/";
        newfilePath = newfilePath + fileName + "/";

        oldfilePath = oldfilePath.replace("\\", "\\\\\\\\");
        oldfilePath = oldfilePath.replace("'", "\\'");
        oldfilePath = oldfilePath.replace("%", "\\%");
        oldfilePath = oldfilePath.replace("_", "\\_");

        if (extendName == null) { //为null说明是目录，则需要移动子目录
            userFileMapper.batchInsertByFilepath(oldfilePath, newfilePath, userId);

        }

    }

    @Override
    public IPage<FileListVo> getFileByFileType(Integer fileTypeId, Long currentPage, Long pageCount, long userId) {
        Page<FileListVo> page = new Page<>(currentPage, pageCount);

        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        return userFileMapper.selectPageVo(page, userFile, fileTypeId);
    }

    @Override
    public List<UserFile> selectUserFileListByPath(String filePath, Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(UserFile::getFilePath, filePath)
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectFileListLikeRightFilePath(String filePath, long userId) {
        filePath = filePath.replace("\\", "\\\\\\\\");
        filePath = filePath.replace("'", "\\'");
        filePath = filePath.replace("%", "\\%");
        filePath = filePath.replace("_", "\\_");

        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        log.info("查询文件路径：" + filePath);

        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .likeRight(UserFile::getFilePath, filePath)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<UserFile> selectFilePathTreeByUserId(Long userId) {
        LambdaQueryWrapper<UserFile> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserFile::getUserId, userId)
                .eq(UserFile::getIsDir, 1)
                .eq(UserFile::getDeleteFlag, 0);
        return userFileMapper.selectList(lambdaQueryWrapper);
    }


    @Override
    public void deleteUserFile(Long userFileId, Long sessionUserId) {
        UserFile userFile = userFileMapper.selectById(userFileId);
        String uuid = UUID.randomUUID().toString();
        if (userFile.getIsDir() == 1) {
            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<UserFile>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtil.randomInt(FileConstant.deleteFileRandomSize))
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .eq(UserFile::getUserfileid, userFileId);
            userFileMapper.update(null, userFileLambdaUpdateWrapper);

            String filePath = userFile.getFilePath() + userFile.getFileName() + "/";
            updateFileDeleteStateByFilePath(filePath, uuid, sessionUserId);

        }else{
            UserFile userFileTemp = userFileMapper.selectById(userFileId);
            LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userFileLambdaUpdateWrapper.set(UserFile::getDeleteFlag, RandomUtil.randomInt(1, FileConstant.deleteFileRandomSize))
                    .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                    .set(UserFile::getDeleteBatchNum, uuid)
                    .eq(UserFile::getUserfileid, userFileTemp.getUserfileid());
            userFileMapper.update(null, userFileLambdaUpdateWrapper);
        }

        RecoveryFile recoveryFile = new RecoveryFile();
        recoveryFile.setUserFileId(userFileId);
        recoveryFile.setDeleteTime(DateUtil.getCurrentTime());
        recoveryFile.setDeleteBatchNum(uuid);
        recoveryFileMapper.insert(recoveryFile);


    }

    private void updateFileDeleteStateByFilePath(String filePath, String deleteBatchNum, Long userId) {
        executor.execute(() -> {
            List<UserFile> fileList = selectFileListLikeRightFilePath(filePath, userId);
            for (int i = 0; i < fileList.size(); i++){
                UserFile userFileTemp = fileList.get(i);
                    //标记删除标志
                    LambdaUpdateWrapper<UserFile> userFileLambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
                    userFileLambdaUpdateWrapper1.set(UserFile::getDeleteFlag, RandomUtil.randomInt(FileConstant.deleteFileRandomSize))
                            .set(UserFile::getDeleteTime, DateUtil.getCurrentTime())
                            .set(UserFile::getDeleteBatchNum, deleteBatchNum)
                            .eq(UserFile::getUserfileid, userFileTemp.getUserfileid())
                            .eq(UserFile::getDeleteFlag, 0);
                    userFileMapper.update(null, userFileLambdaUpdateWrapper1);

            }
        });
    }


}
