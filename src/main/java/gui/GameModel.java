package gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.Color;

/**
 * Модель описывающая движение робота к цели
 */
public class GameModel implements RobotBehavior {
    public static final String ROBOT_POSITION_UPDATED = "ROBOT_POSITION_UPDATED";
    public static final String TARGET_POSITION_UPDATED = "TARGET_POSITION_UPDATED";

    private final PropertyChangeSupport propChangeDispatcher =
            new PropertyChangeSupport(this);//могут подписаться на изм модельки

    private RobotBehavior robot;

    /**
     * Создает робота по умолчанию
     */
    public GameModel() {
        this.robot = new RobotModelDefault();
    }

    /**
     * Двигает робота
     */
    public void updateRobotPosition() {
        robot.updateRobotPosition();
        propChangeDispatcher.firePropertyChange(ROBOT_POSITION_UPDATED, null, null);
    }

    @Override
    public double getX() {
        return robot.getX();
    }

    @Override
    public double getY() {
        return robot.getY();
    }

    @Override
    public double getTargetX() {
        return robot.getTargetX();
    }

    @Override
    public double getTargetY() {
        return robot.getTargetY();
    }

    @Override
    public double getDirection() {
        return robot.getDirection();
    }

    @Override
    public void setTargetPosition(int x, int y) {
        robot.setTargetPosition(x, y);
        propChangeDispatcher.firePropertyChange(TARGET_POSITION_UPDATED, null, null);
    }

    @Override
    public double getAngleToTarget() {
        return robot.getAngleToTarget();
    }

    @Override
    public double getAngleDifference() {
        return robot.getAngleDifference();
    }

    @Override
    public Color getColor() {
        return robot.getColor();
    }

    public int getRobotX() {
        return (int) robot.getX();
    }

    public int getRobotY() {
        return (int) robot.getY();
    }

    /**
     * Подписывает объект на уведомления об изменениях
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propChangeDispatcher.addPropertyChangeListener(listener);
    }

    /**
     * Заменяет текущего робота на нового
     */
    public void setRobotModel(RobotBehavior robotModel) {
        RobotBehavior oldRobot = this.robot;
        this.robot = robotModel;
        propChangeDispatcher.firePropertyChange(ROBOT_POSITION_UPDATED, oldRobot, robotModel);
    }
}