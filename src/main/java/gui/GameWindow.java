package gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Окно игрового поля
 */
public class GameWindow extends JInternalFrame implements StateSaveAndRestore {
    private GameVisualizer gameVisualizer;
    private RobotModel robotModel;
    private String PREFIX = "game";
    private LocaleManager localeManager;


    /**
     * Конструктор окна игрового поля
     */
    public GameWindow(RobotModel robotModel) {
        super();
        localeManager = LocaleManager.getInstance();
        this.robotModel = robotModel;
        GameVisualizer visualizer = new GameVisualizer(robotModel);
        GameController controller = new GameController(robotModel);

        visualizer.addPropertyChangeListener(evt -> {
            if ("mouseClick".equals(evt.getPropertyName())) {
                controller.handleMouseClick((Point) evt.getNewValue());
            }
        });

        this.gameVisualizer = visualizer;

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        updateUITexts();
        pack();
    }

    @Override
    public void updateUITexts() {
        setTitle(localeManager.getString("window.game.title"));
    }

    @Override
    public Map<String, String> saveState() {
        Map<String, String> state = new HashMap<>();
        state.put("x", String.valueOf(getX()));
        state.put("y", String.valueOf(getY()));
        state.put("width", String.valueOf(getWidth()));
        state.put("height", String.valueOf(getHeight()));
        state.put("icon", String.valueOf(isIcon()));
        return state;
    }

    @Override
    public void restoreState(Map<String, String> state) {
        try {
            int x = Integer.parseInt(state.getOrDefault("x", String.valueOf(getX())));
            int y = Integer.parseInt(state.getOrDefault("y", String.valueOf(getY())));
            int width = Integer.parseInt(state.getOrDefault("width", String.valueOf(getWidth())));
            int height = Integer.parseInt(state.getOrDefault("height", String.valueOf(getHeight())));
            boolean icon = Boolean.parseBoolean(state.getOrDefault("isIcon", "false"));

            setLocation(x, y);
            setSize(width, height);
            try {
                setIcon(icon);
            } catch (java.beans.PropertyVetoException e) {
                // ignore
            }
        } catch (NumberFormatException e) {
            // use defaults
        }
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }
}