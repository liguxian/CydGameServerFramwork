package com.cyd.gameserver.external.core;

import com.cyd.gameserver.external.core.micro.MicroBootstrap;

public interface ExternalCore {

    //创建与用户连接的服务器
    MicroBootstrap createMicroBootstrap();
}
