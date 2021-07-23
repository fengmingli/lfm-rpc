package com.lifengming.rpc.client;

import com.lifengming.rpc.client.annotation.EnableLfmRpcClient;
import com.lifengming.rpc.common.annotation.RpcService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 执行时机：在所有bean定义信息将要被加载，bean实例还未创建的；优先于BeanFactoryPostProcessor执行；
 * 作用：利用BeanDefinitionRegistryPostProcessor给容器中再额外添加一些组件，在标准初始化之后修改applicationContext的内部bean registry
 * <p>
 *
 * @author lifengming
 * @since 2019.10.16
 */
@Slf4j
@RequiredArgsConstructor
public class ServiceBeanDefinitionHandler implements BeanDefinitionRegistryPostProcessor {

    public static final String SYSTEM_PROPERTY = "sun.java.command";

    @NonNull
    private final LfmRpcClientProxy lfmRpcClientProxy;

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        log.info("开始注册Bean");
        //拿到类目录bean扫描器
        ClassPathScanningCandidateComponentProvider scanner = this.getScanner();
        //增加一个注解过滤器
        scanner.addIncludeFilter(new AnnotationTypeFilter(RpcService.class));
        //告诉bean加载器扫描注解的位置
        for (String apiPackages : getApiPackages()) {
            //拿到路径下申请组件者的Bean set集合
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(apiPackages);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    BeanDefinitionHolder holder = createBeanDefinition(annotationMetadata);
                    BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
                }
            }
        }
    }


    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    private BeanDefinitionHolder createBeanDefinition(AnnotationMetadata annotationMetadata) {
        String className = annotationMetadata.getClassName();
        log.info("为类创建bean定义: {}", className);

        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(ProxyFactoryBean.class);
        //首字母转小写：HelloService-》helloService
        String beanName = StringUtils.uncapitalize(className.substring(className.lastIndexOf('.') + 1));
        definition.addPropertyValue("type", className);
        definition.addPropertyValue("lfmRpcClientProxy", lfmRpcClientProxy);

        return new BeanDefinitionHolder(definition.getBeanDefinition(), beanName);
    }

    /**
     * 用包路径放入一个无序不重复的set
     **/
    private Set<String> getApiPackages() {
        //@EnableRPCClients注解的value路径数组
        String[] basePackages;
        Set<String> set = new HashSet<>();
        if (getMainClass().getAnnotation(EnableLfmRpcClient.class) != null) {
            //获取 ClientApplication上面的EnableLfmRpcClient注解的
            basePackages = getMainClass().getAnnotation(EnableLfmRpcClient.class).basePackages();
            Collections.addAll(set, basePackages);
        }
        return set;


    }

    /**
     * 首先获取当前执行类
     * 然后通过反射拿到系统变量中的mainClass
     *
     * @return class
     */
    private Class<?> getMainClass() {
        if (null != System.getProperty(SYSTEM_PROPERTY)) {
            //正在执行的类：sun.java.command
            String mainClass = System.getProperty(SYSTEM_PROPERTY);
            log.debug("Main class: {}", mainClass);
            try {
                return Class.forName(mainClass);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Cannot determine main class.");
            }
        }
        throw new IllegalStateException("Cannot determine main class.");
    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(@NonNull AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (beanDefinition.getMetadata().isInterface()
                            && beanDefinition.getMetadata().getInterfaceNames().length == 1
                            && Annotation.class.getName().equals(beanDefinition.getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = Class.forName(beanDefinition.getMetadata().getClassName());
                            return !target.isAnnotation();
                        } catch (Exception ex) {
                            log.error("Could not load target class: {}, {}",
                                    beanDefinition.getMetadata().getClassName(), ex);
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }
}
