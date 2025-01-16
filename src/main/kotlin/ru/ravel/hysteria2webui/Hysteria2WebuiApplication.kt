package ru.ravel.hysteria2webui

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Hysteria2WebuiApplication

fun main(args: Array<String>) {
	runApplication<Hysteria2WebuiApplication>(*args)
	Runtime.getRuntime().exec(arrayOf("/usr/bin/nohup", "/usr/local/bin/hysteria", "server", "&"))
}