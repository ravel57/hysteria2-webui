package ru.ravel.hysteria2webui.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping

@Controller
@CrossOrigin
class WebController {

	@GetMapping("")
	fun rootMapping(): String {
		return "index"
	}

}