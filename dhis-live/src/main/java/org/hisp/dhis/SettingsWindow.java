/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis;


import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JFileChooser;
import java.io.File;
import javax.swing.JFrame;
import org.hisp.dhis.config.ConfigType.DatabaseConfiguration.ConnectionTypes.ConnectionType;
import org.hisp.dhis.config.ConfigType.DatabaseConfiguration.DatabaseConnections.Connection;

public class SettingsWindow extends JFrame
{

    public SettingsWindow()
    {
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( Exception ex )
        {
            JOptionPane.showMessageDialog( null, ex.getMessage() );
        }
        initComponents();
        setLocationRelativeTo( null );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        config = TrayApp.config;
        appConfigPanel = new javax.swing.JPanel();
        portLabel = new javax.swing.JLabel();
        portField = new javax.swing.JTextField();
        hostLabel = new javax.swing.JLabel();
        hostField = new javax.swing.JTextField();
        browserPathLabel = new javax.swing.JLabel();
        browserPathField = new javax.swing.JTextField();
        browserPathButton = new javax.swing.JButton();
        langLabel = new javax.swing.JLabel();
        countryLabel = new javax.swing.JLabel();
        langField = new javax.swing.JTextField();
        countryField = new javax.swing.JTextField();
        maxSizeLabel = new javax.swing.JLabel();
        maxSizeField = new javax.swing.JTextField();
        maxSizeDefaultLabel = new javax.swing.JLabel();
        unitLabel = new javax.swing.JLabel();
        databaseConfigPanel = new javax.swing.JPanel();
        connTypePanel = new javax.swing.JPanel();
        connTypePane = new javax.swing.JScrollPane();
        connTypeTable = new javax.swing.JTable();
        connTypeAddButton = new javax.swing.JButton();
        connPanel = new javax.swing.JPanel();
        connPane = new javax.swing.JScrollPane();
        connTable = new javax.swing.JTable();
        connAddButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DHIS 2 Live - Settings");
        setAlwaysOnTop(true);
        setResizable(false);

        appConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Application Configuration"));

        portLabel.setText("Port:");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.port}"), portField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"), "portBinding");
        bindingGroup.addBinding(binding);

        hostLabel.setText("Host:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.host}"), hostField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"), "hostBinding");
        bindingGroup.addBinding(binding);

        browserPathLabel.setText("Preferred Browser Path:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.preferredBrowser}"), browserPathField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"), "browserPathBinding");
        bindingGroup.addBinding(binding);

        browserPathButton.setText("Browse");
        browserPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browserPathButtonActionPerformed(evt);
            }
        });

        langLabel.setText("Language:");

        countryLabel.setText("Country:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.localeLanguage}"), langField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"), "langBinding");
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.localeCountry}"), countryField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_FOCUS_LOST"), "countryBinding");
        bindingGroup.addBinding(binding);

        maxSizeLabel.setText("Max Form Content Size:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${appConfiguration.maxFormContentSize}"), maxSizeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        maxSizeDefaultLabel.setText("(* requires restart of DHIS 2 Live - default: 200000)");

        unitLabel.setText("(bytes)");

        javax.swing.GroupLayout appConfigPanelLayout = new javax.swing.GroupLayout(appConfigPanel);
        appConfigPanel.setLayout(appConfigPanelLayout);
        appConfigPanelLayout.setHorizontalGroup(
            appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appConfigPanelLayout.createSequentialGroup()
                        .addComponent(browserPathLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browserPathField, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(browserPathButton)
                        .addGap(8, 8, 8))
                    .addGroup(appConfigPanelLayout.createSequentialGroup()
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, appConfigPanelLayout.createSequentialGroup()
                                .addComponent(langLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(langField))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, appConfigPanelLayout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(hostLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hostField, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(appConfigPanelLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(portLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(appConfigPanelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(countryLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(countryField)))
                        .addGap(18, 18, 18)
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maxSizeDefaultLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                            .addGroup(appConfigPanelLayout.createSequentialGroup()
                                .addComponent(maxSizeLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(unitLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        appConfigPanelLayout.setVerticalGroup(
            appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appConfigPanelLayout.createSequentialGroup()
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portLabel)
                    .addComponent(hostLabel)
                    .addComponent(maxSizeLabel)
                    .addComponent(maxSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitLabel))
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appConfigPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(maxSizeDefaultLabel))
                    .addGroup(appConfigPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(langLabel)
                            .addComponent(langField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(countryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(countryLabel))))
                .addGap(18, 18, 18)
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browserPathLabel)
                    .addComponent(browserPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browserPathButton))
                .addContainerGap())
        );

        databaseConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Database Configuration"));

        connTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Connection Types"));

        connTypeTable.setColumnSelectionAllowed(true);
        connTypeTable.getTableHeader().setReorderingAllowed(false);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${databaseConfiguration.connectionTypes.connectionType}");
        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, eLProperty, connTypeTable);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id}"));
        columnBinding.setColumnName("ID");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${dialect}"));
        columnBinding.setColumnName("Dialect");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${driverClass}"));
        columnBinding.setColumnName("Driver Class");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        connTypePane.setViewportView(connTypeTable);
        connTypeTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        connTypeAddButton.setText("Add New Connection Type");
        connTypeAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connTypeAddButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout connTypePanelLayout = new javax.swing.GroupLayout(connTypePanel);
        connTypePanel.setLayout(connTypePanelLayout);
        connTypePanelLayout.setHorizontalGroup(
            connTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connTypePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(connTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(connTypePane, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                    .addComponent(connTypeAddButton))
                .addContainerGap())
        );
        connTypePanelLayout.setVerticalGroup(
            connTypePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connTypePanelLayout.createSequentialGroup()
                .addComponent(connTypePane, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(connTypeAddButton)
                .addGap(6, 6, 6))
        );

        connPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Connections"));

        connTable.setColumnSelectionAllowed(true);
        connTable.getTableHeader().setReorderingAllowed(false);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${databaseConfiguration.databaseConnections.connection}");
        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, eLProperty, connTable);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${id}"));
        columnBinding.setColumnName("ID");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${type}"));
        columnBinding.setColumnName("Type");
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${name}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${URL}"));
        columnBinding.setColumnName("URL");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${userName}"));
        columnBinding.setColumnName("Username");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${password}"));
        columnBinding.setColumnName("Password");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, config, org.jdesktop.beansbinding.ELProperty.create("${databaseConfiguration.databaseConnections.selected}"), connTable, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        connPane.setViewportView(connTable);
        connTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        connTable.getColumnModel().getColumn(3).setPreferredWidth(60);

        connAddButton.setText("Add New Connection");
        connAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connAddButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout connPanelLayout = new javax.swing.GroupLayout(connPanel);
        connPanel.setLayout(connPanelLayout);
        connPanelLayout.setHorizontalGroup(
            connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connPanelLayout.createSequentialGroup()
                .addContainerGap(450, Short.MAX_VALUE)
                .addComponent(connAddButton)
                .addContainerGap())
            .addGroup(connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(connPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(connPane, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        connPanelLayout.setVerticalGroup(
            connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, connPanelLayout.createSequentialGroup()
                .addContainerGap(101, Short.MAX_VALUE)
                .addComponent(connAddButton)
                .addContainerGap())
            .addGroup(connPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(connPanelLayout.createSequentialGroup()
                    .addComponent(connPane, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(41, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout databaseConfigPanelLayout = new javax.swing.GroupLayout(databaseConfigPanel);
        databaseConfigPanel.setLayout(databaseConfigPanelLayout);
        databaseConfigPanelLayout.setHorizontalGroup(
            databaseConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, databaseConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(databaseConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(connPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(connTypePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        databaseConfigPanelLayout.setVerticalGroup(
            databaseConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseConfigPanelLayout.createSequentialGroup()
                .addComponent(connTypePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(connPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        saveButton.setText("Save and Close");
        saveButton.setToolTipText("Save and Close");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(databaseConfigPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(appConfigPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(databaseConfigPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveButtonActionPerformed
    {//GEN-HEADEREND:event_saveButtonActionPerformed
        TrayApp.getInstance().writeConfigToFile();
        this.dispose();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void browserPathButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_browserPathButtonActionPerformed
    {//GEN-HEADEREND:event_browserPathButtonActionPerformed
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
        int returnVal = fc.showOpenDialog( this );
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            File file = fc.getSelectedFile();
            browserPathField.setText( file.getAbsolutePath() );
        }
    }//GEN-LAST:event_browserPathButtonActionPerformed

    private void connTypeAddButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_connTypeAddButtonActionPerformed
    {//GEN-HEADEREND:event_connTypeAddButtonActionPerformed
        TrayApp.databaseConfig.getConnectionTypes().getConnectionType().add( new ConnectionType() );
        bindingGroup.unbind();
        bindingGroup.bind();
    }//GEN-LAST:event_connTypeAddButtonActionPerformed

    private void connAddButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_connAddButtonActionPerformed
    {//GEN-HEADEREND:event_connAddButtonActionPerformed
        TrayApp.databaseConfig.getDatabaseConnections().getConnection().add( new Connection() );
        bindingGroup.unbind();
        bindingGroup.bind();
    }//GEN-LAST:event_connAddButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel appConfigPanel;
    private javax.swing.JButton browserPathButton;
    private javax.swing.JTextField browserPathField;
    private javax.swing.JLabel browserPathLabel;
    private org.hisp.dhis.config.ConfigType config;
    private javax.swing.JButton connAddButton;
    private javax.swing.JScrollPane connPane;
    private javax.swing.JPanel connPanel;
    private javax.swing.JTable connTable;
    private javax.swing.JButton connTypeAddButton;
    private javax.swing.JScrollPane connTypePane;
    private javax.swing.JPanel connTypePanel;
    private javax.swing.JTable connTypeTable;
    private javax.swing.JTextField countryField;
    private javax.swing.JLabel countryLabel;
    private javax.swing.JPanel databaseConfigPanel;
    private javax.swing.JTextField hostField;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JTextField langField;
    private javax.swing.JLabel langLabel;
    private javax.swing.JLabel maxSizeDefaultLabel;
    private javax.swing.JTextField maxSizeField;
    private javax.swing.JLabel maxSizeLabel;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel unitLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
