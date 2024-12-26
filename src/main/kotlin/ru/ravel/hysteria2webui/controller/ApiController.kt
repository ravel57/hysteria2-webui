package ru.ravel.hysteria2webui.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.ravel.hysteria2webui.service.YamlService


@RestController
@RequestMapping("/api/v1")
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
	): ByteArray {
		return yamlService.generateQRCode(username)
	}

	@PostMapping("/user")
	fun newUser(
		@RequestParam username: String,
	): ResponseEntity<Any> {
		return ResponseEntity.ok().body(yamlService.addNewUserToConfig(username))
	}

	@DeleteMapping("/user")
	fun deleteUser(
		@RequestParam username: String,
	): ResponseEntity<Any> {
		return ResponseEntity.ok().body(yamlService.deleteUserFromConfig(username))
	}

}