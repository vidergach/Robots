package gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

public class RobotInfoWindow extends JInternalFrame implements StateSaveAndRestore, PropertyChangeListener {
    private static final String PREFIX = "robotInfo";
    private RobotModel model;
    private final JLabel lblPosition = new JLabel("--");
    private final JLabel lblDirection = new JLabel("--");
    private final JLabel lblTarget = new JLabel("--");
    private final JLabel lblAngleToTarget = new JLabel("--");
    private final JLabel lblAngleDiff = new JLabel("--");


    public RobotInfoWindow(RobotModel model) {
        super("Информация о роботе", true, true, true, true);
        this.model = model;
        model.addPropertyChangeListener(this);
        initUI();
        updateInfo();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(new JLabel("Координаты:"));
        panel.add(lblPosition);
        panel.add(new JLabel("Направление:"));
        panel.add(lblDirection);
        panel.add(new JLabel("Цель:"));
        panel.add(lblTarget);
        panel.add(new JLabel("Угол до цели (рад):"));
        panel.add(lblAngleToTarget);
        panel.add(new JLabel("Угол поворота (рад):"));
        panel.add(lblAngleDiff);

        setContentPane(panel);
        setSize(250, 200);
    }

    private void updateInfo() {
        if (model == null) return;

        lblPosition.setText(String.format("(%.1f, %.1f)",
                model.getRobotPositionX(), model.getRobotPositionY()));
        lblDirection.setText(String.format("%.1f°",
                Math.toDegrees(model.getRobotDirection())));
        lblTarget.setText(String.format("(%d, %d)",
                model.getTargetPositionX(), model.getTargetPositionY()));
        lblAngleToTarget.setText(String.format("%.3f", model.getAngleToTarget()));
        lblAngleDiff.setText(String.format("%.3f", model.getAngleDifference()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(this::updateInfo);
    }


    @Override
    public Map<String, String> saveState() {
        Map<String, String> state = new HashMap<>();
        state.put("x", String.valueOf(getX()));
        state.put("y", String.valueOf(getY()));
        state.put("w", String.valueOf(getWidth()));
        state.put("h", String.valueOf(getHeight()));
        state.put("icon", String.valueOf(isIcon()));
        return state;
    }

    @Override
    public void restoreState(Map<String, String> state) {
        try {
            setLocation(Integer.parseInt(state.getOrDefault("x", "0")),
                    Integer.parseInt(state.getOrDefault("y", "0")));
            setSize(Integer.parseInt(state.getOrDefault("w", "250")),
                    Integer.parseInt(state.getOrDefault("h", "150")));
            setIcon(Boolean.parseBoolean(state.getOrDefault("icon", "false")));
        } catch (NumberFormatException | java.beans.PropertyVetoException ignored) {}
    }

    @Override
    public String getPrefix() { return PREFIX; }

}