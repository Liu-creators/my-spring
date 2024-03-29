package org.example.spring.utils;

import org.example.spring.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * -03/29-0:24
 * -
 */
public class CreateBeanUtils {

    /**
     * 创建bean对象
     * @param beanDefinition bean的定义
     * @return Object 创建好的bean对象
     */
    public static Object createBean(BeanDefinition beanDefinition) {
        Object bean = null;
        Class<?> beanType = beanDefinition.getType();

        // 获取所有构造方法
        Constructor<?>[] constructors = beanType.getConstructors();

        try {
            /*
             * 推断构造方法
             * 1、没有提供构造方法：调用默认的无参构造
             * 2、提供了构造方法：
             *   - 构造方法个数为1
             *     - 构造方法参数个数为0：无参构造
             *     - 构造方法参数个数不为0：传入多个为空的参数
             *   - 构造方法个数 > 1：
             *     - 提供了无参构造方法：调用无参构造方法实例化
             *     - 没有提供无参构造方法：推断失败，抛出异常
             */
            if (isEmpty(constructors)) { // 注意：这个分支永远不会执行，可以删除，但是为了方便理解代码，在此保留
                // 无参构造方法
                Constructor<?> constructor = beanType.getConstructor();

                bean = constructor.newInstance();
            } else if (constructors.length == 1) {
                Constructor<?> constructor = constructors[0];
                // 得到构造方法参数个数
                int parameterCount = constructor.getParameterCount();

                if (parameterCount == 0) {
                    // 无参构造方法
                    bean = constructor.newInstance();
                } else {
                    // 多个参数的构造方法
                    Object[] array = new Object[parameterCount];

                    bean = constructor.newInstance(array);
                }
            } else {
                boolean success = false;

                for (Constructor<?> constructor : constructors) {
                    if (constructor.getParameterCount() == 0) {
                        bean = constructor.newInstance();
                        success = true;
                        break;
                    }
                }

                if (!success) {
                    throw new IllegalStateException("No default constructor found.");
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return bean;
    }

    private static boolean isEmpty(Object[] array) {
        return array.length == 0;
    }

    /**
     * 判断作用域是否单例
     * @param scope bean的作用域
     * @return boolean 如果是单例，返回true，否则返回false
     */
    public static boolean isSingleton(String scope) {
        return "singleton".equals(scope);
    }

}
