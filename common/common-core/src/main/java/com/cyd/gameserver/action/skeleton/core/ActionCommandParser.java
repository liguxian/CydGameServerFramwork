package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.action.skeleton.annotation.ActionController;
import com.cyd.gameserver.action.skeleton.annotation.ActionMethod;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActionCommandParser {

    @Getter
    final ActionCommandRegions actionCommandRegions = new ActionCommandRegions();

    final BarSkeletonSetting barSkeletonSetting;

    ActionCommandParser(BarSkeletonSetting skeletonSetting) {
        barSkeletonSetting = skeletonSetting;
    }

    public ActionCommandParser buildAction(List<Class<?>> actionControllerClazz) {
        actionControllerClazz.stream().filter(clazz -> Objects.nonNull(clazz.getAnnotation(ActionController.class)))
                .forEach(controllerClazz -> {
                    // 方法访问器: 获取类中自己定义的方法
                    MethodAccess methodAccess = MethodAccess.get(controllerClazz);
                    ConstructorAccess<?> constructorAccess = ConstructorAccess.get(controllerClazz);

                    //主路由
                    int cmd = controllerClazz.getAnnotation(ActionController.class).value();
                    ActionCommandRegion actionCommandRegion = actionCommandRegions.getActionCommandRegion(cmd);

                    //controller是否交给了容器管理
                    boolean deliveryContainer = deliveryContainer(controllerClazz);
                    // action 类的实例化对象
                    Object actionControllerInstance = ofInstance(controllerClazz);

                    //获取并遍历所有方法
                    Arrays.stream(controllerClazz.getDeclaredMethods())
                            // 得到在业务方法上添加了 ActionMethod 注解的方法对象
                            .filter(method -> Objects.nonNull(method.getAnnotation(ActionMethod.class)))
                            // 访问权限必须是 public 的
                            .filter(method -> Modifier.isPublic(method.getModifiers()))
                            // 不能是静态方法的
                            .filter(method -> !Modifier.isStatic(method.getModifiers()))
                            .forEach(method -> {
                                //子路由信息
                                int subCmd = method.getAnnotation(ActionMethod.class).value();
                                //方法名
                                String name = method.getName();
                                //方法下标
                                int index = methodAccess.getIndex(name);
                                //方法返回值类型
                                Class returnType = methodAccess.getReturnTypes()[index];

                                ActionCommand.Builder builder = new ActionCommand.Builder()
                                        .setCmd(cmd)
                                        .setSubCmd(subCmd)
                                        .setActionMethodAccess(methodAccess)
                                        .setActionControllerConstructorAccess(constructorAccess)
                                        .setActionMethodName(name)
                                        .setActionControllerClazz(controllerClazz)
                                        .setActionController(actionControllerInstance)
                                        .setActionMethod(method)
                                        .setActionMethodIndex(index)
                                        .setReturnTypeClazz(returnType)
                                        .setDeliveryContainer(deliveryContainer)
                                        .setCreateSingleActionCommandController(barSkeletonSetting.isCreateSingleActionCommandController());

                                //检查子路由是否重复
                                checkExistSubCmd(controllerClazz, subCmd, actionCommandRegion);

                                //参数信息
                                paramInfo(method, builder);

                                ActionCommand actionCommand = builder.build();

                                actionCommandRegion.add(actionCommand);
                            });
                });

        // 内部将所有的 action 转换为 action 二维数组
        actionCommandRegions.initActionCommandArray(barSkeletonSetting);

        return this;
    }

    /**
     * controller是否交给了容器管理
     */
    private boolean deliveryContainer(Class<?> controllerClass) {
        if (DependencyInjectionPart.me().isInjection()) {
            return DependencyInjectionPart.me().deliveryContainer(controllerClass);
        }

        return false;
    }

    /**
     * 获取class实例
     */
    private Object ofInstance(Class<?> clazz) {
        // 如果 actionController 交给容器管理了，就从容器中获取实例，否则就 newInstance
        boolean deliveryContainer = deliveryContainer(clazz);

        Object instance = deliveryContainer ? DependencyInjectionPart.me().getBean(clazz) : ConstructorAccess.get(clazz).newInstance();

        Objects.requireNonNull(instance);

        return instance;
    }

    /**
     * 设置actionCommand的参数信息
     */
    private void paramInfo(Method method, ActionCommand.Builder actionCommandBuilder) {
        Parameter[] parameters = method.getParameters();

        ActionCommand.ParamInfo[] paramInfos = new ActionCommand.ParamInfo[parameters.length];
        actionCommandBuilder.setParamInfos(paramInfos);

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            ActionCommand.ParamInfo paramInfo = new ActionCommand.ParamInfo(i, parameter);
            paramInfos[i] = paramInfo;
        }
    }

    /**
     * 检查子路由是否重复
     */
    private void checkExistSubCmd(Class<?> controllerClass, int subCmd, ActionCommandRegion actionCommandRegion) {

        if (actionCommandRegion.containsKey(subCmd)) {

            String message = String.format("cmd:【{}】下已经存在方法编号 subCmd:【{}】 .请查看: {}",
                    actionCommandRegion.cmd,
                    subCmd,
                    controllerClass);

            throw new RuntimeException(message);
        }
    }
}
