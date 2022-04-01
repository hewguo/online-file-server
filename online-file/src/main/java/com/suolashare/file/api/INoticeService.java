package com.suolashare.file.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.suolashare.file.domain.Notice;
import com.suolashare.file.dto.notice.NoticeListDTO;

public interface INoticeService extends IService<Notice> {


    IPage<Notice> selectUserPage(NoticeListDTO noticeListDTO);

}
