package com.cyd.gameserver.test.external;

import com.cyd.gameserver.external.core.ExternalServer;
import com.cyd.gameserver.external.core.common.ExternalJoinEnum;
import com.cyd.gameserver.external.core.config.ExternalGlobalConfig;
import com.cyd.gameserver.external.core.netty.kit.ExternalServerCreateKit;

/**
 * Hello world!
 *
 */
public class ExternalApplication
{
    public static void main( String[] args )
    {
        ExternalServer externalServer = ExternalServerCreateKit.createExternalServer(ExternalGlobalConfig.externalPort, ExternalJoinEnum.TCP);
        externalServer.startUp();
    }
}
