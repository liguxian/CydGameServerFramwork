package com.cyd.gameserver.action.skeleton.ext.spring;

import com.cyd.gameserver.action.skeleton.core.ActionCommand;
import com.cyd.gameserver.action.skeleton.core.ActionFactoryBean;
import com.cyd.gameserver.action.skeleton.core.DependencyInjectionPart;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * spring集成
 * <pre>
 *     把 action 交由 spring 管理
 *
 *     对于 action 的解释可以参考这里:
 *     https://www.yuque.com/iohao/game/sqcevl
 * </pre>
 *
 * @author 渔民小镇
 * @date 2022-03-22
 */
public class ActionFactoryBeanForSpring<T> implements ActionFactoryBean<T>, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Getter
    boolean spring;
    @Override
    public T getBean(ActionCommand actionCommand) {
        Class<?> actionControllerClazz = actionCommand.getActionControllerClazz();
        return (T) applicationContext.getBean(actionControllerClazz);
    }

    @Override
    public T getBean(Class<?> actionControllerClazz) {
        return (T) applicationContext.getBean(actionControllerClazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Objects.requireNonNull(applicationContext);
        initDependencyInjectionPart();
        spring = true;
        this.applicationContext = applicationContext;
    }

    private void initDependencyInjectionPart() {
        DependencyInjectionPart dependencyInjectionPart = DependencyInjectionPart.me();
        dependencyInjectionPart.setInjection(true);
        dependencyInjectionPart.setAnnotationClass(Component.class);
        dependencyInjectionPart.setActionFactoryBean(this);
    }

    private ActionFactoryBeanForSpring() {
    }

    public static ActionFactoryBeanForSpring me() {
        return Holder.ME;
    }

    /** 通过 JVM 的类加载机制, 保证只加载一次 (singleton) */
    private static class Holder {
        static final ActionFactoryBeanForSpring ME = new ActionFactoryBeanForSpring();
    }
}
