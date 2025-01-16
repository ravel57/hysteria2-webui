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
import ru.ravel.hysteria2webui.model.*
import java.awt.image.BufferedImage
import java.io.*
import java.util.*
import javax.imageio.ImageIO


@Service
class YamlService @Autowired constructor(
	private val jsonService: JsonService,

	@Value("\${hysteria-config-path}")
	private var configPath: String,

	@Value("\${host-url}")
	private var hostUrl: String,
) {

	fun getAllUsers(): Set<User> {
		return jsonService.getAllUsers().toSet()
	}


	fun addNewUserToConfig(username: Username, pswrd: String = ""): User {
		val password: String = pswrd.ifEmpty { generatePassword(20) }
		val config = readConfig()
		val uuid = UUID.randomUUID()
		config.auth?.userpass?.put(uuid.toString(), password)
		saveConfig(config)
		jsonService.newUser(User(username.username, password, true, uuid))
		return User(username.username, password, true, uuid)
	}


	fun deleteUserFromConfig(username: String, removeFromJson: Boolean = false): User {
		val config = readConfig()
		val user = jsonService.getAllUsers().find { it.name == username }
		val uuid = user?.uuid ?: UUID.randomUUID()
		config.auth?.userpass?.remove(uuid.toString())
		saveConfig(config)
		if (removeFromJson) {
			jsonService.deleteUser(User(username, "", false, uuid))
		}
		return User(username, "", false, uuid)
	}


	fun getUrlForUser(username: String): String {
		val user = jsonService.getAllUsers().find { it.name == username }
		val password = user?.password
		val uuid = user?.uuid
		return "hysteria2://${uuid}:${password}@${hostUrl}:443/?insecure=1"
	}


	private fun readConfig(): HysteriaConfig {
		val yaml = Yaml()
		val config = try {
			val input: InputStream = FileInputStream(configPath)
			yaml.loadAs(input, HysteriaConfig::class.java)
		} catch (e: Exception) {
			val hysteriaConfig = HysteriaConfig(
				auth = Auth(
					type = "userpass",
					userpass = mutableMapOf(),
				),
				masquerade = Masquerade(
					type = "proxy",
					proxy = Proxy(
						url = "https://news.ycombinator.com/",
						rewriteHost = true,
					),
				),
				acme = Acme(
					domains = listOf(hostUrl),
					email = "your@email.com",
				),
			)
			saveConfig(hysteriaConfig)
			hysteriaConfig
		}
		return config
	}


	private fun saveConfig(config: HysteriaConfig) {
		val yaml = Yaml()
		File(configPath).writeText(
			yaml.dumpAs(config, Tag("!!"), FlowStyle.BLOCK)
				.replaceFirst("!!", "---")
		)
		val runtime = Runtime.getRuntime()
		runtime.exec(arrayOf("pkill", "-f", "/usr/local/bin/hysteria")).waitFor()
		while (runtime.exec(arrayOf("pgrep", "-f", "/usr/local/bin/hysteria")).waitFor() == 0) {
			Thread.sleep(500)
		}
		runtime.exec(arrayOf("/usr/bin/nohup", "/usr/local/bin/hysteria", "server", "&"))
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
			when (random.nextInt(6)) {
				0, 1, 2 -> if (random.nextBoolean()) {
					chars[i] = ('a' + random.nextInt(26)).toChar()
				} else {
					chars[i] = ('A' + random.nextInt(26)).toChar()
				}

				3, 4 -> if (useNumbers) {
					chars[i] = ('0' + random.nextInt(10)).toChar()
				}

				5 -> if (useSpecialChars) {
					val specialChars = "!\$()*+,;="
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