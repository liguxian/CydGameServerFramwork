package com.cyd.gameserver.common.kit.system;

import com.cyd.gameserver.common.consts.LogName;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统属性工具包
 */
@UtilityClass
@Slf4j(topic = LogName.CommonStdout)
public class InternalSystemPropsKit {

    public String get(String name, boolean quiet) {
        String value = null;
        try{
            value = System.getProperty(name);
        } catch (SecurityException e) {
            if(!quiet) {
                log.error("Caught a SecurityException reading the system property '{}'; the SystemPropsKit property value will default to null.", name);
            }
        }

        if(value == null){
            try{
                value = System.getenv(name);
            } catch (SecurityException e) {
                if(!quiet) {
                    log.error("Caught a SecurityException reading the system env '{}'; the SystemPropsKit env value will default to null.", name);
                }
            }
        }

        return value;
    }

}
