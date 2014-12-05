@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')
import groovyx.net.http.*

@Grab('com.netflix.rxjava:rxjava-groovy:0.20.7')
import rx.Observable

@Grab('com.sendgrid:sendgrid-java:2.0.0')
import com.sendgrid.*

@Grab('org.commonjava.googlecode.markdown4j:markdown4j:2.2-cj-1.0')
import org.markdown4j.Markdown4jProcessor

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

date = System.getProperty("date")
if (!date) {
	throw new IllegalStateException("Please specify -Ddate arg")
}

enUser = System.getProperty("enUser")
if (!enUser) {
	throw new IllegalStateException("Please evernote user with -DenUser arg")
}

enApp = System.getProperty("enApp")
if (!enApp) {
	throw new IllegalStateException("Please evernote app key with -DenApp arg")
}

sgPassword = System.getProperty("sgPassword")
if (!sgPassword) {
	throw new IllegalStateException("Please SendGrid password with -DsgPassword arg")
}

def markdownTemplate = new File("$date/digest.md")
def html = new Markdown4jProcessor().process(markdownTemplate)

def sendgrid = new SendGrid("latcraft", sgPassword)
def mailSender = { recipient ->
		def email = new SendGrid.Email(
			to: [ "eduards.sizovs@gmail.com" ], 
			from: "digest@latcraft.lv",
			fromName: "LatCraft Digest",
			subject: "Monthly Digest",
			html: html,
			replyTo: "no-reply@latcraft.lv"
		)
		email.addSubstitution "userName", recipient.first_name
		email.addSubstitution "pixies/", "https://raw.githubusercontent.com/latcraft/digest/master/$date/pixies/"

		def response = sendgrid.send(email)
		println "Sending email to $recipient.email -> $response.code / $response.message"
}		

def getEventIds() {
  observable = Observable
  .create { observer ->
    try {
    	eventbrite = new HTTPBuilder( 'https://www.eventbrite.com')
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
    	eventbrite = new HTTPBuilder( 'https://www.eventbrite.com' )
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
	.take(1) // !!!
	.subscribe mailSender









 



