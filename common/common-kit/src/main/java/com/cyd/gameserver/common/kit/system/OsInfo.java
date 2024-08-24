package com.cyd.gameserver.common.kit.system;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * 操作系统信息单例类
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OsInfo {

    final String osName = InternalSystemPropsKit.get("os.name", false);
    final boolean linux = getOsMatches("Linux") || getOsMatches("LINUX");
    final boolean mac = getOsMatches("Mac") || getOsMatches("Mac OS X");

    private boolean getOsMatches(String osName) {
        if (this.osName == null) {
            return false;
        }

        return this.osName.startsWith(osName);
    }

    //静态内部类单例
    private OsInfo() {
    }

    public static OsInfo me() {
        return Holder.me;
    }

    private static class Holder {
        static OsInfo me = new OsInfo();
    }
}
