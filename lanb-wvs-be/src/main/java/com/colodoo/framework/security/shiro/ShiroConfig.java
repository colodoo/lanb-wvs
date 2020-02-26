package com.colodoo.framework.security.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro 配置
 * 
 * @author colodoo
 *
 */
@Configuration
public class ShiroConfig {
	/**
	 * 过滤器默认权限表 {anon=anon, authc=authc, authcBasic=authcBasic, logout=logout, noSessionCreation=noSessionCreation, perms=perms, port=port, rest=rest, roles=roles, ssl=ssl, user=user}
	 * <p>
	 * anon, authc, authcBasic, user 是第一组认证过滤器 perms, port, rest, roles, ssl 是第二组授权过滤器
	 * <p>
	 * user 和 authc 的不同：当应用开启了rememberMe时, 用户下次访问时可以是一个user, 但绝不会是authc, 因为authc是需要重新认证的, user表示用户不一定已通过认证, 只要曾被Shiro记住过登录状态的用户就可以正常发起请求,比如rememberMe 以前的一个用户登录时开启了rememberMe, 然后他关闭浏览器, 下次再访问时他就是一个user, 而不会authc
	 *
	 * @param securityManager
	 *            初始化 ShiroFilterFactoryBean 的时候需要注入 SecurityManager
	 */
	@Bean("shiroFilter")
	public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// setLoginUrl 如果不设置值，默认会自动寻找Web工程根目录下的"/login.jsp"页面 或 "/login" 映射
		// shiroFilterFactoryBean.setLoginUrl("/api/notRole");
		shiroFilterFactoryBean.setLoginUrl("/#/user/login");
		// 设置无权限时跳转的 url;
		shiroFilterFactoryBean.setUnauthorizedUrl("/api/notRole");

		// 设置拦截器
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		// 开放验证接口
		filterChainDefinitionMap.put("/user/logout", "anon");
		filterChainDefinitionMap.put("/user/loginCheck", "anon");
		filterChainDefinitionMap.put("/user/register", "anon");
		filterChainDefinitionMap.put("/#/user/login", "anon");
		// 用户，需要角色权限 “user”
		filterChainDefinitionMap.put("/menu/**", "roles[user]");

		// 管理员，需要角色权限“admin”
		filterChainDefinitionMap.put("/user/**", "roles[admin]");
		filterChainDefinitionMap.put("/role/**", "roles[admin]");
		filterChainDefinitionMap.put("/creater/**", "roles[admin]");
		filterChainDefinitionMap.put("/config/**", "roles[admin]");

		// 动态载入权限
		// ...
		// 主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截
		filterChainDefinitionMap.put("**", "authc");
		
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		
		return shiroFilterFactoryBean;
	}
	
	@Bean
	public FilterRegistrationBean delegatingFilterProxy(){
	    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
	    DelegatingFilterProxy proxy = new DelegatingFilterProxy();
	    proxy.setTargetFilterLifecycle(true);
	    proxy.setTargetBeanName("shiroFilter");
	    filterRegistrationBean.setFilter(proxy);
	    return filterRegistrationBean;
	}

	/**
	 * 注入 securityManager
	 */
	@Bean
	public SecurityManager securityManager(CustomRealm customRealm) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 设置realm.
		securityManager.setRealm(customRealm);
		return securityManager;
	}
}