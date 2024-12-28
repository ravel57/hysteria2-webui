package ru.ravel.hysteria2webui.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.ravel.hysteria2webui.model.User
import java.io.File


@Service
class JsonService(
	private val objectMapper: ObjectMapper = ObjectMapper(),

	@Value("\${json-config-path}")
	private var jsonConfigPath: String,
) {

	init {
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
	}

	fun getAllUsers(): MutableList<User> {
		val json = File(jsonConfigPath)
		if (!json.exists()) {
			json.createNewFile()
			json.writeText("[]")
		}
		if (json.readText().isBlank()) {
			json.writeText("[]")
		}
		val valueType = objectMapper.typeFactory.constructCollectionType(
			List::class.java,
			User::class.java
		)
		return try {
			objectMapper.readValue(json, valueType)
		} catch (e: Exception) {
			println("Error parsing json: ${e.message}")
			mutableListOf<User>()
		}
	}


	fun newUser(user: User): User {
		val allUsers = getAllUsers()
		allUsers.add(user)
		File(jsonConfigPath).writeText(objectMapper.writeValueAsString(allUsers))
		return user
	}

	fun deleteUser(user: User): User {
		val allUsers = getAllUsers()
		allUsers.remove(user)
		File(jsonConfigPath).writeText(objectMapper.writeValueAsString(allUsers))
		return user
	}

	fun patchUser(user: User): User {
		val allUsers = getAllUsers()
		allUsers.find { u -> u == user }.apply {
			this?.enabled = user.enabled
		}
		File(jsonConfigPath).writeText(objectMapper.writeValueAsString(allUsers))
		return user
	}

}