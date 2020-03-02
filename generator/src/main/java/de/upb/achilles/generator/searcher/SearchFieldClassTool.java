package de.upb.achilles.generator.searcher;

import javafx.css.PseudoClass;
import javafx.scene.Node;

/** @author Andreas Dann created on 05.01.19 */
public final class SearchFieldClassTool {
  private static final PseudoClass CLASS_FAIL = PseudoClass.getPseudoClass("fail");

  private SearchFieldClassTool() {
    // Prevent instantiation - all methods are private
  }

  public static void updateStateClass(final Node node, final boolean isFail) {
    node.pseudoClassStateChanged(CLASS_FAIL, isFail);
  }
}
