package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wms
 * @since 2022-07-21
 */
public interface UserService extends IService<User> {

    IPage pageC(IPage<User> page);

    IPage pageCC(IPage<User> page, Wrapper wrapper);

    List<User> listAll();
}
