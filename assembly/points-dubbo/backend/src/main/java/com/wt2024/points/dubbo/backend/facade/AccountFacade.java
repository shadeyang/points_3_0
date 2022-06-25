package com.wt2024.points.dubbo.backend.facade;

import com.wt2024.points.core.api.domain.account.*;
import com.wt2024.points.core.api.service.AccountService;
import com.wt2024.points.dubbo.backend.constant.RestPathConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.protocol.rest.support.ContentType;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/5/26 15:14
 * @Project points2.0:com.wt2024.points.service.facade
 */
@DubboService(protocol = "rest", validation = "true")
@Path(RestPathConstants.ACCOUNT.PATH)
@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
@Produces({ContentType.APPLICATION_JSON_UTF_8, ContentType.TEXT_XML_UTF_8})
@Slf4j
public class AccountFacade implements IAccountFacade {

    @Resource
    private AccountService accountService;

    @Override
    @POST
    @Path(RestPathConstants.ACCOUNT.QUERY_ALL_PATH)
    public AccountInfoQueryAllOutput queryAllAccountInfo(AccountInfoQueryAllInput accountInfoQueryAllInput) {
        return accountService.queryAllAccountInfo(accountInfoQueryAllInput);
    }

    @Override
    @POST
    @Path(RestPathConstants.ACCOUNT.QUERY_PATH)
    public AccountInfoQueryOutput queryAccountInfoByPointsType(AccountInfoQueryInput accountInfoQueryInput) {
        return accountService.queryAccountInfoByPointsType(accountInfoQueryInput);
    }

    @Override
    @POST
    @Path(RestPathConstants.ACCOUNT.QUERY_DETAIL_PATH)
    public AccountDetailsQueryOutput queryAccountDetails(AccountDetailsQueryInput accountDetailsQueryInput) {
        return accountService.queryAccountDetails(accountDetailsQueryInput);
    }

}
