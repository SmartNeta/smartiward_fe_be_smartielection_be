package com.mnt.sampark.core.shiro;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.config.AbstractShiroAnnotationProcessorConfiguration;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;

@Configuration

public class ShiroConfig extends AbstractShiroAnnotationProcessorConfiguration{
	/*@Bean
    @DependsOn("lifecycleBeanPostProcessor")
    protected DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return super.defaultAdvisorAutoProxyCreator();
    }*/

    @Bean
    protected AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        return super.authorizationAttributeSourceAdvisor(securityManager);
    }
	/**
	 * FilterRegistrationBean
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<DelegatingFilterProxy> filterRegistrationBean() {
		FilterRegistrationBean<DelegatingFilterProxy> filterRegistration = new FilterRegistrationBean<DelegatingFilterProxy>();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter")); 
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*"); 
        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST);
        return filterRegistration;
	}
	
	/**
	 * @see org.apache.shiro.spring.web.ShiroFilterFactoryBean
	 * @return
	 */
	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilter(){
		ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
		bean.setSecurityManager(securityManager());
		bean.setLoginUrl("/open/login");
		bean.setUnauthorizedUrl("/open/unauthorized");
		
		Map<String, Filter>filters = new HashMap<String, Filter>();
		//filters.put("perms", urlPermissionsFilter());
		filters.put("restc", new JWTAuthenticationFilter());
		filters.put("anon", new AnonymousFilter());
		bean.setFilters(filters);
		
		Map<String, String> chains = new HashMap<String, String>();
		chains.put("/open/rest/login", "anon");
		//chains.put("/open/**", "anon");
		chains.put("/open/unauthorized", "anon");
		//chains.put("/open/logout", "logout");
		chains.put("/base/**", "anon");
		chains.put("/css/**", "anon");
		chains.put("/js/**", "anon");
		chains.put("/layer/**", "anon");
		//chains.put("/**", "authc,perms");
		chains.put("/rest/**", "restc");
		chains.put("/open/sampark/**", "restc");
		chains.put("/secure/sampark/**", "restc");
		//chains.put("/**", "anon");
		bean.setFilterChainDefinitionMap(chains);
		return bean;
	}
	
	/**
	 * @see org.apache.shiro.mgt.SecurityManager
	 * @return
	 */
	@Bean(name="securityManager")
	public DefaultWebSecurityManager securityManager() {
		DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
		manager.setRealm(userRealm());
		//manager.getRealms().add(accountRealm());
		manager.getRealms().add(statelessRealm()); 
		manager.setCacheManager(cacheManager());
		manager.setSessionManager(defaultWebSessionManager());
		return manager;
	}
	
	/**
	 * @see DefaultWebSessionManager
	 * @return
	 */
	@Bean(name="sessionManager")
	public DefaultWebSessionManager defaultWebSessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setCacheManager(cacheManager());
		//sessionManager.setGlobalSessionTimeout(1800000);
		//sessionManager.setDeleteInvalidSessions(true);
		//sessionManager.setSessionValidationSchedulerEnabled(false);
		//sessionManager.setDeleteInvalidSessions(true);
		sessionManager.setSessionIdCookieEnabled(false);
		return sessionManager;
	}
	
	/**
	 * @see UserRealm--->AuthorizingRealm
	 * @return
	 */
	@Bean
	@DependsOn(value="lifecycleBeanPostProcessor")
	public UserRealm userRealm() {
		UserRealm userRealm = new UserRealm();
		//userRealm.setCachingEnabled(true);
		userRealm.setCacheManager(cacheManager());
		return userRealm;
	}
	
	@Bean
	@DependsOn(value="lifecycleBeanPostProcessor")
	public AccountRealm accountRealm() {
		AccountRealm userRealm = new AccountRealm();
		userRealm.setCachingEnabled(true);
		userRealm.setCacheManager(cacheManager());
		return userRealm;
	}
	
	@Bean
	@DependsOn(value="lifecycleBeanPostProcessor")
	public StatelessRealm statelessRealm() {
		StatelessRealm userRealm = new StatelessRealm();
		//userRealm.setCachingEnabled(true);
		userRealm.setCacheManager(cacheManager());
		return userRealm;
	}
	
	@Bean
	public URLPermissionsFilter urlPermissionsFilter() {
		return new URLPermissionsFilter();
	}
	
	/*@Bean
	public EhCacheManager cacheManager() {
		EhCacheManager cacheManager = new EhCacheManager();
		cacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
		return cacheManager;
	}*/
	
	//@Bean
	public CacheManager cacheManager() {
		return new MemoryConstrainedCacheManager();
	}
	
	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
}
