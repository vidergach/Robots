package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Визуализатор игрового поля
 */
public class GameVisualizer extends JPanel implements PropertyChangeListener {
    private RobotModel robotModel;
    private GameController controller;

    public GameVisualizer(RobotModel robotModel, GameController controller) {
        this.robotModel = robotModel;
        this.controller = controller;

        //подписываемся на изменения модели
        robotModel.addPropertyChangeListener(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.handleMouseClick(e.getPoint());
            }
        });
        setDoubleBuffered(true);
    }

    /**
     * Реакция на изменения модели
     */
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(this::repaint);
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d,
                round(robotModel.getRobotPositionX()),
                round(robotModel.getRobotPositionY()),
                robotModel.getRobotDirection());
        drawTarget(g2d,
                robotModel.getTargetPositionX(),
                robotModel.getTargetPositionY());
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x + 10, y, 5, 5);
        g.setTransform(new AffineTransform());
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setTransform(new AffineTransform());
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}