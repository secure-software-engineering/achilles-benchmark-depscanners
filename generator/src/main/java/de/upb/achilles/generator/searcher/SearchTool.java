package de.upb.achilles.generator.searcher;

/*
 * Open Source Software published under the Apache Licence, Version 2.0.
 */

import de.upb.achilles.generator.model.TestFixtureModel;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;

public final class SearchTool {
  private SearchTool() {
    // Prevent instantiation - all methods are static
  }

  public static Predicate<TestFixtureModel> matchMaker(final String text) {
    String search = normalise(text.trim());

    return w -> isMatch(w, search);
  }

  private static boolean isMatch(final TestFixtureModel w, final String searchText) {
    return normalise(w.getTestFixtureIdentifier()).contains(searchText);
  }

  private static String normalise(final String s) {
    return StringUtils.stripAccents(s).toLowerCase(Locale.ENGLISH);
  }

  public static int getMatchIndex(
      final List<? extends TestFixtureModel> matches, final TestFixtureModel currentWord) {
    return IntStream.range(0, matches.size())
        .filter(i -> matches.get(i).equals(currentWord))
        .findFirst()
        .orElse(-1);
  }

  public static int getPreviousMatchIndex(
      final List<? extends TestFixtureModel> matches, final TestFixtureModel currentWord) {
    int currentSequenceNo = currentWord.getSequenceNo();
    int matchCount = matches.size();

    return IntStream.range(0, matchCount)
        .map(i -> matchCount - i - 1)
        .filter(i -> matches.get(i).getSequenceNo() < currentSequenceNo)
        .findFirst()
        .orElse(-1);
  }
}
