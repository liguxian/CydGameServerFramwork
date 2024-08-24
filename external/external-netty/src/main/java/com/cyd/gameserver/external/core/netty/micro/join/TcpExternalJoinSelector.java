package com.cyd.gameserver.external.core.netty.micro.join;

import com.cyd.gameserver.common.kit.PresentKit;
import com.cyd.gameserver.external.core.ExternalCoreSetting;
import com.cyd.gameserver.external.core.common.ExternalJoinEnum;
import com.cyd.gameserver.external.core.micro.MicroBootstrapFlow;
import com.cyd.gameserver.external.core.netty.DefaultExternalCoreSetting;
import com.cyd.gameserver.external.core.netty.micro.TcpMicroBootstrapFlow;

public class TcpExternalJoinSelector extends SocketExternalJoinSelector{
    @Override
    public ExternalJoinEnum getExternalJoinEnum() {
        return ExternalJoinEnum.TCP;
    }

    @Override
    public void defaultSetting(ExternalCoreSetting coreSetting){
        super.defaultSetting(coreSetting);

        DefaultExternalCoreSetting setting = (DefaultExternalCoreSetting) coreSetting;

        //设置handler业务流
        MicroBootstrapFlow<?> microBootstrapFlow = setting.getMicroBootstrapFlow();
        PresentKit.ifNull(microBootstrapFlow, () -> setting.setMicroBootstrapFlow(new TcpMicroBootstrapFlow()));
    }
}
