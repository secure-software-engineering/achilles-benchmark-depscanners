package de.upb.achilles.generator.creator.helper.install;

import de.upb.achilles.generator.model.TestFixtureModel;
import java.nio.file.Path;
import java.util.Collection;

/** @author Andreas Dann created on 20.03.19 */
public class BatInstallScriptCreator extends AbstractInstallScriptCreator {

  private static final String WIN_LINEENDING = "\n";

  public BatInstallScriptCreator(Collection<TestFixtureModel> testFixtureModels, Path projectPath) {
    super(testFixtureModels, projectPath);
  }

  @Override
  protected String getCommentChars() {
    return "::";
  }

  @Override
  protected String getLineEnding() {
    return WIN_LINEENDING;
  }

  @Override
  protected String createScriptHeader() {
    return "@echo on" + getLineEnding();
  }

  @Override
  protected String getScriptFileName() {
    return "m2InstallProject.bat";
  }

  @Override
  protected String executeDepCheckerCommand() {
    String cmd =
        "IF \"%"
            + AbstractInstallScriptCreator.ENV_VAR_NAME
            + "%\"==\"\" ECHO command is NOT defined"
            + getLineEnding();

    cmd +=
        "IF NOT \"%"
            + AbstractInstallScriptCreator.ENV_VAR_NAME
            + "%\"==\"\" %"
            + AbstractInstallScriptCreator.ENV_VAR_NAME
            + "%"
            + getLineEnding();
    return cmd;
  }
}
