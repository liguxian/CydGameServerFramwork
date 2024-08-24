package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.action.skeleton.core.flow.FlowContext;
import com.cyd.gameserver.action.skeleton.core.flow.parser.MethodParser;
import com.cyd.gameserver.action.skeleton.core.flow.parser.MethodParsers;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 命令信息
 */
@Getter
public class ActionCommand {

    /**
     * 命令码信息
     */
    final CmdInfo cmdInfo;

    /**
     * 构造方法访问器
     */
    final ConstructorAccess<?> actionControllerConstructorAccess;

    /**
     * 单例controller对象
     */
    final Object actionController;

    /**
     * 方法所在class对象
     */
    final Class<?> actionControllerClazz;

    /**
     * controller对象是否是单例 默认true
     */
    final boolean createSingleActionCommandController;

    /**
     * 方法对象
     */
    final Method actionMethod;

    /**
     * 方法名
     */
    final String actionMethodName;

    /**
     * 方法下标
     */
    final int actionMethodIndex;

    /**
     * 方法访问器
     */
    final MethodAccess actionMethodAccess;

    /**
     * 方法是否有参数
     */
    final boolean methodHasParam;

    /**
     * 方法参数信息
     */
    final ParamInfo[] paramInfos;

    /**
     * 方法是否有异常抛出, 一般是错误码: true 有异常
     */
    final boolean throwException;
    /**
     * 返回类型
     */
    final ActionMethodReturnInfo actionMethodReturnInfo;

    /**
     * true 表示交付给容器来管理 如 spring 等
     */
    boolean deliveryContainer;

    private ActionCommand(Builder builder) {
// -------------- 路由相关 --------------
        this.cmdInfo = CmdInfoFlyweightFactory.of(builder.cmd, builder.subCmd);

        // -------------- 控制器相关 --------------
        this.actionControllerClazz = builder.actionControllerClazz;
        this.actionControllerConstructorAccess = builder.actionControllerConstructorAccess;
        this.actionController = builder.actionController;
        this.createSingleActionCommandController = builder.createSingleActionCommandController;

        // -------------- 控制器-方法相关 --------------
        this.actionMethod = builder.actionMethod;
        this.actionMethodName = builder.actionMethodName;
        this.actionMethodIndex = builder.actionMethodIndex;
        this.actionMethodAccess = builder.actionMethodAccess;

        // -------------- 控制器-方法参数相关 --------------
        this.paramInfos = builder.paramInfos;
        this.methodHasParam = builder.paramInfos != null;
        this.throwException = builder.actionMethod.getExceptionTypes().length != 0;
        this.actionMethodReturnInfo = new ActionMethodReturnInfo(builder);

        this.deliveryContainer = builder.deliveryContainer;
    }

    @Setter
    @Accessors(chain = true)
    @FieldDefaults(level = AccessLevel.PUBLIC)
    static final class Builder {
        /** 目标路由 */
        int cmd;
        /** 目标子路由 */
        int subCmd;
        /** 方法访问器 */
        MethodAccess actionMethodAccess;
        /** 类访问器 */
        ConstructorAccess<?> actionControllerConstructorAccess;
        /** 方法名 */
        String actionMethodName;
        /** tcp controller类 */
        Class<?> actionControllerClazz;
        /** 方法对象 */
        Method actionMethod;
        /** 参数列表信息 */
        ActionCommand.ParamInfo[] paramInfos;
        /** 方法下标 (配合 MethodAccess 使用) */
        int actionMethodIndex;
        /** 返回值信息 */
        Class<?> returnTypeClazz;
        /** true 表示交付给容器来管理 如 spring 等 */
        boolean deliveryContainer;
        /** 默认:true ，action 对象是 single. 如果设置为 false, 每次创建新的 action 类的对象. */
        boolean createSingleActionCommandController;
        /** 一个single控制器对象 */
        Object actionController;

        ActionCommand build(){
            return new ActionCommand(this);
        }
    }

    /**
     * action 方法中的参数与返回值信息
     */
    public interface MethodParamResultInfo {
        /**
         * 参数是否是list
         *
         * @return
         */
        default boolean isList() {
            return false;
        }

        /**
         * List 泛型的类型，也称为方法返回值类型
         * <pre>
         *     如果不是方法的返回值不是 List 类型，这个值会取自 paramClazz 成员属性
         *     原计划想用 Collection ，这样可以兼容 Set 之类的；但似乎这样有一点争议，先暂支持 List 把
         * </pre>
         *
         * @return List 泛型的类型
         */
        Class<?> getActualTypeArgumentClazz();
    }

    /**
     * 方法参数信息
     */
    @Getter
    public static final class ParamInfo implements MethodParamResultInfo {
        /**
         * 参数名称
         */
        final String name;

        /**
         * 下标
         */
        final int index;

        /**
         * Parameter对象
         */
        final Parameter parameter;

        /**
         * 参数类型 如List<String>, 则值为List
         */
        final Class<?> paramClazz;

        /**
         * List 泛型的类型，也称为方法返回值类型 如List<String>, 则值为String
         * <pre>
         *     如果方法的返回值不是 List 类型，这个值会取自 paramClazz 成员属性
         *     原计划想用 Collection ，这样可以兼容 Set 之类的；但似乎这样有一点争议，先暂支持 List
         * </pre>
         */
        final Class<?> actualTypeArgumentClazz;

        /**
         * true 是 list 类型
         */
        final boolean list;

        /**
         * 实际的内置包装类型
         * <pre>
         *     常规的 java class 是指本身，如：
         *     开发者自定义了一个 StudentPb，在 action 方法上参数声明为 xxx(StudentPb studentPb)
         *     那么这个值就是 StudentPb
         * </pre>
         *
         * <pre>
         *     但由于框架现在内置了一些包装类型，如：
         *     int --> IntValue
         *     List&lt;Integer&gt; --> IntValueList
         *
         *     long --> LongValue
         *     List&lt;Long&gt; --> LongValueList
         *
         *     所以当开发者在 action 方法上参数声明为基础类型时；
         *     如声明为 int 那么这个值将会是 IntValue
         *     如声明为 long 那么这个值将会是 LongValue
         *
         *     如声明为 List&lt;Integer&gt; 那么这个值将会是 IntValueList
         *     包装类型相关的以此类推;
         *
         *     这么做的目的是为了生成文档时，不与前端产生争议，
         *     如果提供给前端的文档显示 int ，或许前端同学会懵B，
         *     当然如果提前与前端同学沟通好这些约定，也不是那么麻烦。
         *     但实际上如果前端是新来接手项目的，碰见这种情况也会小懵，
         *     所以为了避免这些小尬，框架在生成文档时，用基础类型的内置包装类型来表示。
         * </pre>
         */
        final Class<?> actualClazz;
        /**
         * 是否是FlowContext
         */
        final boolean extension;
        final boolean customMethodParser;

        ParamInfo(int index, Parameter p) {
            this.index = index;
            this.parameter = p;
            this.paramClazz = p.getType();
            this.name = p.getName();

            //是否是list
            if (List.class.isAssignableFrom(paramClazz)) {
                ParameterizedType genericType = (ParameterizedType) p.getParameterizedType();
                this.actualTypeArgumentClazz = (Class<?>) genericType.getActualTypeArguments()[0];
                this.list = true;
            } else {
                this.actualTypeArgumentClazz = paramClazz;
                this.list = false;
            }

            MethodParser methodParser = MethodParsers.getMethodParser(this);
            this.actualClazz = methodParser.getActualClazz(this);
            this.customMethodParser = methodParser.isCustomMethodParser();

            this.extension = FlowContext.class.isAssignableFrom(paramClazz);
        }

        /**
         * 是否扩展属性
         *
         * @return true 是扩展属性
         */
        public boolean isExtension() {
            return extension;
        }

    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static final class ActionMethodReturnInfo implements MethodParamResultInfo {

        /**
         * 返回值类型
         */
        final Class<?> returnTypeClazz;

        /**
         * 泛型类型
         */
        final Class<?> actualTypeArgumentClazz;

        /**
         * true 是 list 类型
         */
        final boolean list;

        /**
         * 实际的内置包装类型
         * <pre>
         *     常规的 java class 是指本身，如：
         *     开发者自定义了一个 StudentPb，在 action 方法上参数声明为 xxx(StudentPb studentPb)
         *     那么这个值就是 StudentPb
         * </pre>
         *
         * <pre>
         *     但由于框架现在内置了一些包装类型，如：
         *     int --> IntValue
         *     List&lt;Integer&gt; --> IntValueList
         *
         *     long --> LongValue
         *     List&lt;Long&gt; --> LongValueList
         *
         *     所以当开发者在 action 方法上参数声明为基础类型时；
         *     如声明为 int 那么这个值将会是 IntValue
         *     如声明为 long 那么这个值将会是 LongValue
         *
         *     如声明为 List&lt;Integer&gt; 那么这个值将会是 IntValueList
         *     包装类型相关的以此类推;
         *
         *     这么做的目的是为了生成文档时，不与前端产生争议，
         *     如果提供给前端的文档显示 int ，或许前端同学会懵B，
         *     当然如果提前与前端同学沟通好这些约定，也不是那么麻烦。
         *     但实际上如果前端是新来接手项目的，碰见这种情况也会小懵，
         *     所以为了避免这些小尬，框架在生成文档时，用基础类型的内置包装类型来表示。
         * </pre>
         */
        final Class<?> actualClazz;

        final boolean customMethodParser;

        private ActionMethodReturnInfo(Builder builder) {
            this.returnTypeClazz = builder.returnTypeClazz;

            if (List.class.isAssignableFrom(returnTypeClazz)) {
                ParameterizedType genericReturnType = (ParameterizedType) builder.actionMethod.getGenericReturnType();
                this.actualTypeArgumentClazz = (Class<?>) genericReturnType.getActualTypeArguments()[0];
                this.list = true;
            } else {
                this.actualTypeArgumentClazz = returnTypeClazz;
                this.list = false;
            }

            MethodParser methodParser = MethodParsers.getMethodParser(this);
            this.actualClazz = methodParser.getActualClazz(this);
            this.customMethodParser = methodParser.isCustomMethodParser();
        }

        /**
         * 方法返回值类型是否 void
         *
         * @return true 是 void
         */
        public boolean isVoid() {
            return Void.TYPE == this.returnTypeClazz;
        }
    }

}
