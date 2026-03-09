package gui;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LogWindow extends JInternalFrame implements LogChangeListener, StateSaveAndRestore {
    private LogWindowSource logSource;
    private TextArea logContent;
    private static final String PREFIX = "log";

    public LogWindow(LogWindowSource logSource) {
        super("Протокол работы", true, true, true, true);
        this.logSource = logSource;
        this.logSource.registerListener(this);
        this.logContent = new TextArea("");
        this.logContent.setSize(200, 500);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateLogContent();
    }

    private void updateLogContent() {
        StringBuilder content = new StringBuilder();
        for (LogEntry entry : logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        logContent.setText(content.toString());
        logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
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