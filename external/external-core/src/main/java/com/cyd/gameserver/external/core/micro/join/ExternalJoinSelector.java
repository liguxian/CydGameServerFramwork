package com.cyd.gameserver.external.core.micro.join;

import com.cyd.gameserver.external.core.ExternalCoreSetting;
import com.cyd.gameserver.external.core.common.ExternalJoinEnum;

public interface ExternalJoinSelector {

    ExternalJoinEnum getExternalJoinEnum();

    void defaultSetting(ExternalCoreSetting setting);
}
