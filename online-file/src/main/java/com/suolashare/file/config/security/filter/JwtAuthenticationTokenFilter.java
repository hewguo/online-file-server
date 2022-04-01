package com.suolashare.file.config.security.filter;

import com.suolashare.common.exception.OnlineException;
import com.suolashare.file.service.SysParamService;
import com.suolashare.file.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt过滤器（第一个过滤器）：获取用户token，查询用户信息拼装到security中，以便后续filter使用
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;
    @Resource
    SysParamService sysParamService;

    @Value("${online.file.version}")
    String onlineVersion;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String version = sysParamService.getValue("version");
        if (!onlineVersion.equals(version)) {
            throw new OnlineException(999999, "脚本未初始化，请在数据库执行数据初始化脚本，存放路径： '/resources/import.sql'！");
        }

        String token = request.getHeader("token");
        if (StringUtils.isNotBlank(token) && !"undefined".equals(token)) {

            Long userId = userService.getUserIdByToken(token);

            // 验证
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(String.valueOf(userId));
                if (userDetails.isEnabled()) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        chain.doFilter(request, response);
    }

}