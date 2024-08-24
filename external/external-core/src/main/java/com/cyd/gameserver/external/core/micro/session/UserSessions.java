package com.cyd.gameserver.external.core.micro.session;

import com.cyd.gameserver.common.kit.attr.AttrOptionDynamic;
import com.cyd.gameserver.external.core.kit.hook.UserHook;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public interface UserSessions<SessionContext, Session extends UserSession> extends AttrOptionDynamic {

    /**
     * 添加session
     *
     * @param sessionContext
     * @return
     */
    Session add(SessionContext sessionContext);

    /**
     * 获取userSession
     */
    Session getUserSession(SessionContext sessionContext);

    /**
     * 获取userSession
     */
    Session getUserSession(UserChannelId userChannelId);

    /**
     * 获取userSession
     */
    Session getUserSession(long userId);

    /**
     * 如果userSession存在， 则对其执行指定操作，否则不执行
     */
    default void ifPresent(long userId, Consumer<Session> consumer) {
        Session userSession = getUserSession(userId);
        if (Objects.nonNull(userSession)) {
            consumer.accept(userSession);
        }
    }

    /**
     * 批量
     * 如果userSession存在， 则对其执行指定操作，否则不执行
     */
    default void ifPresent(Collection<Long> userIds, Consumer<Session> consumer) {
        userIds.stream()
                .map(this::getUserSession)
                .filter(Objects::nonNull)
                .forEach(consumer)
        ;
    }

    /**
     * userSession是否存在
     */
    boolean exist(long userId);

    /**
     * 给userSession设置userId
     */
    boolean setUserId(UserChannelId userChannelId, long userId);

    /**
     * 移除UserSession
     */
    void remove(Session session);

    /**
     * 根据userId移除UserSession， 并发送一个消息
     */
    void remove(long userId, Object msg);

    /**
     * 设置UserHook
     */
    void setUserHook(UserHook userHook);

    /**
     * 当前在线人数
     */
    int countOnline();

    /**
     * 全体广播消息
     */
    void broadCast(Object msg);

    /**
     * 遍历所有玩家
     */
    void foreach(Consumer<Session> consumer);
}
