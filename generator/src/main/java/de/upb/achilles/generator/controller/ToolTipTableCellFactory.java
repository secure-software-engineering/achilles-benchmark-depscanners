package de.upb.achilles.generator.controller;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

/** @author Andreas Dann created on 07.01.19 */
public class ToolTipTableCellFactory<S, T>
    implements Callback<TableColumnBase<S, T>, TableCell<S, T>> {

  public TableCell<S, T> call(TableColumnBase<S, T> param) {
    return (ToolTipTableCell<S, T>) new ToolTipTableCell();
  }

  public static class ToolTipTableCell<S, T> extends TableCell<S, T> {

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
