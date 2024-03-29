package org.example.spring.utils;

import org.example.spring.BeanDefinition;
import org.example.spring.BeanPostProcessor;
import org.example.spring.exception.NoSuchBeanException;
import org.example.spring.exception.TooMuchBeanException;

import java.util.HashMap;
import java.util.List;
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
    public static Object getBean(String beanName, Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects, List<BeanPostProcessor> list) {
        if (!beanDefinitionMap.containsKey(beanName)) {
            return null;
        }

        return getBean(beanName, beanDefinitionMap.get(beanName), beanDefinitionMap, singletonObjects, list);
    }

    /**
     * 使用默认构造器
     * @param type bean类型
     * @return T
     */
    public static <T> T getBean(Class<T> type,Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects, List<BeanPostProcessor> list) {
        if (type == null) {
            throw new IllegalStateException("bean类型不能为空！");
        }

        // 保存指定类型的bean的个数
        AtomicInteger count = new AtomicInteger();
        // 保存同一类型的bean
        Map<String, BeanDefinition> objectMap = new HashMap<>();

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getValue();
            Class<?> beanType = beanDefinition.getType();
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
            return (T) getBean((String) objectMap.keySet().toArray()[0], (BeanDefinition) objectMap.values().toArray()[0], beanDefinitionMap, singletonObjects, list);
        }
    }

    /**
     * 通过类型和beanName获取对应的bean对象
     * @param beanName bean名字
     * @param type bean的类型
     * @return 需要的bean类型
     */
    public static <T> T getBean(String beanName, Class<T> type, Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects, List<BeanPostProcessor> list) {
        if (type == null) {
            throw new IllegalStateException("bean 类型不能为空！ ");
        }
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (type.equals(beanDefinition.getType())) {
                return (T) getBean(beanName, beanDefinition, beanDefinitionMap, singletonObjects, list);
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
    private static Object getBean(String beanName, BeanDefinition beanDefinition,Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects, List<BeanPostProcessor> list) {
        String scope = beanDefinition.getScope();

        // bean的作用域是单例
        if (CreateBeanUtils.isSingleton(scope)) {
            Object object = singletonObjects.get(beanName);

            // 懒加载的单例bean
            if (object == null) {
                Object bean = CreateBeanUtils.createBean(beanDefinition,beanDefinitionMap, singletonObjects, list);

                singletonObjects.put(beanName, bean);
            }

            return singletonObjects.get(beanName);
        }
        // 创建bean对象
        return CreateBeanUtils.createBean(beanDefinition, beanDefinitionMap, singletonObjects, list);
    }

    /**
     * 根据类对象获取beanName
     * @param clazz 类对象
     * @return beanName
     */
    public static String getBeanName(Class<?> clazz) {
        String beanName = clazz.getSimpleName();

        // 判断是否以双大写字母开头（查找连续的两个大写字母，并在其中间添加_）
        String className = beanName.replaceAll("([A-Z])([A-Z])", "$1_$2");

        // 正常的大驼峰命名：bean名称为类名首字母大写
        if (className.indexOf("_") != 1) {
            beanName = beanName.substring(0, 1).toLowerCase().concat(beanName.substring(1));
        } else { // 否则，bean名称为类名
            beanName = beanName;
        }

        return beanName;
    }
}
