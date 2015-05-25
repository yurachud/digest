@Grab('org.commonjava.googlecode.markdown4j:markdown4j:2.2-cj-1.0')
import org.markdown4j.Markdown4jProcessor

import static java.util.Objects.*

def arg(name) {
	def value = System.getProperty(name)
	requireNonNull(value, "Please specify -D$name arg")
}

template = arg "template"

def markdownTemplate = new File(template)
def html = new Markdown4jProcessor()
	.addHtmlAttribute("style", "font-size: 3em", "h1")
        .addHtmlAttribute("style", "font-size: 1.5em; line-height: 1.5em", "li")
	.addHtmlAttribute("style", "color: #878787; font-size: 1.5em; line-height: 1.5em", "p")
	.addHtmlAttribute("style", "font-weight: bold; color: #820000; text-decoration: none", "a")
	.process(markdownTemplate)

print html




 



