package gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * модель движения робота
 */
public class RobotModel {
    //уведомления для слушателей
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private volatile double robotPositionX = 100;//x-координата
    private volatile double robotPositionY = 100;//y-координата
    private volatile double robotDirection = 0;//направление робота в радианах

    private volatile int targetPositionX = 150;//х-цель
    private volatile int targetPositionY = 100;//у-цель

    private static final double MAX_VELOCITY = 0.1;//макс скорость
    private static final double MAX_ANGULAR_VELOCITY = 0.01;//макс угловая скорость

    /**
     * добавляет слушателя для получения уведомлений об изменениях свойств модели
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * возвращает текущую X-координату робота
     */
    public double getRobotPositionX() {
        return robotPositionX;
    }

    /**
     * возвращает текущую Y-координату робота
     */
    public double getRobotPositionY() {
        return robotPositionY;
    }

    /**
     * возвращает текущее направление робота
     */
    public double getRobotDirection() {
        return robotDirection;
    }

    /**
     * возвращает X-координату целевой точки
     */
    public int getTargetPositionX() {
        return targetPositionX;
    }

    /**
     * возвращает Y-координату целевой точки
     */
    public int getTargetPositionY() {
        return targetPositionY;
    }

    /**
     * устанавливает новую целевую точку
     */
    public void setTargetPosition(int x, int y) {
        this.targetPositionX = x;
        this.targetPositionY = y;
        propertyChangeSupport.firePropertyChange("target", null, null);
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
        double oldX = robotPositionX;
        double oldY = robotPositionY;
        double oldDir = robotDirection;

        double distance = distance(targetPositionX, targetPositionY,
                robotPositionX, robotPositionY);

        if (distance < 0.5) {
            return;
        }
        double velocity = MAX_VELOCITY;
        //вычисляем угол относительно текущей позиции
        double angleToTarget = angleTo(robotPositionX, robotPositionY,
                targetPositionX, targetPositionY);

        //вычисляем угловую скорости
        double angularVelocity = calculateVelocity(angleToTarget, robotDirection);

        moveRobot(velocity, angularVelocity, 10);

        //уведомления о перерисовке
        if (oldX != robotPositionX || oldY != robotPositionY) {
            propertyChangeSupport.firePropertyChange("position", null, null);
        }
        if (oldDir != robotDirection) {
            propertyChangeSupport.firePropertyChange("direction", null, null);
        }
    }

    /**
     * вычисляет угловую скорость с учетом кратчайшего пути поворота
     */
    private double calculateVelocity(double targetAngle, double currentAngle) {

        double angleDiff = normalizeAngle(targetAngle - currentAngle);

        if (Math.abs(angleDiff) < 0.01) {
            return 0.0;
        }
        //определяем направление поворота
        if (angleDiff > 0) {
            return MAX_ANGULAR_VELOCITY; //против часовой
        } else {
            return -MAX_ANGULAR_VELOCITY; //по часовой
        }
    }

    /**
     * вычисляет расстояние между точками
     */
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
     * перемещение робота с линейной и угловой скоростями в течение определенного времени
     */
    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, MAX_VELOCITY);
        angularVelocity = applyLimits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX, newY;
        double newDirection = robotDirection + angularVelocity * duration;

        if (Math.abs(angularVelocity) < 1e-10) {
            // Прямолинейное движение
            newX = robotPositionX + velocity * duration * Math.cos(robotDirection);
            newY = robotPositionY + velocity * duration * Math.sin(robotDirection);
        } else {
            // Криволинейное движение
            double radius = velocity / angularVelocity;
            newX = robotPositionX + radius * (Math.sin(newDirection) - Math.sin(robotDirection));
            newY = robotPositionY - radius * (Math.cos(newDirection) - Math.cos(robotDirection));
        }
        robotPositionX = newX;
        robotPositionY = newY;
        robotDirection = asNormalizedRadians(newDirection);
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
     * возвращает угол до цели в радианах
     */
    public double getAngleToTarget() {
        return angleTo(robotPositionX, robotPositionY, targetPositionX, targetPositionY);
    }

    /**
     * возвращает угол поворота до цели в радианах
     */
    public double getAngleDifference() {
        return normalizeAngle(getAngleToTarget() - robotDirection);
    }
}