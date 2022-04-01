package com.suolashare.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.suolashare.file.domain.user.Role;
import com.suolashare.file.domain.user.UserBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<UserBean> {
    int insertUser(UserBean userBean);

    int insertUserRole(long userid, long roleid);

    List<Role>  selectRoleListByUserId(@Param("userid") long userid);

    String selectSaltByTelephone(@Param("telephone") String telephone);

    UserBean selectUserByTelephoneAndPassword(@Param("telephone") String telephone, @Param("password") String password);

}
