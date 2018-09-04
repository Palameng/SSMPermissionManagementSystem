package com.mengyuan.common;

import com.mengyuan.model.SysUser;

import javax.servlet.http.HttpServletRequest;

public class RequestHolder {

    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<SysUser>();

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();

    public static void add(SysUser sysUser) {
        userHolder.set(sysUser);
    }

    public static SysUser getCurrentUser() {
        return userHolder.get();
    }

    public static void remove() {
        userHolder.remove();
        requestHolder.remove();
    }

    public static void add(HttpServletRequest request) {
        requestHolder.set(request);
    }

    public static HttpServletRequest getCurrentRequest() {
        return requestHolder.get();
    }
}
