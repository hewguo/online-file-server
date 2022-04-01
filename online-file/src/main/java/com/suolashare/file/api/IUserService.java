package com.suolashare.file.api;

import com.baomidou.mybatisplus.extension.service.IService;
import com.suolashare.common.result.RestResult;
import com.suolashare.file.domain.user.Role;
import com.suolashare.file.domain.user.UserBean;

import java.util.List;

public interface IUserService extends IService<UserBean> {


    Long getUserIdByToken(String token);


    /**
     * 用户注册
     *
     * @param userBean 用户信息
     * @return 结果
     */
    RestResult<String> registerUser(UserBean userBean);



    UserBean findUserInfoByTelephone(String telephone);
    List<Role> selectRoleListByUserId(long userId);
    String getSaltByTelephone(String telephone);
    UserBean selectUserByTelephoneAndPassword(String username, String password);







}
