package com.jeyaramj;

import com.jeyaramj.utils.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class GUI {

    JFrame frame;
    private JList<CheckboxListItem> list;

    public GUI() {

        frame = new JFrame();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public void createJList(CheckboxListItem[] items) {

        list = new JList<CheckboxListItem>(items);
        list.setCellRenderer(new CheckboxListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                JList<CheckboxListItem> list = (JList<CheckboxListItem>) event.getSource();
                int index = list.locationToIndex(event.getPoint());
                CheckboxListItem item = list.getModel()
                        .getElementAt(index);

                item.setSelected(!item.isSelected());

                list.repaint(list.getCellBounds(index, index));
                String fileName = item.toString().substring(item.toString().lastIndexOf(" | ") + 3);
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to Delete the file ?" + fileName, "Warning", dialogButton);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    System.out.println("Deleting.. " + fileName);
                    File dFile = new File(fileName);
                    try {
                        if (Util.deleteFile(dFile)) {
                            System.out.println("File " + fileName + " is successfully deleted.");
                        } else {
                            System.out.println("Deleting file " + fileName + " is failed.");
                        }
                    } catch (SecurityException se) {
                        System.out.println("Deleting file " + fileName + " is failed:" + se.getMessage());
                    }
                }

            }
        });
        frame.getContentPane().removeAll();

        frame.getContentPane().add(new JScrollPane(list));
        frame.pack();
        frame.setVisible(true);
    }

    public void addItem(String item) {
        DefaultListModel<CheckboxListItem> aModel = new DefaultListModel<CheckboxListItem>();
        for (int i = 0; i < list.getModel().getSize(); i++) {
            aModel.addElement(list.getModel().getElementAt(i));
        }
        aModel.addElement(new CheckboxListItem(item));
        list.setModel(aModel);
    }

    public void addItems(CheckboxListItem[] items) {
        createJList(items);
    }

}

class CheckboxListItem {
    private String label;
    private boolean isSelected = false;

    public CheckboxListItem(String label) {
        this.label = label;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String toString() {
        return label;
    }
}

class CheckboxListRenderer extends JCheckBox implements
        ListCellRenderer<CheckboxListItem> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(
            JList<? extends CheckboxListItem> list, CheckboxListItem value,
            int index, boolean isSelected, boolean cellHasFocus) {
        //System.out.println(value.toString());
        if (value.toString() == "-") {
            return new JSeparator(JSeparator.HORIZONTAL);
        } else {

            setEnabled(list.isEnabled());
            setSelected(value.isSelected());
            setFont(list.getFont());
            setBackground(list.getBackground());
            if (value.toString().startsWith("[DEL]"))
                setForeground(Color.RED);
            else
                setForeground(list.getForeground());
            setText(value.toString().replace("[DEL]", ""));
            return this;
        }
    }
}