package com.cyd.gameserver.action.skeleton.kit;

import com.cyd.gameserver.action.skeleton.core.BarSkeletonBuilder;
import com.cyd.gameserver.action.skeleton.core.BarSkeletonBuilderParamConfig;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LogicServerCreateKit {

    public BarSkeletonBuilder createBuilder(Class<?> actionControllerClass) {
        BarSkeletonBuilderParamConfig barSkeletonBuilderParamConfig = new BarSkeletonBuilderParamConfig().scanPackage(actionControllerClass);
        return createBuilder(barSkeletonBuilderParamConfig);
    }

    public BarSkeletonBuilder createBuilder(BarSkeletonBuilderParamConfig config) {
        BarSkeletonBuilder skeletonBuilder = config.createSkeletonBuilder();

        //todo 添加插件

        return skeletonBuilder;
    }
}
