package org.example.spring.utils;

import org.example.spring.BeanDefinition;
import org.example.spring.BeanPostProcessor;
import org.example.spring.annotation.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * -03/29-10:51
 * -扫描组件（向容器中注册Bean）
 */
public class ScanBeanUtils {

    /**
     * 扫描组件
     * @param clazz 配置类的类对象
     * @throws ClassNotFoundException 类找不到
     */
    public static <T> void scan(Class<T> clazz, Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects, List<BeanPostProcessor> list) throws ClassNotFoundException {
        // 如果类上使用了@ComponentScan注解
        if (clazz.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            String value = componentScan.value();

            if (!"".equals(value)) {
                String path = value;
                path = path.replace(".", "/");
                // 通过URL和File获取文件资源
                URL resource = clazz.getClassLoader().getResource(path);
                assert resource != null;
                File file = new File(resource.getFile());

                loopFor(file, clazz, beanDefinitionMap, singletonObjects,list);
            }
        }
    }

    /**
     * 递归遍历指定文件/文件夹
     * @param file 文件/文件夹
     * @throws ClassNotFoundException 类找不到
     */
    private static <T> void loopFor(File file, Class<T> configClass, Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects, List<BeanPostProcessor> list) throws ClassNotFoundException {
        if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                if (listFile.isDirectory()) {
                    loopFor(listFile,configClass, beanDefinitionMap, singletonObjects, list);
                    continue;
                }
                toBeanDefinitionMap(listFile, configClass, beanDefinitionMap, singletonObjects, list);
            }
        } else if (file.isFile()) {
            toBeanDefinitionMap(file, configClass, beanDefinitionMap, singletonObjects, list);
        }
    }
    /**
     * 解析bean，并保存到Map<String, BeanDefinition>
     * @param file 解析的class文件
     * @throws ClassNotFoundException 类找不到
     */
    private static <T> void toBeanDefinitionMap(File file, Class<T> configClass, Map<String, BeanDefinition> beanDefinitionMap, Map<String, Object> singletonObjects, List<BeanPostProcessor> list) throws ClassNotFoundException {
        // 获取类的绝对路径
        String absolutePath = file.getAbsolutePath();
        // 处理得到类的全限定名
        absolutePath = absolutePath.substring(absolutePath.indexOf("org"), absolutePath.indexOf(".class"));
        absolutePath = absolutePath.replace("\\", ".");

        // 通过类加载器加载
        Class<?> loadClass = configClass.getClassLoader().loadClass(absolutePath);

        String beanName = "";
        // 是否是懒加载
        boolean lazy = false;
        // bean的作用域
        String scope = "singleton";

        if (loadClass.isAnnotationPresent(Component.class)) {
            // 获取@Component注解上配置的组件名
            Component component = loadClass.getAnnotation(Component.class);
            beanName = component.value();
            if ("".equals(beanName)) {
                beanName = GetBeanUtils.getBeanName(loadClass);
            }

            // 类上使用了@Scope注解
            if (loadClass.isAnnotationPresent(Scope.class)) {
                // 获取@Scope注解
                Scope annotation = loadClass.getAnnotation(Scope.class);

                // 单例
                if (CreateBeanUtils.isSingleton(annotation.value())) {
                    lazy = loadClass.isAnnotationPresent(Lazy.class);
                } else {
                    // 非单例
                    scope = annotation.value();
                }
            } else {
                // 类上没有使用@Scope注解，默认是单例的
                lazy = loadClass.isAnnotationPresent(Lazy.class);
            }
            // 保存bean的定义
            BeanDefinition beanDefinition = new BeanDefinition();
            // bean类型
            beanDefinition.setType(loadClass);
            beanDefinition.setLazy(lazy);
            beanDefinition.setScope(scope);
            // BeanPostProcessor接口的实现类保存到list中
            if (BeanPostProcessor.class.isAssignableFrom(loadClass)) {
                list.add((BeanPostProcessor) CreateBeanUtils.createBean(beanDefinition, beanDefinitionMap,singletonObjects, list));
            }
            beanDefinitionMap.put(beanName, beanDefinition);
        } else if (loadClass.isAnnotationPresent(Configuration.class)) {
            Method[] methods = loadClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Bean.class)) {
                    Bean annotation = method.getAnnotation(Bean.class);
                    beanName = annotation.value();
                    // 是否懒加载
                    lazy = method.isAnnotationPresent(Lazy.class);
                    if ("".equals(beanName)) {
                        beanName = method.getName();
                    }
                    // 保存bean的定义
                    BeanDefinition beanDefinition = new BeanDefinition();
                    // bean类型
                    beanDefinition.setType(method.getReturnType());
                    beanDefinition.setLazy(lazy);
                    beanDefinition.setScope(scope);
                    beanDefinitionMap.put(beanName, beanDefinition);
                }
            }
        }
    }

}
