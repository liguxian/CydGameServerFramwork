package com.cyd.gameserver.action.skeleton.core;

import com.cyd.gameserver.action.skeleton.annotation.ActionController;
import com.cyd.gameserver.common.kit.ClassScanner;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Setter
@Getter
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BarSkeletonBuilderParamConfig {

    final List<Class<?>> actionControllerClassList = new ArrayList<>();

    final Predicate<Class<?>> classPredicate = clazz -> Objects.nonNull(clazz.getAnnotation(ActionController.class));

    public BarSkeletonBuilder createSkeletonBuilder() {
        BarSkeletonBuilder barSkeletonBuilder = new BarSkeletonBuilder();

        scanActionControllerClass(barSkeletonBuilder::addActionController);

        return barSkeletonBuilder;
    }

    public BarSkeletonBuilderParamConfig scanPackage(Class<?> clazz) {
        actionControllerClassList.add(clazz);
        return this;
    }

    private void scanActionControllerClass(Consumer<Class<?>> consumer) {
        scanClass(actionControllerClassList, classPredicate, consumer);
    }

    private void scanClass(List<Class<?>> classList, Predicate<Class<?>> classPredicate, Consumer<Class<?>> consumer) {
        for (Class<?> clazz : classList) {
            String packagePath = clazz.getPackageName();
            ClassScanner classScanner = new ClassScanner(packagePath, classPredicate);
            List<Class<?>> classes = classScanner.listScan();

            // 将扫描好的 class 添加到业务框架中
            classes.forEach(consumer);
        }
    }
}
