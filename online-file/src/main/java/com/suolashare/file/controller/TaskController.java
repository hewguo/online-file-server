package com.suolashare.file.controller;

import com.suolashare.file.api.IElasticSearchService;
import com.suolashare.file.component.FileDealComp;
import com.suolashare.file.domain.UserFile;
import com.suolashare.file.service.UserFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Controller
public class TaskController {

    @Resource
    UserFileService userFileService;
    @Autowired
    @Lazy
    private IElasticSearchService elasticSearchService;
    @Resource
    FileDealComp fileDealComp;


    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    public void updateElasticSearch() {

        try {
            elasticSearchService.deleteAll();
        } catch (Exception e) {
            log.debug("删除ES失败:" + e);
        }

        List<UserFile> userfileList = userFileService.list();
        for (UserFile userFile : userfileList) {
            fileDealComp.uploadESByUserFileId(userFile.getUserfileid());
        }

    }
}
