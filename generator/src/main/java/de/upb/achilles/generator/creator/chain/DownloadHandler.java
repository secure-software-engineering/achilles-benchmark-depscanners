package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.creator.helper.jar.JarDownloader;
import de.upb.achilles.generator.model.ByteCodeModification;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Download the jar and its source from MavenCentral */
public class DownloadHandler extends OneTimeHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadHandler.class);

  @Override
  protected boolean canHandle(TestFixtureModel request) {
    return true;
  }

  @Override
  protected void handle(TestFixtureModel testFixtureModel) throws JarModificationException {

    // if we should recompile, then we download the sources-jar file from Maven Central
    boolean downloadSource =
        testFixtureModel.getByteCodeModification() == ByteCodeModification.FORCE_RECOMPILE;

    try {
      Path downloadedJar = JarDownloader.downloadJarFromCentral(testFixtureModel, downloadSource);
      testFixtureModel.setJarFile(downloadedJar);
    } catch (IOException e) {
      LOGGER.warn("Failed Download for", e);
      // remove from the selected test fixtures
      LOGGER.debug(
          "Removed "
              + testFixtureModel.getOrgGav()
              + " from selected list because downloading failed ",
          e);
      throw new JarModificationException(
          "Failed to download " + testFixtureModel.getOrgGav(), e, testFixtureModel);
    }
  }
}
