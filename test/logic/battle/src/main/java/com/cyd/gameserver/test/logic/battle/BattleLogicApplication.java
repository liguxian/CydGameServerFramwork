package com.cyd.gameserver.test.logic.battle;

import com.cyd.gameserver.action.skeleton.ext.spring.ActionFactoryBeanForSpring;
import com.cyd.gameserver.bolt.client.BrokerClientApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class BattleLogicApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(BattleLogicApplication.class, args);

        //启动战斗逻辑服
        BattleLogicClient battleLogicClient = new BattleLogicClient();
        BrokerClientApplication.start(battleLogicClient);
    }

    @Bean
    public ActionFactoryBeanForSpring actionFactoryBeanForSpring(){
        return ActionFactoryBeanForSpring.me();
    }
}
