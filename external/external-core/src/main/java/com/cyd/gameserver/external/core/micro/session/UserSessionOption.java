package com.cyd.gameserver.external.core.micro.session;

import com.cyd.gameserver.common.kit.attr.AttrOption;
import com.cyd.gameserver.external.core.common.ExternalJoinEnum;

import java.util.Set;

/**
 * userSession的一些属性
 */
public interface UserSessionOption {

    /**
     * 是否已认证
     */
    AttrOption<Boolean> verifyIdentify = AttrOption.valueOf("verifyIdentify");

    /**
     * 元信息
     */
    AttrOption<byte[]> attachment = AttrOption.valueOf("attachment");

    /**
     *用户绑定的多个逻辑服
     */
    AttrOption<Set<Integer>> bindingLogicServerIdSet = AttrOption.valueOf("bindingLogicServerIdSet");

    /**
     * 用户绑定的多个逻辑服
     */
    AttrOption<int[]> bindingLogicServerIdArray = AttrOption.valueOf("bindingLogicServerIdSetArray");
    /**
     *连接方式
     */
    AttrOption<ExternalJoinEnum> externalJoin = AttrOption.valueOf("externalJoin");

    /**
     * 用户真实ip
     */
    AttrOption<String> realIp = AttrOption.valueOf("realIp", "");
}
