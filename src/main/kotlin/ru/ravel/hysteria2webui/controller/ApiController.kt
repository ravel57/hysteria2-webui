package ru.ravel.hysteria2webui.controller

import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.ravel.hysteria2webui.model.User
import ru.ravel.hysteria2webui.model.Username
import ru.ravel.hysteria2webui.service.YamlService


@RestController
@RequestMapping("/api/v1")
@CrossOrigin
class ApiController @Autowired constructor(
	val yamlService: YamlService,
) {

	@GetMapping("/users")
	fun getUsers(): ResponseEntity<Any> {
		return ResponseEntity.ok().body(yamlService.getAllUsers())
	}

	@GetMapping("/user/url")
	fun getUrlForUser(
		@RequestParam username: String,
	): ResponseEntity<Any> {
		return ResponseEntity.ok().body(yamlService.getUrlForUser(username))
	}

	@GetMapping("/user/qr", produces = [MediaType.IMAGE_PNG_VALUE])
	fun getQrForUser(
		@RequestParam username: String,
		response: HttpServletResponse,
	): ResponseEntity<Any> {
		yamlService.generateQRCode(username, response)
		return ResponseEntity.ok().build()
	}

	@PostMapping("/user")
	fun newUser(
		@RequestBody username: Username,
	): ResponseEntity<Any> {
		return ResponseEntity.ok().body(yamlService.addNewUserToConfig(username))
	}

	@DeleteMapping("/user")
	fun deleteUser(
		@RequestParam username: String,
	): ResponseEntity<Any> {
		return ResponseEntity.ok().body(yamlService.deleteUserFromConfig(username, true))
	}

	@PatchMapping("/user")
	fun patchUser(
		@RequestBody user: User,
	): ResponseEntity<Any> {
		return ResponseEntity.ok().body(yamlService.patchUserFromConfig(user))
	}

}