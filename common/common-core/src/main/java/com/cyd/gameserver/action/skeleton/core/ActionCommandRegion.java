package com.cyd.gameserver.action.skeleton.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jctools.maps.NonBlockingHashMap;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PACKAGE)
public class ActionCommandRegion {

    final int cmd;

    Class<?> actionControllerClazz;

    Map<Integer, ActionCommand> subActionCommandMap = new NonBlockingHashMap<>();

    public ActionCommandRegion(int cmd) {
        this.cmd = cmd;
    }

    public boolean containsKey(int subCmd) {
        return subActionCommandMap.containsKey(subCmd);
    }

    public void add(ActionCommand subActionCommand) {
        CmdInfo cmdInfo = subActionCommand.getCmdInfo();
        int subCmd = cmdInfo.getSubCmd();
        subActionCommandMap.put(subCmd, subActionCommand);
    }

    /**
     * 得到子路由最大值
     *
     * @return 子路由最大值
     */
    public int getMaxSubCmd() {
        return subActionCommandMap
                .keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(0)
                ;
    }

    public Collection<ActionCommand> values() {
        return this.subActionCommandMap.values();
    }

    /**
     * 将子路由列表转为数组
     *
     * @return array
     */
    public ActionCommand[] arrayActionCommand(){
        ActionCommand[] array = new ActionCommand[getMaxSubCmd() + 1];

        for (Map.Entry<Integer, ActionCommand> entry : subActionCommandMap.entrySet()) {
            array[entry.getKey()] = entry.getValue();
        }

        return array;
    }

}
