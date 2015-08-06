package lv.latcraft.digest;

import java.nio.charset.Charset;

import io.dropwizard.views.View;

public class DigestView extends View {

    public String pathToPasswordRepo;

    protected DigestView(String pathToPasswordRepo) {
        super("index.ftl", Charset.forName("UTF-8"));
        this.pathToPasswordRepo=pathToPasswordRepo;
    }

    public String getPathToPasswordRepo() {
        return pathToPasswordRepo;
    }
}
