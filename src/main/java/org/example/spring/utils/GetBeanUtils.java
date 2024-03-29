package org.example.spring.utils;

import org.example.spring.BeanDefinition;
import org.example.spring.exception.NoSuchBeanException;
import org.example.spring.exception.TooMuchBeanException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * -03/29-12:38
 * -
 */
public class GetBeanUtils {

    /**
     * 通过beanName查找
     * @param beanName bean名字
     * @return 返回bean实例
     */
    public static Object getBean(String beanName, Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects) {
        if (!beanDefinitionMap.containsKey(beanName)) {
            return null;
        }

        return getBean(beanName, beanDefinitionMap.get(beanName), beanDefinitionMap, singletonObjects);
    }

    /**
     * 使用默认构造器
     * @param type bean类型
     * @return T
     */
    public static <T> T getBean(Class<T> type,Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects) {
        if (type == null) {
            throw new IllegalStateException("bean类型不能为空！");
        }

        // 保存指定类型的bean的个数
        AtomicInteger count = new AtomicInteger();
        // 保存同一类型的bean
        Map<String, BeanDefinition> objectMap = new HashMap<>();

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            Class beanType = beanDefinition.getType();
            String beanName = entry.getKey();

            if (beanType.equals(type)) {
                count.addAndGet(1);
                objectMap.put(beanName, beanDefinition);
            }
        }

        if (count.get() == 0) {
            throw new NoSuchBeanException();
        } else if (count.get() > 1) {
            throw new TooMuchBeanException();
        } else {
            return (T) getBean((String) objectMap.keySet().toArray()[0], (BeanDefinition) objectMap.values().toArray()[0], beanDefinitionMap, singletonObjects);
        }
    }

    /**
     * 通过类型和beanName获取对应的bean对象
     * @param beanName bean名字
     * @param type bean的类型
     * @return 需要的bean类型
     */
    public static <T> T getBean(String beanName, Class<T> type, Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects) {
        if (type == null) {
            throw new IllegalStateException("bean 类型不能为空！ ");
        }
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (type.equals(beanDefinition.getType())) {
                return (T) getBean(beanName, beanDefinition, beanDefinitionMap, singletonObjects);
            }
        }
        throw new NoSuchBeanException();
    }


    /**
     * 统一获取bean的方法
     * @param beanName bean名称
     * @param beanDefinition BeanDefinition
     * @return Object 符合条件的bean对象
     */
    private static Object getBean(String beanName, BeanDefinition beanDefinition,Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects) {
        String scope = beanDefinition.getScope();

        // bean的作用域是单例
        if (CreateBeanUtils.isSingleton(scope)) {
            Object object = singletonObjects.get(beanName);

            // 懒加载的单例bean
            if (object == null) {
                Object bean = CreateBeanUtils.createBean(beanDefinition,beanDefinitionMap, singletonObjects);

                singletonObjects.put(beanName, bean);
            }

            return singletonObjects.get(beanName);
        }
        // 创建bean对象
        return CreateBeanUtils.createBean(beanDefinition, beanDefinitionMap, singletonObjects);
    }
}
