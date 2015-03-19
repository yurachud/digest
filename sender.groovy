@Grab('com.sendgrid:sendgrid-java:2.0.0')
import com.sendgrid.*

@Grab('com.netflix.rxjava:rxjava-groovy:0.14.0')
import rx.Observable

import static java.util.Objects.*

def arg(name) {
	def value = System.getProperty(name)
	requireNonNull(value, "Please specify -D$name arg")
}

sendgrid_user = arg "sendgrid.user"
sendgrid_password = arg "sendgrid.password"

email_to = arg "email.to"
email_from = arg "email.from"
email_subject = arg "email.subject"

def sendgrid = new SendGrid(sendgrid_user, sendgrid_password)

def email = new SendGrid.Email(
	to: [ email_to ], 
	from: email_from,
	fromName: "Latvian Software Craftsmanship Community",
	subject: email_subject,
	html: System.in.text,
	replyTo: "no-reply@latcraft.lv"
)

def response = sendgrid.send(email)
println "Sending email to $email_to -> $response.code / $response.message"





 



