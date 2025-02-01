package ru.ravel.hysteria2webui.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.filter.RequestContextFilter


@Configuration
@EnableWebSecurity
class SecurityConfig {

	@Bean
	fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
		return authConfig.authenticationManager
	}

	@Bean
	fun passwordEncoder(): PasswordEncoder {
		return NoOpPasswordEncoder.getInstance()
	}

	@Bean
	fun requestContextFilter(): RequestContextFilter {
		return RequestContextFilter()
	}


	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		return http
			.csrf { it.disable() }
			.cors { it.disable() }
			.authorizeHttpRequests { authorize ->
				authorize
					.requestMatchers("/js/**", "/css/**", "/logo.png", "/favicon.png").permitAll()
					.requestMatchers("/login", "/auth/**").permitAll()
					.anyRequest().authenticated()
			}
			.sessionManagement { session ->
				session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
			}
			.formLogin { form ->
				form
					.loginPage("/login")
					.loginProcessingUrl("/auth/login")
					.defaultSuccessUrl("/", true)
					.failureUrl("/login?error=true")
					.permitAll()
			}
			.logout { logout ->
				logout
					.logoutUrl("/auth/logout")
					.logoutSuccessUrl("/login")
					.invalidateHttpSession(true)
					.deleteCookies("JSESSIONID")
					.permitAll()
			}
			.build()
	}
}