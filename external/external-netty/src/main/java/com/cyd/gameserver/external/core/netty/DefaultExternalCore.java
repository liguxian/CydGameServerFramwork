package com.cyd.gameserver.external.core.netty;

import com.cyd.gameserver.common.kit.PresentKit;
import com.cyd.gameserver.external.core.ExternalCore;
import com.cyd.gameserver.external.core.ExternalCoreSetting;
import com.cyd.gameserver.external.core.common.ExternalJoinEnum;
import com.cyd.gameserver.external.core.kit.hook.DefaultUserHook;
import com.cyd.gameserver.external.core.kit.hook.UserHook;
import com.cyd.gameserver.external.core.micro.MicroBootstrap;
import com.cyd.gameserver.external.core.micro.join.ExternalJoinSelector;
import com.cyd.gameserver.external.core.micro.join.ExternalJoinSelectors;
import com.cyd.gameserver.external.core.micro.session.UserSessionOption;
import com.cyd.gameserver.external.core.micro.session.UserSessions;

import java.util.Arrays;
import java.util.Objects;

public final class DefaultExternalCore implements ExternalCore {

    final DefaultExternalCoreSetting setting;

    public DefaultExternalCore(ExternalCoreSetting setting) {
        this.setting = (DefaultExternalCoreSetting) setting;
    }

    /**
     * 创建与用户连接的服务器
     * @return
     */
    @Override
    public MicroBootstrap createMicroBootstrap() {
        check();

        //获取连接方式，并设置当前连接方式的属性
        ExternalJoinEnum joinEnum = setting.getJoinEnum();
        ExternalJoinSelector externalJoinSelector = ExternalJoinSelectors.getExternalJoinSelector(joinEnum);
        externalJoinSelector.defaultSetting(setting);

        //以下是设置通用连接方式的属性

        //用户上下线钩子：若开发者没有手动赋值，则根据当前连接方式生成
        UserHook userHook = setting.getUserHook();
        PresentKit.ifNull(userHook, () -> setting.setUserHook(new DefaultUserHook()));
        UserSessions<?, ?> userSessions = setting.getUserSessions();
        userSessions.setUserHook(setting.getUserHook());

        //当前对外服的连接方式
        userSessions.option(UserSessionOption.externalJoin, joinEnum);

        return setting.getMicroBootstrap();
    }

    private void check(){
        int port = setting.getPort();
        if(port <= 0) {
            throw new IllegalArgumentException("对外服端口必须设置 > 0 " + port);
        }

        Objects.requireNonNull(setting.getJoinEnum(), "需要设置一种连接方式" + Arrays.toString(ExternalJoinEnum.values()));
    }
}
