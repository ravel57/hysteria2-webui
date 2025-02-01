package ru.ravel.hysteria2webui.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.Base64


@Component
class CustomAuthenticationProvider(
	@Value("\${login-password}")
	var password: String?,
) : AuthenticationProvider {

	@Throws(AuthenticationException::class)
override fun authenticate(authentication: Authentication): Authentication? {
	val base64password = authentication.credentials.toString()
	val decodedBytes = Base64.getDecoder().decode(base64password)
	val password = String(decodedBytes, Charsets.UTF_8)
	return if (password == this.password) {
		val authorities: List<GrantedAuthority> = emptyList()
		val userDetails = User("admin", password, authorities)
		val authToken = UsernamePasswordAuthenticationToken(userDetails, password, authorities)
		SecurityContextHolder.getContext().authentication = authToken
		authToken
	} else {
		null
	}
}

	override fun supports(authentication: Class<*>): Boolean {
		return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
	}
}

