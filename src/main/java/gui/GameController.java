package gui;

import java.awt.Point;

/**
 * Контроллер игрового поля
 * Обрабатывает пользовательский ввод и управляет моделью
 */
public class GameController {
    private RobotModel robotModel;

    public GameController(RobotModel robotModel) {
        this.robotModel = robotModel;
    }

    /**
     * Обрабатывает клик по игровому полю
     */
    public void handleMouseClick(Point point) {
        if (point != null) {
            robotModel.setTargetPosition(point.x, point.y);
        }
    }
}