package de.upb.achilles.generator.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.util.Callback;

/** @author Andreas Dann created on 07.01.19 */
public class ComboBoxTreeTableCellFactory<S, T>
    implements Callback<TreeTableColumn<S, T>, ComboBoxTreeTableCell> {

  private static final PseudoClass MY_PSEUDO_CLASS_STATE = PseudoClass.getPseudoClass("editable");
  private static final PseudoClass MY_PSEUDO_CLASS_STATE_NON =
      PseudoClass.getPseudoClass("noneditable");
  private ObservableList<T> nodes;

  public ObservableList<T> getNodes() {
    return nodes;
  }

  public void setNodes(ObservableList<T> nodes) {
    this.nodes = nodes;
  }

  @Override
  public ComboBoxTreeTableCell call(TreeTableColumn<S, T> column) {
    ComboBoxTreeTableCell cell;
    if (nodes != null) {
      cell = new ComboBoxTreeTableCell<>(nodes);
      cell.editableProperty()
          .bind(Bindings.selectBoolean(cell.tableRowProperty(), "item", "editable"));

    } else {

      cell = new ComboBoxTreeTableCell<>();
    }

    cell.setAlignment(Pos.CENTER);
    cell.pseudoClassStateChanged(MY_PSEUDO_CLASS_STATE_NON, !cell.isEditable());

    //    TreeTableCell<S, T> cell = new TreeTableCell<>();
    //    final ComboBox<T> comboBox = new ComboBox<>(nodes);
    //
    //    cell.itemProperty().addListener((observable, oldValue, newValue) -> {
    //      if (oldValue != null) {
    //        comboBox.valueProperty().unbindBidirectional((Property<T>) oldValue);
    //      }
    //      if (newValue != null) {
    //        comboBox.valueProperty().bindBidirectional((Property<T>) newValue);
    //      }
    //    });
    //
    //    cell.graphicProperty()
    //        .bind(Bindings.when(cell.emptyProperty()).then((Node) null).otherwise(comboBox));

    return cell;
  }
}
