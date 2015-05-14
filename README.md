# w00t?

This repository contains both digest as well as scripts for sending emails to community members. SendGrid takes care of unsubscribed users automatically.

In order to send email, you have to perform three magic steps:

## 1. Build HTML from Markdown
```
groovy <parameters>  markdown2html.groovy > body.html
```

### Required parameters
`-Dtemplate` - relative path to markdown template

## 2. Collect and store recipients in a file
```
groovy <parameters> collect_recipients.groovy > recipients.txt
```

### Required parameters
`-Deventbrite.userKey` - User Key for accessing Eventbrite
 
`-Deventbrite.appKey` - App Key for accessing Eventbrite

### Optional parameters
`-Dskip.event` - if specified, does not collect members of the given event 

## 3. (Optional) Create monthly event iCalendar
```
groovy <parameters> calendar.groovy
```

### Required parameters
`-Dout` - iCalendar output file name
 
`-Dname` - Event name
 
`-Dlocation` - Event location
 
`-Ddescription` - Event description
 
`-DdateTime` - DateTime in `d/M/yyyy H:m` format

### Examples
```shell
groovy -Dout=latcraftonair.ics \
  -Dname="Latcraft On Air" \
  -Dlocation="https://www.youtube.com/channel/UCvzMZyJZZ3XYQwbvOACVYrQ" \
  -Ddescription="Talk with Java gods" \
  -DdateTime='17/05/2015 10:00' \
  calendar.groovy
```

```shell
groovy -Dout=pub.ics \
  -Dname="Pub" \
  -Dlocation="Beļgu alus" \
  -Ddescription="Pub meet" \
  -DdateTime='17/05/2015 10:00' \
  calendar.groovy                                  
```

## 4. Send email to recipients
```
groovy <parameters> sender.groovy < recipients.txt
```

### Required parameters
`-Dsendgrid.user` - SendGrid username

`-Dsendgrid.password` - SendGrid password

`-Demail.body` - relative path to email body (normally `body.html`)

`-Demail.from` - sender email (normally `hello@latcraft.lv`)

`-Demail.subject` - email subject

### Optional parameters
`-Dcalendar` - iCalendar (`*.ics`) file to be sent out as event invitation. 

# Misc
Access credentials can be obtained [here](https://github.com/latcraft/passwords).
