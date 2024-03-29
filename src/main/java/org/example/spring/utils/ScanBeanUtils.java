package org.example.spring.utils;

import org.example.spring.BeanDefinition;
import org.example.spring.annotation.Component;
import org.example.spring.annotation.ComponentScan;
import org.example.spring.annotation.Lazy;
import org.example.spring.annotation.Scope;

import java.io.File;
import java.net.URL;
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
    public static <T> void scan(Class<T> clazz, Class<T> configClass, Map<String, BeanDefinition> beanDefinitionMap) throws ClassNotFoundException {
        // 如果类上使用了@Component注解
        if (clazz.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = clazz.getAnnotation(ComponentScan.class);
            String value = componentScan.value();

            if (!"".equals(value)) {
                String path = value;
                path = path.replace(".", "/");

                URL resource = clazz.getClassLoader().getResource(path);
                assert resource != null;
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
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
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
            if ("".equals(beanName)) {
                beanName = getBeanName(loadClass);
            }

            // 是否是懒加载
            boolean lazy = false;
            // bean的作用域
            String scope = "singleton";
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
            beanDefinition.setType(loadClass);
            beanDefinition.setLazy(lazy);
            beanDefinition.setScope(scope);
            // 加入map中
            beanDefinitionMap.put(beanName, beanDefinition);
        }
    }

    /**
     * 根据类对象获取beanName
     * @param clazz 类对象
     * @return beanName
     */
    private static String getBeanName(Class<?> clazz) {
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
