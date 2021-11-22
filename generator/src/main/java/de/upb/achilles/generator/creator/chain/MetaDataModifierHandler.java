package de.upb.achilles.generator.creator.chain;

import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Should be last in the Chain ....
 *
 * <p>Modifies or removes the pom.xml within a jar file
 */
public class MetaDataModifierHandler extends OneTimeHandler {
  static final Logger LOGGER = LoggerFactory.getLogger(MetaDataModifierHandler.class);

  public MetaDataModifierHandler() {}

  @Override
  protected boolean canHandle(TestFixtureModel request) {
    // the gav has been modified so we need to modify it in the manifest, too
    return !(request.getOrgGav().equals(request.getGAV4Pom()));
  }

  @Override
  protected void handle(TestFixtureModel requTestFixtureModel) throws JarModificationException {

    try {
      Path jarFile = requTestFixtureModel.getJarFile();
      Path tempDirectory = Files.createTempDirectory(null);
      Path copiedFile = tempDirectory.resolve(Objects.requireNonNull(jarFile).getFileName());

      // copy the original file

      Files.copy(jarFile, copiedFile, StandardCopyOption.REPLACE_EXISTING);

      requTestFixtureModel.setJarFile(copiedFile);

      // modify the pom.xml in the jar file
      modifyMetaData(requTestFixtureModel);

    } catch (IOException e) {
      throw new JarModificationException(
          "Could not Modify metadata for " + requTestFixtureModel.getJarFile(),
          e,
          requTestFixtureModel);
    }
  }

  /**
   * Change the metadata in the contained pom.xml
   *
   * @param testFixtureModel the test fixture whos jar should be modified
   * @throws JarModificationException
   */
  private void modifyMetaData(TestFixtureModel testFixtureModel) throws JarModificationException {
    // modify the metadata

    Path jarFile = testFixtureModel.getJarFile();
    try (FileSystem zipfs =
        FileSystems.newFileSystem(Objects.requireNonNull(jarFile), (java.lang.ClassLoader) null)) {
      Path metaInfFolder = zipfs.getPath("/", "META-INF", "maven");
      boolean folderExistss = Files.exists(metaInfFolder);
      if (!folderExistss) {
        LOGGER.debug("Could not find META-INF/maven folder  in " + testFixtureModel.getOrgGav());
      } else {

        Files.walk(metaInfFolder)
            .filter(x -> x.getFileName().toString().equals("pom.xml"))
            .forEach(
                path -> {
                  try {
                    //     Charset charset = StandardCharsets.UTF_8;
                    //                  String content;
                    // content = new String(Files.readAllBytes(path), charset);

                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                    Document doc = docBuilder.parse(Files.newInputStream(path));
                    Element root = doc.getDocumentElement();
                    NodeList childNodes = root.getChildNodes();
                    for (int i = 0; i < childNodes.getLength(); i++) {
                      Node node = childNodes.item(i);
                      String nodeName = node.getNodeName();
                      switch (nodeName) {
                        case "artifactId":
                          node.setTextContent(testFixtureModel.getGAV4Pom().getArtifactId());
                          break;
                        case "groupId":
                          node.setTextContent(testFixtureModel.getGAV4Pom().getGroupId());
                          break;
                        case "version":
                          node.setTextContent(testFixtureModel.getGAV4Pom().getVersion());
                          break;
                      }
                    }

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    DOMSource source = new DOMSource(doc);
                    StreamResult result = new StreamResult(baos);
                    transformer.transform(source, result);

                    baos.close();

                    Files.write(
                        path,
                        baos.toByteArray(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
                  } catch (IOException
                      | ParserConfigurationException
                      | TransformerException
                      | SAXException e) {
                    LOGGER.error("Failed to modify the pom.xml in file " + path);
                  }
                });

        LOGGER.debug("Removed  the file " + metaInfFolder);
      }
    } catch (IOException e) {

      LOGGER.error("Failed to modify META-INF with", e);

      throw new JarModificationException("Failed to modify the pom.xml ", e, testFixtureModel);
    }
  }
}
