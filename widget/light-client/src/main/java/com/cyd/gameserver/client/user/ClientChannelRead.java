package com.cyd.gameserver.client.user;

import com.cyd.gameserver.external.core.message.ExternalMessage;

public interface ClientChannelRead {

    void read(ExternalMessage externalMessage);
}
