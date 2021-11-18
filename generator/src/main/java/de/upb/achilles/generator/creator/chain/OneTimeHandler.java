package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.nio.file.Path;
import java.util.HashMap;
import javax.annotation.Nonnull;

/** Is executed only once per GAV */
public abstract class OneTimeHandler extends Handler {

  private final HashMap<GAV, TestFixtureModel> modifiedArtifacts = new HashMap<>();

  @Override
  public void handleRequest(@Nonnull TestFixtureModel request) throws JarModificationException {
    if (this.canHandle(request)) {

      GAV testFixGav = request.getOrgGav();
      if (modifiedArtifacts.containsKey(testFixGav)) {
        // share the recompiled artifact for all fixtures targeting the same dependency/artefact
        Path recompiledJAr = modifiedArtifacts.get(testFixGav).getJarFile();
        request.setJarFile(recompiledJAr);

      } else {
        handle(request);
        modifiedArtifacts.put(testFixGav, request);
      }
    }
    if (nextHandler != null) {
      nextHandler.handleRequest(request);
    }
  }
}
