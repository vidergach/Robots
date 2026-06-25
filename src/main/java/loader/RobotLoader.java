package loader;

import gui.RobotBehavior;
import gui.RobotModelDefault;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;

/**
 * Класс для загрузки из jar-файла
 */
public class RobotLoader {

    /**
     * Метод для загрузки нового робота
     * если не получилось, вовращаем робота по умолчанию
     */
    public static RobotBehavior getNewRobotOrDefault(RobotBehavior defaultRobot, Component parent) {
        try {
            Optional<File> optionalJarFile = chooseFileToLoad(parent);//выбор файла - либо пустой
            if (optionalJarFile.isEmpty()) return defaultRobot;

            File jarFile = optionalJarFile.get();//извлекаем выбранный jar фаил
            if (!jarFile.getName().toLowerCase().endsWith(".jar")) {
                Logger.debug("Выбранный файл не является .jar");
                return defaultRobot;
            }

            URL jarUrl = jarFile.toURI().toURL();

            try (URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{jarUrl},//массив url с jar-файлом
                    RobotBehavior.class.getClassLoader())) {

                //ищем класс
                Class<?> clazz = classLoader.loadClass("test_robot.Robot2");

                //реализует ли интерфейс
                if (RobotBehavior.class.isAssignableFrom(clazz)) {
                    Logger.debug("Робот успешно загружен из " + jarFile.getName());
                    return (RobotBehavior) clazz.getDeclaredConstructor().newInstance();
                } else {
                    Logger.debug("Класс не реализует RobotBehavior");
                }
            }
        } catch (ClassNotFoundException e) {
            Logger.debug("Класс test_robot.Robot2 не найден в jar-архиве");
            e.printStackTrace();
        } catch (Exception e) {
            Logger.error("Ошибка при загрузке робота: " + e.getMessage());
            e.printStackTrace();
        }
        return defaultRobot;
    }

    /**
     * Метод для отображения диалога выбора файла
     */
    private static Optional<File> chooseFileToLoad(Component parent) {
        JFileChooser fileChooser = new JFileChooser();//обьект открыть фаил
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JAR files", "jar"));

        int returnValue = fileChooser.showOpenDialog(parent);//выбор
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            //возвращаем выбранный файл в Optional
            return Optional.ofNullable(fileChooser.getSelectedFile());
        }
        return Optional.empty();
    }
}