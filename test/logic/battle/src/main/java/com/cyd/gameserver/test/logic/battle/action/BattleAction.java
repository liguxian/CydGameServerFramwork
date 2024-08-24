package com.cyd.gameserver.test.logic.battle.action;

import com.cyd.gameserver.action.skeleton.annotation.ActionController;
import com.cyd.gameserver.action.skeleton.annotation.ActionMethod;
import com.cyd.gameserver.test.logic.battle.proto.Test1;
import org.springframework.stereotype.Component;

@Component
@ActionController(1)
public class BattleAction {

    @ActionMethod(1)
    public String test1(Test1 msg){
        return "response - " + msg.getMsg();
    }
}
