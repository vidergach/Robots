package gui;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Окно проецирования движения робота
 */
public class RobotInfoWindow extends JInternalFrame implements StateSaveAndRestore, PropertyChangeListener {
    private static final String PREFIX = "robotInfo";
    private RobotModel model;
    private final JLabel lblPosition = new JLabel("--");
    private final JLabel lblDirection = new JLabel("--");
    private final JLabel lblTarget = new JLabel("--");
    private final JLabel lblAngleToTarget = new JLabel("--");
    private final JLabel lblAngleDiff = new JLabel("--");
    private final JLabel labelPosition = new JLabel();
    private final JLabel labelDirection = new JLabel();
    private final JLabel labelTarget = new JLabel();
    private final JLabel labelAngleToTarget = new JLabel();
    private final JLabel labelAngleDiff = new JLabel();
    private LocaleManager localeManager;

    /**
     *  Конструктор окна информации о роботе
     */
    public RobotInfoWindow(RobotModel model) {
        super();
        localeManager = LocaleManager.getInstance();
        setTitle(localeManager.getString("window.robotInfo.title"));
        this.model = model;
        model.addPropertyChangeListener(this);
        initUI();
        updateUITexts();
        updateInfo();
    }

    /**
     * Инициализирует пользовательский интерфейс окна
     * координаты, направление, цель,
     * угол до цели(рад), угол поворота(рад)
     */
    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(labelPosition);
        panel.add(lblPosition);
        panel.add(labelDirection);
        panel.add(lblDirection);
        panel.add(labelTarget);
        panel.add(lblTarget);
        panel.add(labelAngleToTarget);
        panel.add(lblAngleToTarget);
        panel.add(labelAngleDiff);
        panel.add(lblAngleDiff);

        setContentPane(panel);
        setSize(250, 200);
    }

    /**
     * Обновляет тексты меток при смене языка
     */
    public void updateUITexts() {
        labelPosition.setText(localeManager.getString("robot.info.position"));
        labelDirection.setText(localeManager.getString("robot.info.direction"));
        labelTarget.setText(localeManager.getString("robot.info.target"));
        labelAngleToTarget.setText(localeManager.getString("robot.info.angle.to.target"));
        labelAngleDiff.setText(localeManager.getString("robot.info.angle.diff"));
        updateInfo();
    }

    /**
     * Обновляет отображаемую информацию о роботе
     */
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