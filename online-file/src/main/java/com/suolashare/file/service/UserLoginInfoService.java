package com.suolashare.file.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.suolashare.file.api.IUserLoginInfoService;
import com.suolashare.file.domain.UserLoginInfo;
import com.suolashare.file.mapper.UserLoginInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(rollbackFor=Exception.class)
public class UserLoginInfoService extends ServiceImpl<UserLoginInfoMapper, UserLoginInfo> implements IUserLoginInfoService {


}
