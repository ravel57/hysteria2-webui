package ru.ravel.hysteria2webui.model

data class Auth(
	var type: String = "",
	var userpass: MutableMap<String, String> = mutableMapOf(),
)