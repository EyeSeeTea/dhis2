package org.hisp.dhis;


import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.JFileChooser;
import java.io.File;

public class SettingsWindow extends javax.swing.JFrame
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
        databaseConfigPanel = new javax.swing.JPanel();
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

        javax.swing.GroupLayout appConfigPanelLayout = new javax.swing.GroupLayout(appConfigPanel);
        appConfigPanel.setLayout(appConfigPanelLayout);
        appConfigPanelLayout.setHorizontalGroup(
            appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appConfigPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appConfigPanelLayout.createSequentialGroup()
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(hostLabel)
                            .addComponent(langLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(langField)
                            .addComponent(hostField, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(countryLabel)
                            .addComponent(portLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(countryField)
                            .addComponent(portField, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)))
                    .addGroup(appConfigPanelLayout.createSequentialGroup()
                        .addComponent(browserPathLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browserPathField, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browserPathButton)))
                .addContainerGap(64, Short.MAX_VALUE))
        );
        appConfigPanelLayout.setVerticalGroup(
            appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appConfigPanelLayout.createSequentialGroup()
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portLabel)
                    .addComponent(hostLabel))
                .addGap(18, 18, 18)
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(langLabel)
                    .addComponent(countryLabel)
                    .addComponent(langField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(countryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(appConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browserPathLabel)
                    .addComponent(browserPathField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browserPathButton))
                .addContainerGap())
        );

        databaseConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Database Configuration"));

        javax.swing.GroupLayout databaseConfigPanelLayout = new javax.swing.GroupLayout(databaseConfigPanel);
        databaseConfigPanel.setLayout(databaseConfigPanelLayout);
        databaseConfigPanelLayout.setHorizontalGroup(
            databaseConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 540, Short.MAX_VALUE)
        );
        databaseConfigPanelLayout.setVerticalGroup(
            databaseConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 117, Short.MAX_VALUE)
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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(databaseConfigPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(appConfigPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(saveButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(databaseConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(saveButton)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveButtonActionPerformed
    {//GEN-HEADEREND:event_saveButtonActionPerformed
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
            browserPathField.setText( file.getAbsolutePath());
        }
    }//GEN-LAST:event_browserPathButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel appConfigPanel;
    private javax.swing.JButton browserPathButton;
    private javax.swing.JTextField browserPathField;
    private javax.swing.JLabel browserPathLabel;
    private org.hisp.dhis.config.ConfigType config;
    private javax.swing.JTextField countryField;
    private javax.swing.JLabel countryLabel;
    private javax.swing.JPanel databaseConfigPanel;
    private javax.swing.JTextField hostField;
    private javax.swing.JLabel hostLabel;
    private javax.swing.JTextField langField;
    private javax.swing.JLabel langLabel;
    private javax.swing.JTextField portField;
    private javax.swing.JLabel portLabel;
    private javax.swing.JButton saveButton;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
