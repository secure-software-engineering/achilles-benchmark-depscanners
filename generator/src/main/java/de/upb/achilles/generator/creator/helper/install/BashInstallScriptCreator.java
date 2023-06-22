package de.upb.achilles.generator.creator.helper.install;

import de.upb.achilles.generator.model.TestFixtureModel;
import java.nio.file.Path;
import java.util.Collection;

/** @author Andreas Dann created on 20.03.19 */
public class BashInstallScriptCreator extends AbstractInstallScriptCreator {

  private static final String UNIX_LINEENDING = "\n";

  public BashInstallScriptCreator(
      Collection<TestFixtureModel> testFixtureModels, Path projectPath) {
    super(testFixtureModels, projectPath);
  }

  protected String getScriptFileName() {
    return "m2InstallProject.sh";
  }

  @Override
  protected String executeDepCheckerCommand() {
    String cmd =
        "if [ -z \"$" + AbstractInstallScriptCreator.ENV_VAR_NAME + "\" ]; then" + getLineEnding();
    cmd += "\t echo \"No CMD to execute\"" + getLineEnding();
    cmd += "else" + getLineEnding();
    cmd += "\t eval \"$" + AbstractInstallScriptCreator.ENV_VAR_NAME + "\"" + getLineEnding();
    cmd += "fi";
    return cmd;
  }

  @Override
  protected String getLineEnding() {
    return UNIX_LINEENDING;
  }

  @Override
  protected String getCommentChars() {
    return "#";
  }

  @Override
  protected String createScriptHeader() {
    return "#!/bin/bash" + UNIX_LINEENDING;
  }
}
