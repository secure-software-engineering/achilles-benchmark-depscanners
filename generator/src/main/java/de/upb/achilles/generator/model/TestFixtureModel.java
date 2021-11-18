package de.upb.achilles.generator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Callback;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.event.EventListenerList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Andreas Dann created on 05.01.19 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestFixtureModel {
  //    public static final Callback<TestFixtureModel, Observable[]> PROPERTY_EXTRACTOR
  //            = w -> new Observable[]{w.identifier, w.state};

  public static final Callback<TestFixtureModel, Observable[]> INCLUDE_PROPERTY_CALLBACK =
      w -> new Observable[] {w.include};
  private static final Logger LOGGER = LoggerFactory.getLogger(TestFixtureModel.class);
  private static int sequenceCounter = 0;
  private final SimpleStringProperty groupId;
  private final SimpleStringProperty artifactId;
  private final SimpleStringProperty version;
  private final SimpleStringProperty timestamp;
  private final SimpleStringProperty cve;
  private final ReadOnlyBooleanWrapper vulnerable;
  private final ReadOnlyBooleanWrapper containsCode;
  private final SimpleBooleanProperty include;
  private final String identifier;
  private final TestFixture testFixture;
  private final SimpleObjectProperty<GAVModification> changeGAV;
  private final SimpleObjectProperty<ByteCodeModification> byteCodeModification;
  private final TestFixtureDetailModel[] testFixtureDetailModel;
  private final int sequenceNo;
  private final BooleanProperty editable = new SimpleBooleanProperty(false);
  private final List<TestFixtureDetailModel> containedTestFixtureDetailModels;
  private final EventListenerList listeners = new EventListenerList();
  private HashSet<String> filesDeletedFromJar = new HashSet<>();
  private GAV gav4Pom;
  private Path jarFile;

  public TestFixtureModel(@Nonnull TestFixture testFixture) {
    groupId = new SimpleStringProperty(testFixture.getGav().getGroupId());
    artifactId = new SimpleStringProperty(testFixture.getGav().getArtifactId());
    version = new SimpleStringProperty(testFixture.getGav().getVersion());
    cve = new SimpleStringProperty(testFixture.getCve());
    timestamp = new SimpleStringProperty(testFixture.getTimestamp());
    vulnerable = new ReadOnlyBooleanWrapper(testFixture.isVulnerable());

    containsCode = new ReadOnlyBooleanWrapper(testFixture.getDetails().length > 0);

    include = new SimpleBooleanProperty(false);

    this.gav4Pom = null;

    this.testFixture = testFixture;

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(testFixture.getCve());
    stringBuilder.append(" ");
    stringBuilder.append(testFixture.getGav().toString());
    sequenceNo = TestFixtureModel.sequenceCounter++;

    identifier = stringBuilder.toString();

    this.changeGAV = new SimpleObjectProperty<>(GAVModification.ORG);

    this.byteCodeModification = new SimpleObjectProperty<>(ByteCodeModification.ORG);

    testFixtureDetailModel = new TestFixtureDetailModel[testFixture.getDetails().length];
    int i = 0;
    for (TestFixtureDetail testFixtureDetail : testFixture.getDetails()) {

      TestFixtureDetailModel newDetailModel = new TestFixtureDetailModel(testFixtureDetail);

      if (newDetailModel.isContained()) {
        // add the listener to make changes consistent
        newDetailModel
            .includeProperty()
            .addListener(
                (obs, oldVal, newVal) -> {
                  this.makeDetailSelectionConsistent(
                      newDetailModel, newDetailModel.getFile(), newVal);

                  // notify all other observer of this test fixture
                  DetailModelListener.DetailChangeEvent detailChangeEvent =
                      new DetailModelListener.DetailChangeEvent(
                          this, newDetailModel.getFile(), newVal);
                  this.notifyAdvertisement(detailChangeEvent);
                });
      }

      testFixtureDetailModel[i++] = newDetailModel;
    }
    containedTestFixtureDetailModels =
        Arrays.stream(testFixtureDetailModel)
            .filter(TestFixtureDetailModel::isContained)
            .collect(Collectors.toList());
  }

  public boolean isContainsCode() {
    return containsCode.get();
  }

  public void setContainsCode(boolean containsCode) {
    this.containsCode.set(containsCode);
  }

  public ReadOnlyBooleanWrapper containsCodeProperty() {
    return containsCode;
  }

  public String getTimestamp() {
    return timestamp.get();
  }

  public void setTimestamp(String timestamp) {
    this.timestamp.set(timestamp);
  }

  public SimpleStringProperty timestampProperty() {
    return timestamp;
  }

  public void makeDetailSelectionConsistent(
      @Nullable TestFixtureDetailModel source, @Nonnull String filename, boolean newIncludeValue) {
    // a file has been should be removed/included in the GAV
    // thus, the
    for (TestFixtureDetailModel testFixtureDetailModel : this.containedTestFixtureDetailModels) {
      if (testFixtureDetailModel == source) {
        continue;
      }

      // mark files as the same
      if (testFixtureDetailModel.getFile().equals(filename)) {
        testFixtureDetailModel.setInclude(newIncludeValue);
      }
    }
  }

  public void addAdListener(DetailModelListener listener) {
    listeners.add(DetailModelListener.class, listener);
  }

  public void removeAdListener(DetailModelListener listener) {
    listeners.remove(DetailModelListener.class, listener);
  }

  protected synchronized void notifyAdvertisement(DetailModelListener.DetailChangeEvent event) {
    for (DetailModelListener l : listeners.getListeners(DetailModelListener.class))
      l.advertisement(event);
  }

  public boolean getEditable() {
    return editable.get();
  }

  public void setEditable(boolean editable) {
    this.editable.set(editable);
  }

  public BooleanProperty editableProperty() {
    return editable;
  }

  public ByteCodeModification getByteCodeModification() {
    return byteCodeModification.get();
  }

  // the downloaded jar file

  public void setByteCodeModification(ByteCodeModification byteCodeModification) {
    this.byteCodeModification.set(byteCodeModification);
  }

  public SimpleObjectProperty<ByteCodeModification> byteCodeModificationProperty() {
    return byteCodeModification;
  }

  public int getSequenceNo() {
    return sequenceNo;
  }

  public String getTestFixtureIdentifier() {
    return this.identifier;
  }

  public String getGroupId() {
    return groupId.get();
  }

  public SimpleStringProperty groupIdProperty() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId.get();
  }

  public SimpleStringProperty artifactIdProperty() {
    return artifactId;
  }

  public String getVersion() {
    return version.get();
  }

  public SimpleStringProperty versionProperty() {
    return version;
  }

  public String getCve() {
    return cve.get();
  }

  public SimpleStringProperty cveProperty() {
    return cve;
  }

  public boolean isVulnerable() {
    return vulnerable.get();
  }

  public ReadOnlyBooleanProperty vulnerableProperty() {
    return vulnerable;
  }

  public HashSet<String> getFilesDeletedFromJar() {
    return filesDeletedFromJar;
  }

  public void setFilesDeletedFromJar(HashSet<String> filesDeletedFromJar) {
    this.filesDeletedFromJar = filesDeletedFromJar;
  }

  public boolean isInclude() {
    return include.get();
  }

  public void setInclude(boolean include) {
    this.include.set(include);
  }

  public SimpleBooleanProperty includeProperty() {
    return include;
  }

  public String getIdentifier() {
    return identifier;
  }

  public GAVModification getChangeGAV() {
    return changeGAV.get();
  }

  public SimpleObjectProperty<GAVModification> changeGAVProperty() {
    return changeGAV;
  }

  public GAV getGAV4Pom() {

    if (gav4Pom == null) {
      if (this.changeGAV.getValue() != GAVModification.ORG) {
        throw new RuntimeException("No GAV4Pom specified");
      } else if (this.changeGAV.getValue() == GAVModification.ORG) {
        return testFixture.getGav();
      }
    }

    return gav4Pom;
  }

  public void setGAV4Pom(GAV gav4Pom) {
    //    if (this.gav4Pom != null) {
    //      throw new RuntimeException("setGAV4Pom called twice?");
    //    }
    this.gav4Pom = gav4Pom;
  }

  public GAV getOrgGav() {
    return this.testFixture.getGav();
  }

  public TestFixtureDetailModel[] getTestFixtureDetailModel() {
    return testFixtureDetailModel;
  }

  public Collection<TestFixtureDetailModel> getTestFixtureDetailModelAsList() {
    return Arrays.asList(testFixtureDetailModel);
  }

  public boolean testFixtureDetailsChanges() {
    boolean res = false;
    for (TestFixtureDetailModel testFixtureDetailModel : this.testFixtureDetailModel) {
      res = res | (testFixtureDetailModel.isContained() != testFixtureDetailModel.isInclude());
    }
    return res;
  }

  public void updateIncludesAfterDelete() {
    if (this.getFilesDeletedFromJar().isEmpty()) {
      LOGGER.warn(String.format("No files deleted for the GAV: %s ", this.getOrgGav()));
    }

    for (TestFixtureDetailModel testFixtureDetailModel : this.testFixtureDetailModel) {
      String testFixtureFile = testFixtureDetailModel.getFile();
      if (this.getFilesDeletedFromJar().contains(testFixtureFile)) {
        testFixtureDetailModel.includeProperty().set(false);
      }
    }
  }

  @Nullable
  public Path getJarFile() {
    return jarFile;
  }

  public void setJarFile(Path jarFile) {
    this.jarFile = jarFile;
  }

  public int getId() {
    return Objects.hash(groupId.get(), artifactId.get(), version.get(), cve.get());
  }

  public boolean isChangeGAV() {
    return this.changeGAV.getValue() != GAVModification.ORG;
  }

  public void setChangeGAV(GAVModification changeGAV) {
    this.changeGAV.set(changeGAV);
  }
}
