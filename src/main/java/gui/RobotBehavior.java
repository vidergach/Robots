package gui;

import java.awt.Color;

/**
 * Интерфейс модели поведения робота
 */
public interface RobotBehavior {
    /**
     * Обновление позиции робота
     */
    void updateRobotPosition();

    /**
     * Возвращает x координату робота
     */
    double getX();

    /**
     * Возвращает y координату робота
     */
    double getY();

    /**
     * x-координата целевой точки
     */
    double getTargetX();

    /**
     * y-координата целевой точки
     */
    double getTargetY();

    /**
     * Возвращает направление робота в радианах
     */
    double getDirection();

    /**
     * Устанавливает целевую точку для движения робота
     */
    void setTargetPosition(int x, int y);

    /**
     * Возвращает угол до цели в радианах
     */
    double getAngleToTarget();

    /**
     * Возвращает разницу углов в радианах
     */
    double getAngleDifference();

    /**
     * Цвет робота
     */
    Color getColor();
}