package de.upb.achilles.generator.creator.helper.install;

import de.upb.achilles.generator.model.TestFixtureModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractInstallScriptCreator {

  public static final String ENV_VAR_NAME = "DEPCMD";
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
  private final List<String> commands;
  private final String fullScriptPath;

  public AbstractInstallScriptCreator(
      Collection<TestFixtureModel> testFixtureModels, Path projectPath) {

    this.commands = new ArrayList<>();

    for (TestFixtureModel entry : testFixtureModels) {
      String[] cmd =
          JarInstaller.mvnInstallCommand(
              entry.getGAV4Pom(), Objects.requireNonNull(entry.getJarFile()), projectPath);
      String joinedCmd = StringUtils.join(cmd, " ");
      commands.add(joinedCmd);
    }
    this.fullScriptPath = projectPath.resolve(getScriptFileName()).toAbsolutePath().toString();
  }

  public void createScript() throws IOException {
    Path file = Paths.get(fullScriptPath);
    String bashScriptText = this.createScriptAsString();
    Files.write(file, bashScriptText.getBytes(), StandardOpenOption.CREATE);
  }

  private String createScriptAsString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(createScriptHeader());
    stringBuilder.append(getLineEnding());
    LocalDateTime now = LocalDateTime.now();
    String timestamp = (dtf.format(now));
    stringBuilder.append(getCommentChars()).append(" ").append(timestamp);
    stringBuilder.append(getLineEnding());
    stringBuilder.append(getLineEnding());

    stringBuilder.append(createCommandsForScript());
    stringBuilder.append(getLineEnding());

    stringBuilder
        .append(getCommentChars())
        .append(" ")
        .append("execute the command in the environment variable");

    stringBuilder
        .append(getCommentChars())
        .append(" ")
        .append("for instance, export DEPCMD=\"mvn org.owasp:dependency-check-maven:check\"");

    stringBuilder.append(getLineEnding());

    stringBuilder.append(executeDepCheckerCommand());

    stringBuilder.append(getLineEnding());
    stringBuilder.append(getLineEnding());

    return stringBuilder.toString();
  }

  private String createCommandsForScript() {
    StringBuilder stringBuilder = new StringBuilder();
    for (String cmd : this.commands) {
      stringBuilder.append(cmd);
      stringBuilder.append(getLineEnding());
    }

    return stringBuilder.toString();
  }

  protected abstract String getLineEnding();

  protected abstract String getCommentChars();

  protected abstract String createScriptHeader();

  protected abstract String getScriptFileName();

  protected abstract String executeDepCheckerCommand();
}
