package com.ceridwen.util.versioning;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>

 * @author Matthew J. Dovey
 * @version 1.0
 */

public class AboutDialog extends JDialog {
  JPanel jPanel1 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JLabel AppAuthorField = new JLabel();
  JLabel AppVersionField = new JLabel();
  JLabel AppNameField = new JLabel();
  JTable ComponentsTable = new JTable();
  Class app;
  GridLayout gridLayout1 = new GridLayout();

  public AboutDialog(Frame frame, boolean modal, Class app) throws HeadlessException {
    super(frame,modal);
    try {
      this.app = app;
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    AppAuthorField.setFont(new java.awt.Font("Dialog", 1, 14));
    AppAuthorField.setText(ComponentRegistry.getAuthor(app));
    AppVersionField.setFont(new java.awt.Font("Dialog", 1, 14));
    AppVersionField.setText(ComponentRegistry.getVersionString(app));
    AppNameField.setFont(new java.awt.Font("Dialog", 1, 16));
    AppNameField.setText(ComponentRegistry.getName(app));
    jPanel1.setLayout(gridLayout1);
    ComponentsTable.setFont(new java.awt.Font("Dialog", 1, 12));
    ComponentsTable.setMinimumSize(new Dimension(200, 50));
    ComponentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    ComponentsTable.setColumnSelectionAllowed(false);
    ComponentsTable.setIntercellSpacing(new Dimension(1, 1));
    gridLayout1.setRows(3);
    this.getContentPane().add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(AppAuthorField, null);
    jPanel1.add(AppVersionField, null);
    jPanel1.add(AppNameField, null);
    this.getContentPane().add(jScrollPane1,  BorderLayout.CENTER);
    jScrollPane1.getViewport().add(ComponentsTable, null);


  }


  static {
    ComponentRegistry.registerComponent(AboutDialog.class);
  }
  public void show() {
    DefaultTableModel table = new DefaultTableModel();
    table.addColumn("Component");
    table.addColumn("Version");
    table.addColumn("Author");
    Iterator iterate = ComponentRegistry.listRegisteredComponents();
    while (iterate.hasNext()) {
      Class component = (Class)iterate.next();
      if (component != app)
        table.addRow(new String[]{ComponentRegistry.getName(component), ComponentRegistry.getVersionString(component), ComponentRegistry.getAuthor(component)});
    }
    ComponentsTable.setModel(table);
    this.setSize(600, 200);
    this.doLayout();
    super.show();
  }
}
