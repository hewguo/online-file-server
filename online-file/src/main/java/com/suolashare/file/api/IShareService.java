package com.suolashare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suolashare.file.domain.RecoveryFile;
import com.suolashare.file.domain.Share;
import com.suolashare.file.domain.ShareFile;
import com.suolashare.file.dto.sharefile.ShareListDTO;
import com.suolashare.file.vo.share.ShareFileListVO;
import com.suolashare.file.vo.share.ShareListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IShareService  extends IService<Share> {
    List<ShareListVO> selectShareList(ShareListDTO shareListDTO, Long userId);
    int selectShareListTotalCount(ShareListDTO shareListDTO, Long userId);
}
