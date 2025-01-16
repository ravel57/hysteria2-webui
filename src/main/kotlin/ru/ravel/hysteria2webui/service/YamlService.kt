package ru.ravel.hysteria2webui.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.yaml.snakeyaml.DumperOptions.FlowStyle
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.nodes.Tag
import ru.ravel.hysteria2webui.model.HysteriaConfig
import ru.ravel.hysteria2webui.model.User
import ru.ravel.hysteria2webui.model.Username
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import javax.imageio.ImageIO


@Service
class YamlService @Autowired constructor(
	private val jsonService: JsonService,

	@Value("\${hysteria-config-path}")
	private var configPath: String,
) {

	fun getAllUsers(): Set<User> {
		return jsonService.getAllUsers().toSet()
	}


	fun addNewUserToConfig(username: Username, pswrd: String = ""): User {
		val password: String = pswrd.ifEmpty {
			generatePassword(20)
		}
		val config = readConfig()
		config.auth?.userpass?.put(username.username, password)
		saveConfig(config)
		jsonService.newUser(User(username.username, password, true))
		return User(username.username, password, true)
	}


	fun deleteUserFromConfig(username: String, removeFromJson: Boolean = false): User {
		val config = readConfig()
		config.auth?.userpass?.remove(username)
		saveConfig(config)
		if (removeFromJson) {
			jsonService.deleteUser(User(username, "", false))
		}
		return User(username, "", false)
	}


	fun getUrlForUser(username: String): String {
		val config = readConfig()
		val password = jsonService.getAllUsers().find { it.name == username }?.password
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
		Runtime.getRuntime().exec("systemctl restart hysteria-server.service")
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
					val specialChars = "!\$'()*+,;="
					chars[i] = specialChars.random().toChar()
				}
			}
			i++
		}
		return String(chars)
	}


	fun generateQRCode(username: String, response: HttpServletResponse) {
		val qrCodeWriter = QRCodeWriter()
		val bitMatrix = qrCodeWriter.encode(getUrlForUser(username), BarcodeFormat.QR_CODE, 300, 300)
		val qrImage: BufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)
		val outputStream = ByteArrayOutputStream()
		ImageIO.write(qrImage, "png", outputStream)
		outputStream.flush()
		val inputStream = ByteArrayInputStream(outputStream.toByteArray())
		outputStream.close()
		response.outputStream.use { os ->
			inputStream.copyTo(os)
		}
	}


	fun patchUserFromConfig(user: User): User? {
		if (user.enabled) {
			addNewUserToConfig(username = Username(user.name), pswrd = user.password)
		} else {
			deleteUserFromConfig(user.name, false)
		}
		jsonService.patchUser(user)
		return user
	}

}