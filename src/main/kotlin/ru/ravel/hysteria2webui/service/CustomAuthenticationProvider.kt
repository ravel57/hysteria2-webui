package ru.ravel.hysteria2webui.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component


@Component
class CustomAuthenticationProvider(
	@Value("\${login-password}")
	var password: String?,
) : AuthenticationProvider {

	@Throws(AuthenticationException::class)
	override fun authenticate(authentication: Authentication): Authentication? {
		val password = authentication.credentials.toString()
		return if (password == this.password) {
			val authorities: List<GrantedAuthority> = emptyList()
			val userDetails = User("admin", password, authorities)
			UsernamePasswordAuthenticationToken(userDetails, password, authorities)
		} else {
			null
		}
	}

	override fun supports(authentication: Class<*>): Boolean {
		return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
	}
}

