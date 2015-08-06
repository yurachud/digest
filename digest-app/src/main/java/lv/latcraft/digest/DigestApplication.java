package lv.latcraft.digest;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilderSpec;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class DigestApplication extends Application<DigestConfiguration> {

    private static final Logger log = Logger.getLogger(Application.class);

    public static void main(String[] args) throws Exception {

        if (isEmpty(System.getProperty("password.repo.local.location"))) {
            log.error("Property \"-Dpassword.repo.local.location\" should be defined. Example: \"/Users/yurychudnovsky/latcraft/passwords\" ");
            System.exit(0);
        }
        String[] startupProperties = { "server" };
        new DigestApplication().run(startupProperties);
    }

    @Override
    public String getName() {
        return "Digest application";
    }

    @Override
    public void initialize(Bootstrap<DigestConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<>());
    }

    @Override
    public void run(DigestConfiguration configuration, Environment environment) {
        environment.jersey().register(new DigestResource());
    }

}
