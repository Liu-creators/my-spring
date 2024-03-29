package org.example.spring.utils;

import org.example.spring.BeanDefinition;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.ComponentScan;

import java.io.File;
import java.net.URL;
import java.util.Map;

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
    public static <T> void scan(Class<T> clazz, Class<T> configClass, Map<String, BeanDefinition> beanDefinitionMap) throws ClassNotFoundException {
        // 如果类上使用了@Component注解
        if (clazz.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            String value = componentScan.value();

            if (!"".equals(value)) {
                String path = value;
                path = path.replace(".", "/");

                URL resource = clazz.getClassLoader().getResource(path);
                File file = new File(resource.getFile());

                loopFor(file, configClass, beanDefinitionMap);
            }
        }
    }

    /**
     * 递归遍历指定文件/文件夹
     * @param file 文件/文件夹
     * @throws ClassNotFoundException 类找不到
     */
    private static <T> void loopFor(File file, Class<T> configClass, Map<String, BeanDefinition> beanDefinitionMap) throws ClassNotFoundException {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                if (listFile.isDirectory()) {
                    loopFor(listFile,configClass, beanDefinitionMap);

                    continue;
                }
                toBeanDefinitionMap(listFile, configClass, beanDefinitionMap);
            }
        } else if (file.isFile()) {
            toBeanDefinitionMap(file, configClass, beanDefinitionMap);
        }
    }
    /**
     * 解析bean，并保存到Map<String, BeanDefinition>
     * @param file 解析的class文件
     * @throws ClassNotFoundException 类找不到
     */
    private static <T> void toBeanDefinitionMap(File file, Class<T> configClass, Map<String, BeanDefinition> beanDefinitionMap) throws ClassNotFoundException {
        // 获取类的绝对路径
        String absolutePath = file.getAbsolutePath();
        // 处理得到类的全限定名
        absolutePath = absolutePath.substring(absolutePath.indexOf("org"), absolutePath.indexOf(".class"));
        absolutePath = absolutePath.replace("\\", ".");

        // 通过类加载器加载
        Class<?> loadClass = configClass.getClassLoader().loadClass(absolutePath);

        String beanName;

        if (loadClass.isAnnotationPresent(Component.class)) {
            // 获取@Component注解上配置的组件名
            Component component = loadClass.getAnnotation(Component.class);
            beanName = component.value();

            // 是否懒加载
            boolean lazy = false;
            // bean的作用域
            String scope = "singleton";

            // 保存bean的定义
            BeanDefinition beanDefinition = new BeanDefinition();

            beanDefinition.setType(loadClass);
            beanDefinition.setLazy(lazy);
            beanDefinition.setScope(scope);

            beanDefinitionMap.put(beanName, beanDefinition);
        }
    }
}
