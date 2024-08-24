package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.common.kit.CmdKit;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.jctools.maps.NonBlockingHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@FieldDefaults(level = AccessLevel.PACKAGE)
public class ActionCommandRegions {

    private static final ActionCommand[][] EMPTY = new ActionCommand[0][0];

    /**
     * actionRegion Map
     * key:cmd
     */
    Map<Integer, ActionCommandRegion> actionCommandRegionMap = new NonBlockingHashMap<>();

    /**
     * action 数组. 下标对应 cmd
     * <pre>
     *     第一维: cmd
     *     第二维: cmd 下面的子 subCmd
     *
     *     这里使用数组，没有使用 map 嵌入 map 的方式
     *     因为 map 嵌 map 的方式获取一个 action 需要多次 hash 和 equals 才能找到
     *     而通过数组则可以快速的找到对应的 action
     * </pre>
     */
    @Getter
    ActionCommand[][] actionCommands = EMPTY;

    public ActionCommandRegion getActionCommandRegion(int cmd) {
        ActionCommandRegion actionCommandRegion = actionCommandRegionMap.get(cmd);

        //无锁化
        if (Objects.isNull(actionCommandRegion)) {
            actionCommandRegion = new ActionCommandRegion(cmd);
            actionCommandRegion = actionCommandRegionMap.putIfAbsent(cmd, actionCommandRegion);

            if (Objects.isNull(actionCommandRegion)) {
                actionCommandRegion = actionCommandRegionMap.get(cmd);
            }
        }

        return actionCommandRegion;
    }

    public ActionCommand getActionCommand(int cmd, int subCmd) {
        if (cmd > actionCommands.length) {
            return null;
        }
        ActionCommand[] sunActionCommands = actionCommands[cmd];
        if (subCmd >= sunActionCommands.length) {
            return null;
        }

        return sunActionCommands[subCmd];
    }

    public ActionCommand getActionCommand(int cmdMerge) {
        int cmd = CmdKit.getCmd(cmdMerge);
        int subCmd = CmdKit.getSubCmd(cmdMerge);
        return getActionCommand(cmd, subCmd);
    }

    /**
     * 命令列表
     */
    public List<Integer> listCmdMerge() {
        return actionCommandRegionMap.values()
                // 并发流
                .parallelStream()
                // 将 map.values 合并成一个 list
                .flatMap((Function<ActionCommandRegion, Stream<ActionCommand>>) actionCommandRegion -> actionCommandRegion.values().stream())
                // 转为命令路由信息
                .map(ActionCommand::getCmdInfo)
                // 转为 合并的路由
                .map(CmdInfo::getCmdMerge)
                .collect(Collectors.toList());
    }

    void initActionCommandArray(BarSkeletonSetting barSkeletonSetting) {
        this.actionCommands = this.convertArray(barSkeletonSetting);
    }

    /**
     * 将 map 转换成二维数组
     * <pre>
     *     第一维: cmd
     *     第二维: cmd 下面的子 subCmd
     * </pre>
     *
     * @param barSkeletonSetting config
     * @return 二维数组
     */
    private ActionCommand[][] convertArray(BarSkeletonSetting barSkeletonSetting) {

        if (this.actionCommandRegionMap.isEmpty()) {
            return EMPTY;
        }

        // 获取主路由最大值
        int max = getMaxCmd(barSkeletonSetting);

        var behaviors = new ActionCommand[max][1];

        this.actionCommandRegionMap.keySet().forEach(cmd -> {
            var actionCommandRegion = this.actionCommandRegionMap.get(cmd);

            behaviors[cmd] = actionCommandRegion.arrayActionCommand();
        });

        return behaviors;
    }

    private int getMaxCmd(BarSkeletonSetting barSkeletonSetting) {
        // 获取最大的路由数字 并且+1
        int max = this.actionCommandRegionMap
                .keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(0) + 1;

        if (max > barSkeletonSetting.getCmdMaxLen()) {

            String info = String.format("cmd 超过最大默认值! 如果有需要, 请手动设置容量!  默认最大容量 %s. 当前容量 %s"
                    , barSkeletonSetting.getCmdMaxLen(), max
            );

            throw new RuntimeException(info);
        }

        // subCmd
        for (ActionCommandRegion actionCommandRegion : this.actionCommandRegionMap.values()) {
            int subCmdMax = actionCommandRegion.getMaxSubCmd() + 1;

            if (subCmdMax > barSkeletonSetting.getSubCmdMaxLen()) {

                String info = String.format("subCmd 超过最大默认值! 如果有需要, 请手动设置容量!  默认最大容量 %s. 当前容量 %s"
                        , barSkeletonSetting.getSubCmdMaxLen(), subCmdMax
                );

                throw new RuntimeException(info);
            }
        }

        return max;
    }

}
