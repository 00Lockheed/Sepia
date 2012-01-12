package edu.cwru.SimpleRTS.start;

import java.awt.Color;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.cwru.SimpleRTS.start.StartWindow;

@SuppressWarnings("serial")
public class AgentPanel extends JPanel {

    public AgentPanel() {
        super(new GridBagLayout());
        TitledBorder border = new TitledBorder("Agents");
        border.setBorder(new LineBorder(Color.BLACK, 2));
        border.setTitleFont(border.getTitleFont().deriveFont(StartWindow.TITLE_FONT));
        this.setBorder(border);

        JLabel agentTable = new JLabel("TODO: AGENT TABLE");
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        add(agentTable, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        add(Box.createHorizontalGlue(), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        add(addButton, gbc);

        gbc.gridx = 2;
        add(deleteButton, gbc);

    }

}
