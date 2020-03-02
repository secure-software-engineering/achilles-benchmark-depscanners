package de.upb.achilles.generator.controller;

import javafx.scene.control.TableColumnBase;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableCell;
import javafx.util.Callback;

/** @author Andreas Dann created on 07.01.19 */
public class ToolTipTreeTableCellFactory<S, T>
    implements Callback<TableColumnBase<S, T>, TreeTableCell<S, T>> {

  public TreeTableCell<S, T> call(TableColumnBase<S, T> param) {
    return (ToolTipTableCell<S, T>) new ToolTipTableCell();
  }

  public static class ToolTipTableCell<S, T> extends TreeTableCell<S, T> {

    private final Tooltip tooltip = new Tooltip();

    @Override
    protected void updateItem(T item, boolean empty) {
      super.updateItem(item, empty);
      if (item != null) {
        setText(item.toString());
        tooltip.setText(item.toString());
        setTooltip(tooltip);
      }
    }
  }
}
