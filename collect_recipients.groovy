@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7.1')
import groovyx.net.http.*

@Grab('com.netflix.rxjava:rxjava-groovy:0.14.0')
import rx.Observable

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import static java.util.Objects.*

def arg(name) {
	def value = System.getProperty(name)
	requireNonNull(value, "Please specify -D$name arg")
}

eventbrite_userKey = arg "eventbrite.userKey"
eventbrite_appKey = arg "eventbrite.appKey"
eventToSkip = System.getProperty("skip.event")

def eventbriteAttendees() {
  eventbrite = new HTTPBuilder('https://www.eventbrite.com')
  credentials = [ user_key: eventbrite_userKey, app_key: eventbrite_appKey ]
  observable = Observable.create { observer ->
		eventbrite.request( GET, JSON ) {
		  uri.path = '/json/user_list_events'
		  uri.query = credentials + [ only_display: 'id' ]
		  response.success = { _, json ->
		  	observer.onNext(json)
		  	observer.onCompleted()
		  }
		} 
  }.flatMap { response -> 
  	Observable
  		.from(response.events*.event)
  }.flatMap { event -> 
	Observable.create { observer ->
			eventbrite.request( GET, JSON ) {
			  uri.path = '/json/event_list_attendees'
			  uri.query = credentials + [ id: event.id, only_display: 'email' ]
			  response.success = { _, json ->
			  	observer.onNext(json)
			  	observer.onCompleted()
			  }
			} 
  	}.flatMap { 
  		response -> Observable.from(response.attendees*.attendee.email) 
  	}
  }
}

def websiteSubscribers() {
  observable = Observable.create { observer ->
    	eventbrite = new HTTPBuilder('https://radiant-fire-3288.firebaseio.com')
		eventbrite.request( GET, JSON ) {
		  uri.path = '/subscribers.json'
		  response.success = { _, json ->
		  	observer.onNext(json)
		  	observer.onCompleted()
		  }
		} 
  }
  .flatMap { response -> Observable.from(response*.value)  }	
}


def getAttendees(eventId) {
    	eventbrite = new HTTPBuilder('https://www.eventbrite.com')
		eventbrite.request( GET, JSON ) {
		  uri.path = '/json/event_list_attendees'
		  uri.query = [ id: eventId, user_key:eventbrite_userKey, app_key: eventbrite_appKey, only_display: 'email' ]

		  response.success = { _, json ->
		  	return json.attendees*.attendee.email
		  }
		} 
}

if (eventToSkip) {
	attendessToSkip = getAttendees(eventToSkip)
}

Observable.merge(
	eventbriteAttendees(),
	websiteSubscribers()
)
	.distinct { it }
	.filter { !!it }
	.filter { !eventToSkip || !attendessToSkip.contains(it)}
	.subscribe { println it }








 



