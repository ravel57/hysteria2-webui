package ru.ravel.hysteria2webui.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.DumperOptions.FlowStyle
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Tag
import ru.ravel.hysteria2webui.model.HysteriaConfig
import ru.ravel.hysteria2webui.model.User
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import javax.imageio.ImageIO


@Service
class YamlService @Autowired constructor(
	@Value("\${hysteria-config-path}")
	var configPath: String,
) {

	fun getAllUsers(): List<User> {
		return readConfig().auth?.userpass
			?.map { User(it.key, it.value) }
			?: listOf()
	}


	fun addNewUserToConfig(username: String): User {
		val password: String = generatePassword(20)
		val config = readConfig()
		config.auth?.userpass?.put(username, password)
		saveConfig(config)
		return User(username, password)
	}


	fun deleteUserFromConfig(username: String): User {
		val config = readConfig()
		config.auth?.userpass?.remove(username)
		saveConfig(config)
		return User(username, "")
	}


	fun getUrlForUser(username: String): String {
		val config = readConfig()
		val password = config.auth?.userpass?.get(username) ?: ""
		val domain = config.acme?.domains?.first() ?: ""
		return "hysteria2://${username}:${password}@${domain}:443/?insecure=1"
	}


	private fun readConfig(): HysteriaConfig {
		val yaml = Yaml()
		val input: InputStream = FileInputStream(configPath)
		return yaml.loadAs(input, HysteriaConfig::class.java)
	}


	private fun saveConfig(config: HysteriaConfig) {
		val yaml = Yaml()
		File(configPath).writeText(
			yaml.dumpAs(config, Tag("!!"), FlowStyle.BLOCK)
				.replaceFirst("!!", "---")
		)
	}


	private fun generatePassword(
		length: Int,
		useNumbers: Boolean = true,
		useSpecialChars: Boolean = true,
	): String {
		val chars = CharArray(length)
		val random = Random()
		var i = 0
		while (i < length) {
			when (random.nextInt(3)) {
				0 -> if (random.nextBoolean()) {
					chars[i] = ('a' + random.nextInt(26)).toChar()
				} else {
					chars[i] = ('A' + random.nextInt(26)).toChar()
				}

				1 -> if (useNumbers) {
					chars[i] = ('0' + random.nextInt(10)).toChar()
				}

				2 -> if (useSpecialChars) {
					val specialChars = "!@#$%^&*()_+-={}:<>?/.,;[]"
					chars[i] = specialChars.random().toChar()
				}
			}
			i++
		}
		return String(chars)
	}


	fun generateQRCode(username: String): ByteArray {
		val qrCodeWriter = QRCodeWriter()
		val bitMatrix = qrCodeWriter.encode(getUrlForUser(username), BarcodeFormat.QR_CODE, 300, 300)
		val qrImage: BufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)
		val outputStream = ByteArrayOutputStream()
		ImageIO.write(qrImage, "png", outputStream)
		return outputStream.toByteArray()
	}
}