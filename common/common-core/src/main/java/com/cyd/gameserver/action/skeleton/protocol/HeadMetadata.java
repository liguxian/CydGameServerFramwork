package com.cyd.gameserver.action.skeleton.protocol;

import com.cyd.gameserver.action.skeleton.core.CmdInfo;
import com.cyd.gameserver.action.skeleton.core.CmdInfoFlyweightFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息元信息
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public class HeadMetadata implements Serializable {
    @Serial
    private static final long serialVersionUID = -472575113683576693L;
    long userId;

    int cmdMerge;

    /**
     * 来源逻辑服id（包括对外服）
     */
    int sourceClientId;

    /**
     * 目标逻辑服id
     */
    int endPointClientId;

    /**
     * rpc type
     * <pre>
     *     see : com.alipay.remoting.rpc.RpcCommandType
     *
     *     在 bolt 中， 调用方使用
     *     com.alipay.remoting.rpc.RpcServer或 com.alipay.remoting.rpc.RpcClient 的 oneway 方法
     *
     *     则 AsyncContext.sendResponse 无法回传响应
     *     原因可阅读 com.alipay.remoting.rpc.protocol.RpcRequestProcessor#sendResponseIfNecessary 源码。
     *
     *
     *     业务框架保持与 bolt 的风格一至使用 RpcCommandType
     *     不同的是业务框架会用 RpcCommandType 区别使用什么方式来发送响应。
     *
     *     如果 rpcCommandType != RpcCommandType.REQUEST_ONEWAY ,
     *     就使用 com.alipay.remoting.AsyncContext#sendResponse 来发送响应
     *
     * </pre>
     */
    byte rpcCommandType;

    /**
     * 扩展字段
     * <pre>
     *     开发者有特殊业务可以通过这个字段来扩展元信息，该字段的信息会跟随每一个请求；
     * </pre>
     */
    byte[] attachmentData;

    /**
     * netty 的 channelId。
     * <pre>
     *     框架存放 netty 的 channelId 是为了能在对外服查找到对应的连接，
     *     一但玩家登录后，框架将会使用玩家的 userId 而不是 channelId 来查找对应的连接（channel），
     *     因为 channelId 的字符串实在太长了，每次将该值传输到逻辑服会小小的影响性能。
     *
     *     一但玩家登录后，框架不会在使用这个属性了；开发者如果有需要，可以把这个字段利用起来。
     * </pre>
     */
    String channelId;

    /** 消息标记号；由前端请求时设置，服务器响应时会携带上 */
    int msgId;

    /** 框架自用字段。将来变化可能较大，开发者请不要使用。 */
    int stick;

    /**
     * 玩家绑定的多个游戏逻辑服 id
     * <pre>
     *     所有与该游戏逻辑服相关的请求都将被分配给已绑定的游戏逻辑服处理。
     *     即使启动了多个同类型的游戏逻辑服，该请求仍将被分配给已绑定的游戏逻辑服处理。
     * </pre>
     */
    int[] bindingLogicServerIds;
    /** 框架自用字段。将来变化可能较大，开发者请不要使用。 */
    int cacheCondition;
    /**
     * 自定义数据，专为开发者预留的一个字段，开发者可以利用该字段来传递自定义数据
     * <pre>
     *     该字段由开发者自己定义，框架不会对数据做任何处理，也不会做任何检查，
     *     开发者可以利用该字段来传递任何数据，包括自定义对象。
     * </pre>
     */
    byte[] customData;

    /** 临时变量 */
    transient Object other;
    transient int withNo;

    public HeadMetadata setCmdInfo(CmdInfo cmdInfo) {
        this.cmdMerge = cmdInfo.getCmdMerge();
        return this;
    }

    /**
     * 得到 cmdInfo 命令路由信息
     * <pre>
     *     如果只是为了获取 cmd 与 subCmd，下面的方式效率会更高
     *     cmd = CmdKit.getCmd(cmdMerge);
     *     subCmd = CmdKit.getSubCmd(cmdMerge);
     *
     *     但实际上更推荐使用 CmdInfo，这样使得代码书写简洁，也更容易理解，毕竟代码是给人看的。
     * </pre>
     *
     * @return cmdInfo
     */
    public CmdInfo getCmdInfo() {
        return CmdInfoFlyweightFactory.of(this.cmdMerge);
    }

    public HeadMetadata setCmdMerge(int cmdMerge) {
        this.cmdMerge = cmdMerge;
        return this;
    }

    /**
     * 类似 clone
     * <p>
     * 使用场景
     * <pre>
     *     与其他游戏逻辑服通信时可以使用
     *     方法中给 HeadMetadata 赋值了玩家的必要属性：
     *     userId、attachmentData、channelId、bindingLogicServerIds
     * </pre>
     * 以下属性不会赋值，如有需要，请自行赋值
     * <pre>
     *     cmdMerge
     *     sourceClientId
     *     endPointClientId
     *     rpcCommandType
     *     msgId
     * </pre>
     *
     * @return HeadMetadata
     */
    public HeadMetadata cloneHeadMetadata() {

        HeadMetadata headMetadata = new HeadMetadata();
        headMetadata.userId = this.userId;
        headMetadata.attachmentData = this.attachmentData;
        headMetadata.channelId = this.channelId;
        headMetadata.bindingLogicServerIds = this.bindingLogicServerIds;

        return headMetadata;
    }
}
