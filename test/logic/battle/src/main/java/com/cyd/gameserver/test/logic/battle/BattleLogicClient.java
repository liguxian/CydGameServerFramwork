package com.cyd.gameserver.test.logic.battle;

import com.cyd.gameserver.action.skeleton.core.BarSkeleton;
import com.cyd.gameserver.action.skeleton.core.BarSkeletonBuilder;
import com.cyd.gameserver.action.skeleton.kit.LogicServerCreateKit;
import com.cyd.gameserver.bolt.client.AbstractBrokerClientStartup;
import com.cyd.gameserver.bolt.core.client.BrokerClient;
import com.cyd.gameserver.bolt.core.client.BrokerClientBuilder;
import com.cyd.gameserver.test.logic.battle.action.BattleAction;

public class BattleLogicClient extends AbstractBrokerClientStartup {
    @Override
    public BarSkeleton createBarSkeleton() {
        BarSkeletonBuilder barSkeletonBuilder = LogicServerCreateKit.createBuilder(BattleAction.class);
        return barSkeletonBuilder.build();
    }

    @Override
    public BrokerClientBuilder createBrokerClientBuilder() {
        BrokerClientBuilder builder = BrokerClient.builder();
        builder.appName("战斗逻辑服");
        return builder;
    }
}
