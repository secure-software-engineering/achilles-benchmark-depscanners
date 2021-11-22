package de.upb.achilles.generator.creator.helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import de.upb.achilles.generator.model.TestFixtureDetailModel;
import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;

/** @author Andreas Dann created on 13.01.19 */
public class CustomTestFixtureModelSerializer extends StdSerializer<TestFixtureModel> {
  protected CustomTestFixtureModelSerializer(Class<TestFixtureModel> t) {
    super(t);
  }

  public void serialize(
      TestFixtureModel testFixtureModel,
      JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider)
      throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeObjectField("org_gav", testFixtureModel.getOrgGav());
    jsonGenerator.writeObjectField("gav", testFixtureModel.getGAV4Pom());
    jsonGenerator.writeStringField("cve", testFixtureModel.getCve());
    jsonGenerator.writeBooleanField("vulnerable", computeIsVulnerable(testFixtureModel));

    jsonGenerator.writeObjectField("details", testFixtureModel.getTestFixtureDetailModel());

    jsonGenerator.writeStringField("timestamp", testFixtureModel.getTimestamp());

    jsonGenerator.writeEndObject();
  }

  private boolean computeIsVulnerable(TestFixtureModel testFixtureModel) {
    // check if the vulnerable code is contained
    if (testFixtureModel.getTestFixtureDetailModel().length > 0) {
      boolean isVulnerable = false;
      for (TestFixtureDetailModel detailModel : testFixtureModel.getTestFixtureDetailModel()) {
        if (detailModel.isContained()) {
          isVulnerable = isVulnerable | detailModel.isInclude();
        }
      }

      return isVulnerable;
    }
    return testFixtureModel.isVulnerable();
  }
}
