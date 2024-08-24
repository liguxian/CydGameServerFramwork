package com.cyd.gameserver.action.skeleton.core.flow.parser;

import com.cyd.gameserver.action.skeleton.core.ActionCommand;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MethodParsers {

    static Map<Class<?>, MethodParser> methodParserMap = new HashMap<>();

    static {
        init();
    }

    static MethodParser methodParser = DefaultMethodParser.me();

    private static void init() {
        // 表示在 action 参数中，遇见 int 类型的参数，用 IntValueMethodParser 来解析
        mapping(int.class, IntValueMethodParser.me());
        mapping(Integer.class, IntValueMethodParser.me());

        // 表示在 action 参数中，遇见 long 类型的参数，用 LongValueMethodParser 来解析
        mapping(long.class, LongValueMethodParser.me());
        mapping(Long.class, LongValueMethodParser.me());

        // 表示在 action 参数中，遇见 String 类型的参数，用 StringValueMethodParser 来解析
        mapping(String.class, StringValueMethodParser.me());

        // 表示在 action 参数中，遇见 boolean 类型的参数，用 BoolValueMethodParser 来解析
        mapping(boolean.class, BoolValueMethodParser.me());
        mapping(Boolean.class, BoolValueMethodParser.me());
    }

    public static void mapping(Class<?> paramClass, MethodParser methodParamParser) {
        methodParserMap.put(paramClass, methodParamParser);
    }

    public static MethodParser getMethodParser(ActionCommand.MethodParamResultInfo paramInfo) {
        Class<?> actualTypeArgumentClazz = paramInfo.getActualTypeArgumentClazz();
        return getMethodParser(actualTypeArgumentClazz);
    }

    public static MethodParser getMethodParser(Class<?> paramClazz) {
        return methodParserMap.getOrDefault(paramClazz, methodParser);
    }
}
