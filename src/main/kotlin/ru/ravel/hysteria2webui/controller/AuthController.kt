package ru.ravel.hysteria2webui.controller

import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/auth")
class AuthController {

	@GetMapping("/status")
	fun getStatus(response: HttpServletResponse): Map<String, Any> {
		val auth = SecurityContextHolder.getContext().authentication
		return if (auth != null && auth.isAuthenticated && auth.principal != "anonymousUser") {
			response.status = HttpServletResponse.SC_OK
			mapOf("authenticated" to auth.isAuthenticated, "user" to auth.name)
		} else {
			response.status = HttpServletResponse.SC_UNAUTHORIZED
			mapOf("authenticated" to false)
		}
	}
}