package ru.ravel.hysteria2webui.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MvcConfig : WebMvcConfigurer {

	override fun addViewControllers(registry: ViewControllerRegistry) {
		registry.addViewController("/login").setViewName("index")
		registry.addViewController("/").setViewName("index")
	}

	override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
		registry.addResourceHandler("/static/**")
			.addResourceLocations("classpath:/static/")
	}

	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
	}
}

