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
`-Deventbrite.userKey` - User Key for accessing Eventbrite available [here](https://github.com/latcraft/passwords)
 
`-Deventbrite.appKey` - App Key for accessing Eventbrite available [here](https://github.com/latcraft/passwords)

### Optional parameters
`-Dskip.event` - if specified, does not collect members of the given event 
 

## 3. Send email to recipients
```
groovy <parameters> sender.groovy < recipients.txt
```

### Required parameters

`-Dsendgrid.user` - SendGrid username

`-Dsendgrid.password` - SendGrid password

`-Demail.body` - relative path to email body (normally `body.html`)

`-Demail.from` - sender email (normally `hello@latcraft.lv`)

`-Demail.subject` - email subject