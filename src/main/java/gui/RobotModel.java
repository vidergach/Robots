package gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;

/**
 * модель движения робота
 */
public class RobotModel {
    private final Timer timer;

    //уведомления для слушателей
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private volatile double robotPositionX = 100;//x-координата
    private volatile double robotPositionY = 100;//y-координата
    private volatile double robotDirection = 0;//направление робота в радианах

    private volatile int targetPositionX = 150;//х-цель
    private volatile int targetPositionY = 100;//у-цель

    private static final double MAX_VELOCITY = 0.1;//макс скорость
    private static final double MAX_ANGULAR_VELOCITY = 0.01;//макс угловая скорость

    public RobotModel() {
        timer = new Timer("robot-model", true);
        timer.schedule(new TimerTask() {
            public void run() {
                onModelUpdateEvent();
            }
        }, 0, 10);
    }

    /**
     * Нормализует угол в диапазон (-π, π]
     */
    private double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle <= -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    /**
     * выполняет шаг обновления робота
     */
    protected void onModelUpdateEvent() {
        double distance = distance(targetPositionX, targetPositionY,
                robotPositionX, robotPositionY);
        if (distance < 0.5) {
            return;
        }
        double velocity = MAX_VELOCITY;
        double angleToTarget = angleTo(robotPositionX, robotPositionY,
                targetPositionX, targetPositionY);

        double angleDiff = normalizeAngle(angleToTarget - robotDirection);
        double angularVelocity = 0;
        if (angleDiff > 1e-6) {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        } else if (angleDiff < -1e-6) {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    /**
     *Вычисляет угол между направлением от точки (fromX, fromY) к точке (toX, toY)
     */
    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    /**
     * обновляет позицию робота на основе заданных скоростей
     */
    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double oldX = robotPositionX;
        double oldY = robotPositionY;
        double oldDirection = robotDirection;

        double newX, newY;
        if (Math.abs(angularVelocity) < 1e-8) {
            // прямолинейное движение
            newX = oldX + velocity * duration * Math.cos(oldDirection);
            newY = oldY + velocity * duration * Math.sin(oldDirection);
        } else {
            // движение по дуге окружности
            double radius = velocity / angularVelocity;
            double deltaAngle = angularVelocity * duration;

            newX = oldX + radius * (Math.sin(oldDirection + deltaAngle) - Math.sin(oldDirection));
            newY = oldY - radius * (Math.cos(oldDirection + deltaAngle) - Math.cos(oldDirection));
        }

        double newDirection = asNormalizedRadians(oldDirection + angularVelocity * duration);

        setRobotPositionX(newX);//обновляем состояние с уведомлением слушателей
        setRobotPositionY(newY);
        setRobotDirection(newDirection);
    }

    /**
     * пределы
     */
    private static double applyLimits(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * нормализует угол в диапазон [0, 2π)
     */
    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    /**
     * возвращает текущую X-координату робота
     */
    public double getRobotPositionX() {
        return robotPositionX;
    }

    /**
     * устанавливает X-координату робота и уведомляет слушателей
     */
    public void setRobotPositionX(double robotPositionX) {
        double oldValue = this.robotPositionX;
        this.robotPositionX = robotPositionX;
        propertyChangeSupport.firePropertyChange("positionX", oldValue, robotPositionX);
    }

    /**
     * возвращает текущую Y-координату робота
     */
    public double getRobotPositionY() {
        return robotPositionY;
    }

    /**
    * устанавливает Y-координату робота и уведомляет слушателей
     */
    public void setRobotPositionY(double robotPositionY) {
        double oldValue = this.robotPositionY;
        this.robotPositionY = robotPositionY;
        propertyChangeSupport.firePropertyChange("positionY", oldValue, robotPositionY);
    }

    /**
     * возвращает текущее направление робота
     */
    public double getRobotDirection() {
        return robotDirection;
    }

    /**
     * устанавливает направление робота и уведомляет слушателей
     */
    public void setRobotDirection(double robotDirection) {
        double oldValue = this.robotDirection;
        this.robotDirection = robotDirection;
        propertyChangeSupport.firePropertyChange("direction", oldValue, robotDirection);
    }

    /**
     * возвращает X-координату целевой точки
     */
    public int getTargetPositionX() {
        return targetPositionX;
    }

    /**
     * устанавливает X-координату цели и уведомляет слушателей
     */
    public void setTargetPositionX(int targetPositionX) {
        int oldValue = this.targetPositionX;
        this.targetPositionX = targetPositionX;
        propertyChangeSupport.firePropertyChange("targetX", oldValue, targetPositionX);
    }

    /**
     * возвращает Y-координату целевой точки
     */
    public int getTargetPositionY() {
        return targetPositionY;
    }

    /**
     * устанавливает Y-координату цели и уведомляет слушателей
     */
    public void setTargetPositionY(int targetPositionY) {
        int oldValue = this.targetPositionY;
        this.targetPositionY = targetPositionY;
        propertyChangeSupport.firePropertyChange("targetY", oldValue, targetPositionY);
    }

    /**
     * устанавливает целевую точку по координатам и уведомляет слушателей
     */
    public void setTargetPosition(int x, int y) {
        setTargetPositionX(x);
        setTargetPositionY(y);
    }

    /**
     * добавляет слушателя для получения уведомлений об изменениях свойств модели
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}