package ru.ravel.hysteria2webui.model


data class HysteriaConfig(
	var listen: String? = null,
	var acme: Acme? = null,
	var auth: Auth? = null,
	var masquerade: Masquerade? = null,
)