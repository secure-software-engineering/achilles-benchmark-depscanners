package de.upb.achilles.generator.controller;

import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

/** @author Andreas Dann created on 07.01.19 */
public class CheckBoxTableCellFactory<S, T>
    implements Callback<TableColumn<S, T>, TableCell<S, T>> {

  private static final PseudoClass MY_PSEUDO_CLASS_STATE = PseudoClass.getPseudoClass("editable");
  private static final PseudoClass MY_PSEUDO_CLASS_STATE_NON =
      PseudoClass.getPseudoClass("roweditable");

  public CheckBoxTableCell<S, T> call(TableColumn<S, T> param) {

    CheckBoxTableCell<S, T> cell = new CheckBoxTableCell<>();
    cell.setAlignment(Pos.CENTER);
    // cell.pseudoClassStateChanged(MY_PSEUDO_CLASS_STATE, cell.isEditable());

    cell.editableProperty()
        .bind(Bindings.selectBoolean(cell.tableRowProperty(), "item", "editable"));

    cell.editableProperty()
        .addListener(
            (obs, oldValue, newValue) -> {
              cell.getTableRow()
                  .pseudoClassStateChanged(MY_PSEUDO_CLASS_STATE_NON, !cell.isEditable());
            });

    return cell;
  }
}
