/*******************************************************************************
 * Copyright 2016 Matthew J. Dovey (www.ceridwen.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ceridwen.util.versioning;

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
 * <p>Title: RTSI</p> <p>Description: Real Time Self Issue</p> <p>Copyright:
 * </p>
 * 
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
    private LibraryIdentifier appId;
    private GridLayout gridLayout1 = new GridLayout();

    public AboutDialog(Frame frame, boolean modal, LibraryIdentifier appId) throws HeadlessException {
        super(frame, modal);
        try {
            this.appId = appId;
            this.jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        LibraryRegistry registry = new LibraryRegistry();
    	this.AppAuthorField.setFont(new java.awt.Font("Dialog", 1, 14));
        this.AppAuthorField.setText(registry.getLibraryVendor(this.appId));
        this.AppVersionField.setFont(new java.awt.Font("Dialog", 1, 14));
        this.AppVersionField.setText(registry.getLibraryVersion(this.appId));
        this.AppNameField.setFont(new java.awt.Font("Dialog", 1, 16));
        this.AppNameField.setText(registry.getLibraryName(this.appId));
        this.jPanel1.setLayout(this.gridLayout1);
        this.ComponentsTable.setFont(new java.awt.Font("Dialog", 1, 12));
        this.ComponentsTable.setMinimumSize(new Dimension(200, 50));
        this.ComponentsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.ComponentsTable.setColumnSelectionAllowed(false);
        this.ComponentsTable.setIntercellSpacing(new Dimension(1, 1));
        this.gridLayout1.setRows(3);
        this.getContentPane().add(this.jPanel1, BorderLayout.NORTH);
        this.jPanel1.add(this.AppNameField, null);
        this.jPanel1.add(this.AppVersionField, null);
        this.jPanel1.add(this.AppAuthorField, null);
        this.getContentPane().add(this.jScrollPane1, BorderLayout.CENTER);
        this.jScrollPane1.getViewport().add(this.ComponentsTable, null);

    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            DefaultTableModel table = new DefaultTableModel();
            table.addColumn("Component");
            table.addColumn("Version");
            table.addColumn("Author");
            LibraryRegistry registry = new LibraryRegistry();
            for (LibraryIdentifier id: registry.listLibraries()) {
                if (! id.equals(this.appId)) {
                    table.addRow(new String[] { registry.getLibraryName(id),
                    		registry.getLibraryVersion(id),
                    		registry.getLibraryVendor(id) });
                }
            }
            this.ComponentsTable.setModel(table);
            this.setSize(600, 200);
            this.doLayout();
        }
        super.setVisible(b);
    }
}
