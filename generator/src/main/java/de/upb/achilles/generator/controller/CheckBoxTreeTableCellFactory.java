package de.upb.achilles.generator.controller;

import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.util.Callback;

/** @author Andreas Dann created on 07.01.19 */
public class CheckBoxTreeTableCellFactory<S, T>
    implements Callback<TreeTableColumn<S, T>, TreeTableCell<S, T>> {

  private static final PseudoClass MY_PSEUDO_CLASS_STATE = PseudoClass.getPseudoClass("editable");
  private static final PseudoClass MY_PSEUDO_CLASS_STATE_NON =
      PseudoClass.getPseudoClass("noneditable");

  PseudoClass up = PseudoClass.getPseudoClass("up");

  public CheckBoxTreeTableCell<S, T> call(TreeTableColumn<S, T> param) {

    CheckBoxTreeTableCell<S, T> cell = new CheckBoxTreeTableCell<>();
    cell.setAlignment(Pos.CENTER);
    cell.editableProperty()
        .bind(Bindings.selectBoolean(cell.tableRowProperty(), "item", "editable"));
    cell.pseudoClassStateChanged(MY_PSEUDO_CLASS_STATE, cell.isEditable());

    //    TreeTableRow<S> treeTableRow = cell.getTreeTableRow();
    //    if (treeTableRow != null) {
    //      treeTableRow
    //          .treeItemProperty()
    //          .addListener(
    //              (obs, oldValue, newValue) -> {
    //                if (newValue == null) {
    //                  cell.setEditable(false);
    //                }
    //                //              else if (newValue.getClass() == TestFixtureModel.class) {
    //                //                cell.setEditable(false);
    //                //
    //                //              } else if (newValue.getClass() ==
    // TestFixtureModelParent.class)
    // {
    //                //                cell.setEditable(true);
    //                //              }
    //              });
    //    }

    //    cell.itemProperty()
    //        .addListener(
    //            (obs, oldValue, newValue) -> {
    //              TreeTableRow row = cell.getTreeTableRow();
    //
    //              if (row == null) {
    //              } else {
    //                TreeItem item = cell.getTreeTableRow().getTreeItem();
    //                if (item == null) {;
    //                } else if (item.getValue().getClass() == TestFixtureModel.class) {
    //                  row.setDisable(true);
    //                }
    //              }
    //            });
    return cell;
  }
}
