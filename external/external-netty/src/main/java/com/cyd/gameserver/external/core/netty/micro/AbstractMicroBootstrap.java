package com.cyd.gameserver.external.core.netty.micro;

import com.cyd.gameserver.external.core.micro.MicroBootstrap;
import com.cyd.gameserver.external.core.netty.DefaultExternalCoreSetting;
import lombok.Setter;

public abstract class AbstractMicroBootstrap implements MicroBootstrap {

    @Setter
    protected DefaultExternalCoreSetting setting;
}
