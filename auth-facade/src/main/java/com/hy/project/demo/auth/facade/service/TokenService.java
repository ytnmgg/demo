package com.hy.project.demo.auth.facade.service;

import java.util.List;

import com.hy.project.demo.auth.facade.model.LoginInfo;
import com.hy.project.demo.auth.facade.model.SysUser;
import com.hy.project.demo.auth.facade.model.request.SimpleRequest;
import com.hy.project.demo.auth.facade.model.result.SimpleResult;
import com.hy.project.demo.common.model.BaseRequest;
import com.hy.project.demo.common.model.BaseResult;
import com.hy.project.demo.common.model.PageRequest;
import com.hy.project.demo.common.model.PageResult;

/**
 * @author rick.wl
 * @date 2022/09/14
 */
public interface TokenService {

    SimpleResult<SysUser> getUserByToken(SimpleRequest<String> request);

    SimpleResult<String> createToken(SimpleRequest<String> request);

    BaseResult removeToken(SimpleRequest<String> request);

    BaseResult saveToken(SimpleRequest<LoginInfo> request);

    BaseResult expireTokens(BaseRequest request);

    BaseResult touchToken(SimpleRequest<String> request);

    PageResult<List<LoginInfo>> pageQueryLoginInfo(PageRequest request);
}
