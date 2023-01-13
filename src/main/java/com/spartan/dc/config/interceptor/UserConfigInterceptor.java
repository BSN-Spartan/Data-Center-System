package com.spartan.dc.config.interceptor;

import com.alibaba.fastjson.JSON;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.util.user.UserGlobals;
import com.spartan.dc.core.util.user.UserLoginInfo;
import com.spartan.dc.dao.write.DcUserMapper;
import com.spartan.dc.dao.write.SysResourceMapper;
import com.spartan.dc.model.DcUser;
import com.spartan.dc.model.SysResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@Component
public class UserConfigInterceptor implements HandlerInterceptor {


    @Autowired
    private SysResourceMapper sysResourceMapper;
    @Autowired
    private DcUserMapper dcUserMapper;

    static String LOGIN_TIMEOUT = "/";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            HandlerMethod handlerMethod = ((HandlerMethod) handler);
            RequiredPermission requiredPermission = handlerMethod
                    .getMethodAnnotation(RequiredPermission.class);



            if (requiredPermission == null || requiredPermission.validate() == false) {
                return true;
            } else {
                HttpSession session = request.getSession();
                if (session.getAttribute(UserGlobals.USER_SESSION_KEY) == null) {
                    if (requiredPermission.isPage()) {
                        return returnResultInfo(response, null, LOGIN_TIMEOUT);
                    } else {// ajax
                        return returnResultInfo(response, ResultInfoUtil.sysErrorResult("Timeout"), null);
                    }
                }
                UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(UserGlobals.USER_SESSION_KEY);
                if (userLoginInfo == null) {
                    if (requiredPermission.isPage()) {
                        return returnResultInfo(response, null, LOGIN_TIMEOUT);
                    } else {// ajax
                        return returnResultInfo(response, ResultInfoUtil.sysErrorResult("Timeout"), null);
                    }
                } else {
                    DcUser dcUser = dcUserMapper.selectByPrimaryKey(userLoginInfo.getUserId());
                    if (dcUser.getState() == 0) {
                        if (requiredPermission.isPage()) {
                            return returnResultInfo(response, null, LOGIN_TIMEOUT);
                        } else {
                            return returnResultInfo(response, ResultInfoUtil.sysErrorResult("The user is disabled."), null);
                        }
                    }

                    List<SysResource> resourceList = sysResourceMapper.listByUserId( userLoginInfo.getUserId());
                    if (CollectionUtils.isEmpty((resourceList))) {
                        if (requiredPermission.isPage()) {
                            return returnResultInfo(response, null, LOGIN_TIMEOUT);
                        } else {// ajax
                            return returnResultInfo(response, ResultInfoUtil.sysErrorResult("No permission, please contact the administrator"), null);
                        }

                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }


    private boolean returnResultInfo(HttpServletResponse response, ResultInfo info, String url)
            throws IOException {


        if (url != null) {

            response.sendRedirect(url);

        } else {

            response.setContentType("application/json");

            response.setCharacterEncoding("UTF-8");

            response.setHeader("Cache-Control", "no-cache");

            PrintWriter pw = response.getWriter();

            pw.write(JSON.toJSONString(info));
        }

        return false;

    }

}
