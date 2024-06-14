package com.liuyanzhao.sens.config.shiro;

import com.liuyanzhao.sens.config.properties.IgnoredUrlsProperties;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 言曌
 * @date 2018/8/20 上午6:19
 */

@Configuration
public class ShiroConfig {

    @Bean
    IgnoredUrlsProperties getIgnoredUrlsProperties() {
        return new IgnoredUrlsProperties();
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //自定义拦截器
        Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
        //访问权限配置
        filtersMap.put("requestURL", getURLPathMatchingFilter());
        shiroFilterFactoryBean.setFilters(filtersMap);

        //拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 配置不会被拦截的链接 顺序判断
        List<String> urls = getIgnoredUrlsProperties().getUrls();
        for (String url : urls) {
            filterChainDefinitionMap.put(url, "anon");
        }
        filterChainDefinitionMap.put("/admin", "authc");
//        filterChainDefinitionMap.put("/admin/**", "authc");
        filterChainDefinitionMap.put("/admin/**", "requestURL");
        filterChainDefinitionMap.put("/**", "anon");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);


        // 如果不设置默认会自动寻找Web工程根目录下的"/login"页面
        shiroFilterFactoryBean.setLoginUrl("/admin/login");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/");
        //未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        return shiroFilterFactoryBean;

    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setAuthenticator(modularRealmAuthenticator());
        List<Realm> realms = new ArrayList<>();
        //密码登录realm
        realms.add(normalRealm());
        //免密登录realm
        realms.add(freeRealm());
        securityManager.setRealms(realms);
        return securityManager;
    }

    /**
     * 系统自带的Realm管理，主要针对多realm
     */
    @Bean
    public ModularRealmAuthenticator modularRealmAuthenticator() {
        //自己重写的ModularRealmAuthenticator
        UserModularRealmAuthenticator modularRealmAuthenticator = new UserModularRealmAuthenticator();
        modularRealmAuthenticator.setAuthenticationStrategy(new AtLeastOneSuccessfulStrategy());
        return modularRealmAuthenticator;
    }

    /**
     * 需要密码登录的realm
     *
     * @return MyShiroRealm
     */
    @Bean
    public NormalRealm normalRealm() {
        NormalRealm normalRealm = new NormalRealm();
        normalRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return normalRealm;
    }


    /**
     * 免密登录realm
     *
     * @return MyShiroRealm
     */
    @Bean
    public FreeRealm freeRealm() {
        FreeRealm realm = new FreeRealm();
        //不验证账号密码
        realm.setCredentialsMatcher(allowAllCredentialsMatcher());
        return realm;
    }

    /**
     * 访问 权限 拦截器
     *
     * @return
     */
    public URLPathMatchingFilter getURLPathMatchingFilter() {
        return new URLPathMatchingFilter();
    }

    /**
     * MD5加盐加密十次
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        //散列的次数，md5("")
        hashedCredentialsMatcher.setHashIterations(10);
        return hashedCredentialsMatcher;
    }

    @Bean
    public AllowAllCredentialsMatcher allowAllCredentialsMatcher() {
        return new AllowAllCredentialsMatcher();
    }

