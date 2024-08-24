package com.cyd.gameserver.action.skeleton.core;

import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;

/**
 * 依赖注入的部分
 * <pre>
 *     通常用于集成到第三方框架，如：
 *     spring
 * </pre>
 *
 * @author 渔民小镇
 * @date 2022-10-25
 */
@Getter
@Setter
public class DependencyInjectionPart {

    /** true 与第三方框架集成 */
    boolean injection;

    /**
     * 容器管标签
     * <pre>
     *     比如 spring 可以使用 Component 来标记类是交给容器管理的；
     *
     *     虽然 spring 还支持其他的注解来标记类可以交给容器管理，
     *     但 ioGame 只推荐大家使用统一的一个就好了；
     *
     *     如果要把 ioGame 集成到其他框架中，也是大概类似的处理方式；
     * </pre>
     */
    Class<? extends Annotation> annotationClass;

    ActionFactoryBean<?> actionFactoryBean;

    /**
     * controller是否交给容器管理
     * @param controllerClass
     * @return
     */
    public boolean deliveryContainer(Class<?> controllerClass) {
        return controllerClass.getAnnotation(annotationClass) != null;
    }

    /**
     * 获取bean实例
     */
    public <T> T getBean(ActionCommand actionCommand) {
        return (T) actionFactoryBean.getBean(actionCommand);
    }

    public <T> T getBean(Class<?> controllerClass) {
        return (T) actionFactoryBean.getBean(controllerClass);
    }

    private DependencyInjectionPart(){}

    public static DependencyInjectionPart me(){
        return Holder.ME;
    }

    private class Holder {
        private static DependencyInjectionPart ME = new DependencyInjectionPart();
    }
}
