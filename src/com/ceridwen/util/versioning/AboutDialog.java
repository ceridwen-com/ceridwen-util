/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * <http://www.gnu.org/licenses/>.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Matthew J. Dovey - initial API and implementation
 ******************************************************************************/
package com.ceridwen.util.versioning;

import java.util.Iterator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>

 * @author Matthew J. Dovey
 * @version 1.0
 */

public class AboutDialog extends JDialog {
  /**
	 * 
	 */
	private static final long serialVersionUID = -5422140871803919187L;
private JPanel jPanel1 = new JPanel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private JLabel AppAuthorField = new JLabel();
  private JLabel AppVersionField = new JLabel();
  private JLabel AppNameField = new JLabel();
  private JTable ComponentsTable = new JTable();
  private Class<?> app;
  private GridLayout gridLayout1 = new GridLayout();

  public AboutDialog(Frame frame, boolean modal, Class<?> app) throws HeadlessException {
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

  public void setVisible(boolean b) {
    if (b) {
      DefaultTableModel table = new DefaultTableModel();
      table.addColumn("Component");
      table.addColumn("Version");
      table.addColumn("Author");
      Iterator<?> iterate = ComponentRegistry.listRegisteredComponents();
      while (iterate.hasNext()) {
        Class<?> component = (Class<?>) iterate.next();
        if (component != app) {
          table.addRow(new String[] {ComponentRegistry.getName(component),
                       ComponentRegistry.getVersionString(component),
                       ComponentRegistry.getAuthor(component)});
        }
      }
      ComponentsTable.setModel(table);
      this.setSize(600, 200);
      this.doLayout();
    }
    super.setVisible(b);
  }
}
