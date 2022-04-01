package com.suolashare.file.util;

import com.suolashare.common.util.DateUtil;
import com.suolashare.file.domain.UserFile;

public class OnlineFileUtil {


    public static UserFile getOnlineDir(long userId, String filePath, String fileName) {
        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        userFile.setFileId(null);
        userFile.setFileName(fileName);
        userFile.setFilePath(filePath);
        userFile.setExtendName(null);
        userFile.setIsDir(1);
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFile.setDeleteFlag(0);
        userFile.setDeleteBatchNum(null);
        return userFile;
    }

    public static UserFile getOnlineFile(long userId, long fileId, String filePath, String fileName, String extendName) {
        UserFile userFile = new UserFile();
        userFile.setUserId(userId);
        userFile.setFileId(fileId);
        userFile.setFileName(fileName);
        userFile.setFilePath(filePath);
        userFile.setExtendName(extendName);
        userFile.setIsDir(0);
        userFile.setUploadTime(DateUtil.getCurrentTime());
        userFile.setDeleteFlag(0);
        userFile.setDeleteBatchNum(null);
        return userFile;
    }

}
