package gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameWindow extends JInternalFrame implements StateSaveAndRestore {
    private GameVisualizer gameVisualizer;
    private String PREFIX = "game";

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        gameVisualizer = new GameVisualizer();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
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