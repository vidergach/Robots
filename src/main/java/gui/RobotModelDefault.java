package gui;

import log.Logger;
import java.awt.Color;

/**
 * Модель робота по умолчанию
 */
public class RobotModelDefault implements RobotBehavior {
    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100;
    private volatile double robotDirection = 0;
    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;
    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.01;
    private final Color robotColor = Color.PINK;

    /**
     * Нормализует угол в диапазон (-π, π]
     */
    private double normalizeAngle(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle <= -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    @Override
    public void updateRobotPosition() {
        double distance = distance(targetPositionX, targetPositionY, robotPositionX, robotPositionY);
        if (distance < 0.5) return;
        double velocity = MAX_VELOCITY;
        double angleToTarget = angleTo(robotPositionX, robotPositionY, targetPositionX, targetPositionY);
        double angularVelocity = calculateVelocity(angleToTarget, robotDirection);
        moveRobot(velocity, angularVelocity, 10);
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

    @Override
    public double getX() { return robotPositionX; }

    @Override
    public double getY() { return robotPositionY; }

    @Override
    public double getTargetX() { return targetPositionX; }

    @Override
    public double getTargetY() { return targetPositionY; }

    @Override
    public double getDirection() { return robotDirection; }

    @Override
    public void setTargetPosition(int x, int y) {
        this.targetPositionX = x;
        this.targetPositionY = y;
        logCurrentTargetPosition();
    }

    @Override
    public Color getColor() { return robotColor; }

    @Override
    public double getAngleToTarget() {
        return angleTo(robotPositionX, robotPositionY, targetPositionX, targetPositionY);
    }

    @Override
    public double getAngleDifference() {
        return normalizeAngle(getAngleToTarget() - robotDirection);
    }

    /**
     * лог текущих координат цели
     */
    private void logCurrentTargetPosition() {
        LocaleManager localeManager = LocaleManager.getInstance();
        String pattern = localeManager.getString("log.target.coordinates");
        String message = String.format(pattern, targetPositionX, targetPositionY);
        Logger.debug(message);
    }
}