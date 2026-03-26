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
    private JLabel position, direction, target, distance;

    public RobotInfoWindow(RobotModel model) {
        super("Информация о роботе", true, true, true, true);
        this.model = model;
        model.addPropertyChangeListener(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(new JLabel("Координаты:"));
        panel.add(position = new JLabel("--"));
        panel.add(new JLabel("Направление:"));
        panel.add(direction = new JLabel("--"));
        panel.add(new JLabel("Цель:"));
        panel.add(target = new JLabel("--"));
        panel.add(new JLabel("Расстояние:"));
        panel.add(distance = new JLabel("--"));

        setContentPane(panel);
        setSize(250, 150);
        updateInfo();
    }

    private void updateInfo() {
        if (model == null) return;

        double x = model.getRobotPositionX();
        double y = model.getRobotPositionY();

        position.setText(String.format("(%.0f, %.0f)", x, y));
        direction.setText(String.format("%.0f°", Math.toDegrees(model.getRobotDirection())));
        target.setText(String.format("(%d, %d)", model.getTargetPositionX(), model.getTargetPositionY()));

        double dist = Math.hypot(x - model.getTargetPositionX(), y - model.getTargetPositionY());
        distance.setText(String.format("%.0f пикс", dist));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        EventQueue.invokeLater(this::updateInfo);
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