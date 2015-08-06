import com.sendgrid.*
import groovy.json.JsonSlurper

import static java.util.Objects.requireNonNull

def arg(String name, String defaultValue = null) {
	def value = System.getProperty(name, defaultValue)
	requireNonNull(value, "Please specify -D$name arg")
}


eventbrite_json = arg "sendgrid.json"

def slurper = new JsonSlurper().parseText(eventbrite_json);
sendgrid_user = slurper.login
sendgrid_password = slurper.password

email_from = arg "email.from"
email_subject = arg "email.subject"
email_body = arg "email.body"
email_to = arg "email.to"

calendar_events = [arg("calendar", "")].findAll({ !it.isEmpty() }).collect({ new File(it) })

def sendgrid = new SendGrid(sendgrid_user, sendgrid_password)

email_to.eachLine() { emailTo ->
	def email = new SendGrid.Email(
		to: [ emailTo ],
		from: email_from,
		fromName: "Latvian Software Craftsmanship Community",
		subject: email_subject,
		html: email_body,
		replyTo: "no-reply@latcraft.lv",
		attachments: calendar_events.collectEntries { [it.name, new FileInputStream(it)] }
	)

	def response = sendgrid.send(email)
	println "Sending email to $emailTo -> $response.code / $response.message"
}











