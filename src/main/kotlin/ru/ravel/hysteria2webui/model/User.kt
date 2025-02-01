package ru.ravel.hysteria2webui.model

import java.util.UUID

data class User (
	var name: String,
	var password: String,
	var enabled: Boolean,
	var uuid: UUID,
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		return name == (other as User).name
	}

	override fun hashCode(): Int {
		return name.hashCode()
	}
}