package com.github.fengleicn;

import cn.hutool.core.util.ReflectUtil;

public class ClassLoadUtil {
    public static Class<?> load(String className, byte[] bytes) {
        return ReflectUtil.invoke(Thread.currentThread().getContextClassLoader(),
                "defineClass",
                className.replace("/", "."),
                bytes,
                0,
                bytes.length);
    }
}
