package lv.latcraft.digest;

import static com.google.common.collect.Iterables.size;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Splitter;
import groovy.lang.GroovyShell;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DigestResource {

    private final GroovyShell shell = new GroovyShell();

    private final String pathToPasswordRepo = System.getProperty("password.repo.local.location");

    @GET
    @Produces(TEXT_HTML)
    public DigestView sayHello() {
        String pathToPasswordRepo = System.getProperty("password.repo.local.location");
        return new DigestView(pathToPasswordRepo);
    }

    @POST
    @Path("/recipients")
    public int recipients(@FormParam("to") String to,
                          @FormParam("skipEventId") String skipEventId) throws IOException {

        String recipients = getRecipients(to, skipEventId);
         
        return size(Splitter.on("\n").omitEmptyStrings().split(recipients));
    }

    @POST
    @Path("/send")
    public void send(@FormParam("from") String from,
                     @FormParam("to") String to,
                     @FormParam("subject") String subject,
                     @FormParam("markdown") String markdown,
                     @FormParam("skipEventId") String skipEventId) throws IOException {

        String digestHtml = markdownToHtml(markdown);

        String recipients = getRecipients(to, skipEventId);

        System.setProperty("email.from", from);
        System.setProperty("email.to", recipients);
        System.setProperty("email.subject", subject);


        sendEmails(digestHtml, recipients);
    }

    private String markdownToHtml(String markdown) throws IOException {
        System.setProperty("template", markdown);
        return (String) shell.evaluate(new File("src/main/resources/markdown2html.groovy"));
    }

    private String getRecipients(String to, String skipEventId) throws IOException {
        String pathToPasswordRepo = System.getProperty("password.repo.local.location");
        String eventbriteJson = new String(readAllBytes(get(pathToPasswordRepo + "/eventbrite.json")));
        System.setProperty("eventbrite.json", eventbriteJson);
        System.setProperty("skip.event", skipEventId);
        String recipients = to;
        if (isEmpty(to)) {
            recipients = (String) shell.evaluate(new File("src/main/resources/collect_recipients.groovy"));
        }
        return recipients;
    }

    private void sendEmails(String digestHtml, String recipients) throws IOException {
        String sendgridJson = new String(readAllBytes(get(pathToPasswordRepo + "/sendgrid.json")));
        System.setProperty("sendgrid.json", sendgridJson);
        System.setProperty("email.body", digestHtml);
        System.setProperty("email.to", recipients);
        shell.evaluate(new File("src/main/resources/sender.groovy"));
    }
}
