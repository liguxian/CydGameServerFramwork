package com.cyd.gameserver.bolt.core.common.processor.hook;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * bolt 业务处理器的钩子管理器
 * <pre>
 *     在构建游戏逻辑服赋值
 *
 *
 * </pre>
 *
 * @author 渔民小镇
 * @date 2022-06-26
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientProcessorHooks {
    /**
     * 逻辑服业务处理钩子接口
     * <pre>
     *     通过业务框架把请求派发给指定的业务类（action）来处理
     * </pre>
     */
    RequestMessageClientProcessorHook requestMessageClientProcessorHook = new DefaultRequestMessageClientProcessorHook();
}
