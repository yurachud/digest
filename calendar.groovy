@Grab(group = 'org.mnode.ical4j', module = 'ical4j', version = '1.0.6')
import net.fortuna.ical4j.util.*
import net.fortuna.ical4j.data.*
import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.property.*
import net.fortuna.ical4j.model.parameter.*
import net.fortuna.ical4j.model.component.*

import static java.util.Objects.requireNonNull

def arg(name, desc) {
    def value = System.getProperty(name)
    requireNonNull(value, "Please specify -D${name} arg - ${desc}")
}

eventName = arg("name", "Event name")
eventLocation = arg("location", "Event location")
eventDescription = arg("description", "Event description")
eventDateTime = arg("dateTime", "DateTime in `d/M/yyyy H:m` format")

outfile = arg("out", "iCalendar output file name")

// Obtains local timezone object (in our case Europe/Riga)
TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry()
TimeZone timezone = registry.getTimeZone("Europe/Riga")

// Calculates event start/end datetime (from command line arguments)
java.util.Calendar startDate = new GregorianCalendar()
startDate.setTime(new Date().parse("d/M/yyyy H:m", eventDateTime, timezone))
java.util.Calendar endDate = new GregorianCalendar()
endDate.setTime(startDate.getTime())
endDate.add(java.util.Calendar.MINUTE, 30)

// Create the event
DateTime start = new DateTime(startDate.getTime())
DateTime end = new DateTime(endDate.getTime())
VEvent meeting = new VEvent(start, end, eventName)
// Add timezone info
VTimeZone tz = timezone.getVTimeZone()
meeting.getProperties().add(tz.getTimeZoneId())
// Add unique identifier
UidGenerator ug = new UidGenerator("latcraft")
meeting.getProperties().add(ug.generateUid())
// Add event location details
meeting.getProperties().add(new Location(eventLocation))
try{ def uri = new URL(eventLocation).toURI(); meeting.getProperties().add(new Url(uri)); }catch(MalformedURLException ignore){}
// Add description
meeting.getProperties().add(new Description(eventDescription))
// Add event organizer details
ParameterList pl = new ParameterList();
pl.add(new Cn("Latvian Software Craftsmanship Community"))      // Visible name of organizer
pl.add(new SentBy("calendar@latcraft.lv"))                      // Not important
def orgEmail = URI.create("mailto:calendar@latcraft.lv")        // Will be used in replies and accept/reject mail notifications
meeting.getProperties().add(new Organizer(pl, orgEmail))

// Create a calendar with events...
net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar()
calendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"))
calendar.getProperties().add(Version.VERSION_2_0)
calendar.getProperties().add(CalScale.GREGORIAN)
calendar.getComponents().add(meeting)

// Validate & printout to file
calendar.validate()
CalendarOutputter outputter = new CalendarOutputter()
outputter.output(calendar, new FileOutputStream(outfile))
