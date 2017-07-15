import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import java.awt.*;

/**
 * Created by marijnzwemmer on 15/07/17.
 */
public class SettingsForm {
    private JPanel panel1;
    public JTextArea textArea;
    private JLabel infoLabel;
    private JCheckBox seperateLinesCheckBox;
    private JCheckBox emptyLineCheckBox;
    private JTextField prefixTextField;
    private JTextField postfixTextField;

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel1.add(panel2, BorderLayout.CENTER);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setInheritsPopupMenu(true);
        panel3.setName("");
        panel3.setPreferredSize(new Dimension(500, 381));
        panel3.setRequestFocusEnabled(false);
        panel2.add(panel3, BorderLayout.CENTER);
        infoLabel = new JLabel();
        infoLabel.setAutoscrolls(false);
        infoLabel.setText("Here you can configure how the injection code generation should look like");
        panel3.add(infoLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        seperateLinesCheckBox = new JCheckBox();
        seperateLinesCheckBox.setText("Attribute and field declaration on seperate lines");
        panel3.add(seperateLinesCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        emptyLineCheckBox = new JCheckBox();
        emptyLineCheckBox.setText("Add an empty line after every injection");
        panel3.add(emptyLineCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel3.add(panel4, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setMaximumSize(new Dimension(100, 16));
        label1.setMinimumSize(new Dimension(50, 16));
        label1.setPreferredSize(new Dimension(140, 16));
        label1.setRequestFocusEnabled(false);
        label1.setText("Declaration prefix");
        label1.setVisible(true);
        panel4.add(label1);
        prefixTextField = new JTextField();
        prefixTextField.setMinimumSize(new Dimension(150, 24));
        prefixTextField.setPreferredSize(new Dimension(150, 24));
        panel4.add(prefixTextField);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel3.add(panel5, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setMaximumSize(new Dimension(100, 16));
        label2.setMinimumSize(new Dimension(50, 16));
        label2.setPreferredSize(new Dimension(140, 16));
        label2.setRequestFocusEnabled(false);
        label2.setText("Declaration postfix");
        label2.setVisible(true);
        panel5.add(label2);
        postfixTextField = new JTextField();
        postfixTextField.setMinimumSize(new Dimension(150, 24));
        postfixTextField.setPreferredSize(new Dimension(150, 24));
        panel5.add(postfixTextField);
        textArea = new JTextArea();
        textArea.setRows(12);
        textArea.setTabSize(4);
        textArea.setText("");
        panel3.add(textArea, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        panel3.add(separator1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
