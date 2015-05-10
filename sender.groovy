@Grab('com.sendgrid:sendgrid-java:2.0.0')
import com.sendgrid.*

import static java.util.Objects.requireNonNull

def arg(String name, String defaultValue = null) {
	def value = System.getProperty(name, defaultValue)
	requireNonNull(value, "Please specify -D$name arg")
}

sendgrid_user = arg "sendgrid.user"
sendgrid_password = arg "sendgrid.password"

email_from = arg "email.from"
email_subject = arg "email.subject"
email_body = arg "email.body"

calendar_events = [arg("calendar", "")].findAll({ !it.isEmpty() }).collect({ new File(it) })

def sendgrid = new SendGrid(sendgrid_user, sendgrid_password)

System.in.eachLine() { emailTo ->
	def email = new SendGrid.Email(
		to: [ emailTo ],
		from: email_from,
		fromName: "Latvian Software Craftsmanship Community",
		subject: email_subject,
		html: new File(email_body).text,
		replyTo: "no-reply@latcraft.lv",
		attachments: calendar_events.collectEntries { [it.name, new FileInputStream(it)] }
	)

	def response = sendgrid.send(email)
	println "Sending email to $emailTo -> $response.code / $response.message"

}











