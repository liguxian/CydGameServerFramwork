package com.cyd.gameserver.bolt.broker.server;

import com.alipay.remoting.rpc.RpcConfigs;
import com.alipay.remoting.rpc.RpcServer;
import com.cyd.gameserver.action.skeleton.toy.IoGameBanner;
import com.cyd.gameserver.bolt.broker.server.balanced.BalancedManager;
import com.cyd.gameserver.bolt.broker.server.service.BrokerClientModules;
import com.cyd.gameserver.bolt.core.GroupWith;
import com.cyd.gameserver.common.consts.LogName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter(AccessLevel.PACKAGE)
@Accessors(chain = true)
@Slf4j(topic = LogName.CommonStdout)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrokerServer implements GroupWith {

    String brokerId;

    int port;

    RpcServer rpcServer;

    /** broker （游戏网关）的启动模式，默认单机模式 */
    BrokerRunModeEnum brokerRunMode;

    BrokerClientModules brokerClientModules;

    final BalancedManager balancedManager = new BalancedManager(this);

    /**
     * 网关服启动进程编号，在逻辑服和对外服注册后反过去，逻辑服根据此编号判断和自己是否为同一进程，在给网关发消息时优先选同进程的
     */
    int withNo;

    void initRpcServer() {
        rpcServer = new RpcServer(port, true);
    }

    public void startup() {
        System.setProperty(RpcConfigs.DISPATCH_MSG_LIST_IN_DEFAULT_EXECUTOR, "false");
        rpcServer.startup();

        log.info("启动游戏网关 port: [{}] 启动模式: [{}] ", this.port, this.brokerRunMode);

        IoGameBanner.render();
        IoGameBanner.me().countDown();
    }

    public static BrokerServerBuilder newBuilder() {
        return new BrokerServerBuilder();
    }

    @Override
    public void setWithNo(int withNo){
        this.withNo = withNo;
    }
}
