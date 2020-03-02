package de.upb.achilles.generator.creator.helper.jar;

import de.upb.achilles.generator.creator.Util;
import de.upb.achilles.generator.model.GAV;
import de.upb.achilles.generator.model.TestFixtureModel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class JarDownloader {
  // URL where to download the JAR files from
  public static final String HTTP_CENTRAL_MAVEN_ORG_MAVEN_DOWNLOAD_URL =
      "http://central.maven.org/maven2/%s/%s/%s/%s";
  // in milliseconds for downloading from MAVEN Central
  private static final int CONNECT_TIMEOUT = 15000;
  private static final int READ_TIMEOUT = 15000;

  private static final Logger LOGGER = LoggerFactory.getLogger(JarDownloader.class);

  public static Path downloadJarFromCentral(TestFixtureModel testFixtureModel) throws IOException {
    return downloadJarFromCentral(testFixtureModel, false);
  }

  public static Path downloadJarFromCentral(
      TestFixtureModel testFixtureModel, boolean downloadSource) throws IOException {
    Path fileName;
    Path tmpPath = Files.createTempDirectory("tmpfiles");

    if (downloadSource) {
      fileName =
          downloadFileFromMavenCentral(
              tmpPath,
              testFixtureModel.getOrgGav(),
              Util.getSourceJARFileName(testFixtureModel.getOrgGav()));
      downloadFileFromMavenCentral(
          tmpPath, testFixtureModel.getOrgGav(), Util.getPOMFileName(testFixtureModel.getOrgGav()));
    } else {
      fileName =
          downloadFileFromMavenCentral(
              tmpPath,
              testFixtureModel.getOrgGav(),
              Util.getJARFileName(testFixtureModel.getOrgGav()));
    }

    testFixtureModel.setJarFile(fileName);

    return fileName;
  }

  private static Path downloadFileFromMavenCentral(Path targetDir, GAV orgGav, String file)
      throws IOException {
    String group_url = orgGav.getGroupId().replace(".", "/");
    String artifact_url = orgGav.getArtifactId();
    String version_url = orgGav.getVersion();
    String downloadURL =
        String.format(
            HTTP_CENTRAL_MAVEN_ORG_MAVEN_DOWNLOAD_URL, group_url, artifact_url, version_url, file);

    Path fileName = targetDir.resolve(file);

    FileUtils.copyURLToFile(new URL(downloadURL), fileName.toFile(), CONNECT_TIMEOUT, READ_TIMEOUT);

    // Files.copy(in, fileName, StandardCopyOption.REPLACE_EXISTING);

    LOGGER.debug(String.format("Downloaded %s using url %s", orgGav, downloadURL));

    return fileName;
  }
}
