@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')
import groovyx.net.http.*

@Grab('com.netflix.rxjava:rxjava-groovy:0.14.0')
import rx.Observable

@Grab('com.sendgrid:sendgrid-java:2.0.0')
import com.sendgrid.*

@Grab('org.commonjava.googlecode.markdown4j:markdown4j:2.2-cj-1.0')
import org.markdown4j.Markdown4jProcessor

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

def arg(name, defaultValue = "") {
	def value = System.getProperty(name, defaultValue)
	if (!value) {
		throw new IllegalStateException("Please specify -D$name arg")
	}
	value
}

dir = arg "dir"
enUser = arg "enUser"
enApp = "6IIT5ER4SCCJYJL36Y"
sgPassword = arg "sgPassword"

def markdownTemplate = new File("$dir/digest.md")
def html = new Markdown4jProcessor()
	.addHtmlAttribute("style", "color: #878787", "p")
	.addHtmlAttribute("style", "color: #0090c5", "a")
	.process(markdownTemplate)

def sendgrid = new SendGrid("latcraft", sgPassword)
def mailSender = { recipient ->
		def recipientEmail = "eduards.sizovs@gmail.com"
		def email = new SendGrid.Email(
			to: [ recipientEmail ], 
			from: "digest@latcraft.lv",
			fromName: "LatCraft Digest",
			subject: "Monthly Digest",
			html: html,
			replyTo: "no-reply@latcraft.lv"
		)
		email.addSubstitution "userName", recipient.first_name
		email.addSubstitution "pixies/", "https://raw.githubusercontent.com/latcraft/digest/master/$dir/pixies/"

		def response = sendgrid.send(email)
		println "Sending email to $recipientEmail -> $response.code / $response.message"
}		

def getEventIds() {
  observable = Observable
  .create { observer ->
    try {
    	eventbrite = new HTTPBuilder('https://www.eventbrite.com')
		eventbrite.request( GET, JSON ) {
		  uri.path = '/json/user_list_events'
		  uri.query = [ user_key: enUser, app_key: enApp, only_display: 'id' ]

		  response.success = { _, json ->
		  	observer.onNext(json)
		  	observer.onCompleted()
		  }
		} 
    } catch (Throwable t) {
      observer.onError(t)
    }
  }
  .flatMap { response -> Observable.from(response.events*.event*.id) }
}

def getAttendees(eventId) {
  observable = Observable
  .create { observer ->
    try {
    	eventbrite = new HTTPBuilder('https://www.eventbrite.com')
		eventbrite.request( GET, JSON ) {
		  uri.path = '/json/event_list_attendees'
		  uri.query = [ id: eventId, user_key:enUser, app_key: enApp, only_display: 'first_name,last_name,email' ]

		  response.success = { _, json ->
		  	observer.onNext(json)
		  	observer.onCompleted()
		  }
		} 
    } catch (Throwable t) {
      observer.onError(t)
    }
  }
  .flatMap { response -> Observable.from(response.attendees*.attendee)  }
}



getEventIds()
	.flatMap { eventId -> getAttendees(eventId) } 
	.distinct { it.email }
	.subscribe mailSender








 



