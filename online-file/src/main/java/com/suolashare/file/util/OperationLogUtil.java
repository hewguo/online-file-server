package com.suolashare.file.util;

import com.suolashare.common.util.CollectUtil;
import com.suolashare.common.util.DateUtil;
import com.suolashare.file.domain.OperationLogBean;

import javax.servlet.http.HttpServletRequest;

public class OperationLogUtil {

    /**
     * 构造操作日志参数
     *
     * @param request   请求
     * @param isSuccess 操作是否成功（成功/失败）
     * @param source    操作源模块
     * @param operation 执行操作
     * @param detail    详细信息
     * @return 操作日志参数
     */
    public static OperationLogBean getOperationLogObj(HttpServletRequest request, Long userId, String isSuccess, String source, String operation, String detail) {

        //用户需要登录才能进行的操作，需要记录操作日志
        OperationLogBean operationLogBean = new OperationLogBean();
        operationLogBean.setUserId(userId);
        operationLogBean.setTime(DateUtil.getCurrentTime());
        operationLogBean.setTerminal(new CollectUtil().getClientIpAddress(request));
        operationLogBean.setSource(source);
        operationLogBean.setResult(isSuccess);
        operationLogBean.setOperation(operation);
        operationLogBean.setDetail(detail);
        operationLogBean.setPlatform(3);

        return operationLogBean;
    }

}
