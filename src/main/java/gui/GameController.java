package gui;

import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Контроллер игрового поля
 * Обрабатывает пользовательский ввод и управляет моделью
 */
public class GameController {
    private GameModel gameModel;
    private final Timer updateTimer;

    public GameController(GameModel gameModel) {
        this.gameModel = gameModel;
        this.updateTimer = new Timer("game-controller", true);
        startMovementTimer();
    }

    /**
     * Конструктор - запускает таймер обновления
     */
    private void startMovementTimer() {
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gameModel.updateRobotPosition();
            }
        }, 0, 30);
    }

    /**
     * Обрабатывает клик по игровому полю
     */
    public void handleMouseClick(Point point) {
        if (point != null) {
            gameModel.setTargetPosition(point.x, point.y);
        }
    }
}