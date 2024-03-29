package org.example.spring.utils;

import org.example.spring.BeanDefinition;
import org.example.spring.BeanPostProcessor;
import org.example.spring.InitializingBean;
import org.example.spring.annotation.Autowired;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * -03/29-0:24
 * -创建Bean的工具类
 */
public class CreateBeanUtils {

    /**
     * 创建bean对象
     * @param beanDefinition bean的定义
     * @return Object 创建好的bean对象
     */
    public static <T> Object createBean(BeanDefinition beanDefinition, Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects, List<BeanPostProcessor> list) {
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
            // 注意：这个分支永远不会执行，可以删除，但是为了方便理解代码，在此保留

            if (isEmpty(constructors)) {
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
            /*
             * 处理字段注入
             * 先AutowiredByName后AutowiredByType
             */
            // 获取bean的所有自定义属性
            Field[] fields = beanType.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    // 获取bean
                    // 通过bean名称获取bean
                    Object autowiredBean = GetBeanUtils.getBean(field.getName(), beanDefinitionMap, singletonObjects,list);

                    if (autowiredBean == null) {
                        // 获取字段类型
                        Class<?> type = field.getType();

                        autowiredBean = GetBeanUtils.getBean((Class<T>) type, beanDefinitionMap, singletonObjects, list);
                    }
                    // 设置到@Autowired注入的属性中
                    field.setAccessible(true);
                    field.set(bean, autowiredBean);
                }
            }
            /**
             * 初始化前
             */
            String beanName = GetBeanUtils.getBeanName(beanDefinition.getType());
            if (!list.isEmpty()) {
                for (BeanPostProcessor beanPostProcessor : list) {
                    bean = beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
                }
            }

            // 调用 InitializingBean的afterPropertiesSet() 方法
            if (bean instanceof InitializingBean) {
                ((InitializingBean) bean).afterPropertiesSet();
            }

            if (!list.isEmpty()) {
                for (BeanPostProcessor beanPostProcessor : list) {
                    bean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
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
