package com.cyd.gameserver.common.kit;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CmdKit {

    public int getCmd(int cmdMerge) {
        return cmdMerge >> 16;
    }

    public int getSubCmd(int cmdMerge) {
        return cmdMerge & 0xFFFF;
    }

    public int merge(int cmd, int subCmd) {
        return (cmd << 16) + subCmd;
    }

    public String mergeToString(int cmdMerge) {
        int cmd = getCmd(cmdMerge);
        int subCmd = getSubCmd(cmdMerge);
        String template = "[cmd:%d - subCmd:%d - cmdMerge:%d]";
        return String.format(template, cmd, subCmd, cmdMerge);
    }

    public String mergeToShort(int cmdMerge) {
        int cmd = getCmd(cmdMerge);
        int subCmd = getSubCmd(cmdMerge);
        return String.format("[cmd:%d-%d %d]", cmd, subCmd, cmdMerge);
    }

    public String toString(int cmdMerge) {
        int cmd = getCmd(cmdMerge);
        int subCmd = getSubCmd(cmdMerge);
        String template = "cmd[%d - %d]";

        return String.format(template, cmd, subCmd);
    }
}
