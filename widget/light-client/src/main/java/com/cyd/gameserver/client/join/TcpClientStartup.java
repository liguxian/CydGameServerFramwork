package com.cyd.gameserver.client.join;

import com.cyd.gameserver.action.skeleton.core.CmdInfo;
import com.cyd.gameserver.client.ClientConnectOption;
import com.cyd.gameserver.client.command.RequestCommand;
import com.cyd.gameserver.client.join.handler.ClientMessageHandler;
import com.cyd.gameserver.client.proto.Test1;
import com.cyd.gameserver.client.user.ClientUser;
import com.cyd.gameserver.client.user.ClientUserChannel;
import com.cyd.gameserver.client.user.DefaultClientUser;
import com.cyd.gameserver.external.core.config.ExternalGlobalConfig;
import com.cyd.gameserver.external.core.netty.handler.codec.TcpExternalCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Objects;

@Slf4j
public class TcpClientStartup implements ClientConnect {

    static int PACKAGE_MAX_SIZE = 1024 * 1024;

    @Override
    public void connect(ClientConnectOption connectOption) {
        ClientUser clientUser = connectOption.getClientUser();
        ClientMessageHandler clientMessageHandler = new ClientMessageHandler(clientUser);

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        // 数据包长度 = 长度域的值 + lengthFieldOffset + lengthFieldLength + lengthAdjustment。
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(PACKAGE_MAX_SIZE,
                                // 长度字段的偏移量， 从 0 开始
                                0,
                                // 字段的长度, 如果使用的是 short ，占用2位；（消息头用的 byteBuf.writeShort 来记录长度的）
                                // 字段的长度, 如果使用的是 int   ，占用4位；（消息头用的 byteBuf.writeInt   来记录长度的）
                                4,
                                // 要添加到长度字段值的补偿值：长度调整值 = 内容字段偏移量 - 长度字段偏移量 - 长度字段的字节数
                                0,
                                // 跳过的初始字节数： 跳过0位; (跳过消息头的 0 位长度)
                                0));

                        // 编解码
                        pipeline.addLast("codec", new TcpExternalCodec());

                        pipeline.addLast(clientMessageHandler);
                    }
                });
        InetSocketAddress address = connectOption.getSocketAddress();
        String hostName = address.getHostName();
        int port = address.getPort();
        final ChannelFuture channelFuture = bootstrap.connect(hostName, port);

        try {
            Channel channel = channelFuture.sync().channel();

            ClientUserChannel userChannel = clientUser.getClientUserChannel();
            userChannel.setClientChannel(channel::writeAndFlush);

            //测试发送消息
            CmdInfo cmdInfo = CmdInfo.getCmdInfo(1, 1);
            Test1 test1 = new Test1();
            test1.setMsg("你好cyd");
            RequestCommand requestCommand = new RequestCommand()
                    .setClientUserChannel(userChannel)
                    .setRequestData(test1)
                    .setTitle("test")
                    .setCallback(commandResult -> System.out.println(String.format("接收服务器响应 : %s", commandResult.getValue(Test1.class).getMsg())))
                    .setCmdMerge(cmdInfo.getCmdMerge());
            requestCommand.execute();

            channel.closeFuture().await();
            System.out.println("連接關閉");
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } finally {
            group.shutdownGracefully();
        }
    }

    private ClientUser clientUser;

    public ClientUser getClientUser() {
        if (Objects.nonNull(clientUser)) {
            return clientUser;
        }
        synchronized (this) {
            if (Objects.nonNull(clientUser)) {
                return clientUser;
            }
            initChannel();
            return clientUser;
        }
    }

    public void initChannel() {
        clientUser = new DefaultClientUser();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", ExternalGlobalConfig.externalPort);
        ClientMessageHandler clientMessageHandler = new ClientMessageHandler(clientUser);

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        // 数据包长度 = 长度域的值 + lengthFieldOffset + lengthFieldLength + lengthAdjustment。
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(PACKAGE_MAX_SIZE,
                                // 长度字段的偏移量， 从 0 开始
                                0,
                                // 字段的长度, 如果使用的是 short ，占用2位；（消息头用的 byteBuf.writeShort 来记录长度的）
                                // 字段的长度, 如果使用的是 int   ，占用4位；（消息头用的 byteBuf.writeInt   来记录长度的）
                                4,
                                // 要添加到长度字段值的补偿值：长度调整值 = 内容字段偏移量 - 长度字段偏移量 - 长度字段的字节数
                                0,
                                // 跳过的初始字节数： 跳过0位; (跳过消息头的 0 位长度)
                                0));

                        // 编解码
                        pipeline.addLast("codec", new TcpExternalCodec());

                        pipeline.addLast(clientMessageHandler);
                    }
                });
        String hostName = address.getHostName();
        int port = address.getPort();
        final ChannelFuture channelFuture = bootstrap.connect(hostName, port);

        try {
            Channel channel = channelFuture.sync().channel();

            ClientUserChannel userChannel = clientUser.getClientUserChannel();
            userChannel.setClientChannel(channel::writeAndFlush);

            channel.closeFuture().addListener(promise -> {
                System.out.println("连接关闭");
                group.shutdownGracefully();
            });

        } catch (InterruptedException e) {
            System.out.println(e);
            log.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        TcpClientStartup tcpClientStartup = new TcpClientStartup();
        ClientUser clientUser = tcpClientStartup.getClientUser();
        //测试发送消息
        CmdInfo cmdInfo = CmdInfo.getCmdInfo(1, 1);
        Test1 test1 = new Test1();
        test1.setMsg("你好cyd");
        RequestCommand requestCommand = new RequestCommand()
                .setClientUserChannel(clientUser.getClientUserChannel())
                .setRequestData(test1)
                .setTitle("test")
                .setCallback(commandResult -> System.out.println(String.format("接收服务器响应 : %s", commandResult.getValue(Test1.class).getMsg())))
                .setCmdMerge(cmdInfo.getCmdMerge());
        requestCommand.execute();
    }
}
