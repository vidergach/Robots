package gui;

import log.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Главное окно приложения, которое содержит все внутренние окна
 */
public class MainApplicationFrame extends JFrame implements StateSaveAndRestore {
    private JDesktopPane desktopPane = new JDesktopPane();
    private String prefix = "main";
    private StateFileManager stateManager;//для сохранения и загрузки состояния в файл

    private StateMap appState;
    List<StateSaveAndRestore> windows = new ArrayList<>();//список окон, которые надо сохранять
    /**
     * Конструктор главного окна приложения.
     */
    public MainApplicationFrame() {
        super("Главное окно приложения");
        stateManager = new StateFileManager("Dergach");
        initComponents();
        restoreState();
    }

    /**
     * Инициализирует все компоненты главного окна:
     * устанавливает размеры, создает меню, добавляет внутренние окна.
     */
    private void initComponents() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);

        setContentPane(desktopPane);

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);
        windows.add(logWindow);

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);
        windows.add(gameWindow);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showExitConfirmation();
            }
        });
    }

    /**
     * Показывает диалог подтверждения выхода из приложения
     */
    private void showExitConfirmation() {
        String[] options = {"Да", "Нет"};
        int result = JOptionPane.showOptionDialog(
                this, "Вы действительно хотите выйти?", "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);

        if (result == JOptionPane.OK_OPTION) {
            saveAllStates();//сохраняем состояния
            dispose();
            System.exit(0);
        }
    }

    /**
     * Сохраняет состояние всех окон в файл
     */
    private void saveAllStates() {
        appState = new StateMap();

        // Сохраняем главное окно
        appState.put(prefix, saveState());
        // Сохраняем все окна из списка
        for (StateSaveAndRestore window : windows) {
            appState.put(window.getPrefix(), window.saveState());
        }
        stateManager.save(appState);
    }


    /**
     * Восстанавливает состояние всех окон из файла
     */
    private void restoreState() {
        appState = stateManager.load();

        // Восстанавливаем главное окно
        if (appState.containsPrefix(prefix)) {
            restoreState(appState.get(prefix));
        }

        // Восстанавливаем все окна из списка
        for (StateSaveAndRestore window : windows) {
            if (appState.containsPrefix(window.getPrefix())) {
                window.restoreState(appState.get(window.getPrefix()));
            }
        }
    }

    /**
     * Сохраняет состояние главного окна в словарь
     */
    @Override
    public Map<String, String> saveState() {
        Map<String, String> state = new HashMap<>();//словарь для состояния
        state.put("x", String.valueOf(getX()));
        state.put("y", String.valueOf(getY()));
        state.put("width", String.valueOf(getWidth()));
        state.put("height", String.valueOf(getHeight()));
        state.put("extendedState", String.valueOf(getExtendedState()));
        return state;//заполненный словарь
    }

    /**
     * Восстанавливает состояние главного окна из словаря
     */
    @Override
    public void restoreState(Map<String, String> state) {
        try {
            if (state.containsKey("x") && state.containsKey("y")) {
                setLocation(Integer.parseInt(state.get("x")), Integer.parseInt(state.get("y")));
            }//строка в число
            if (state.containsKey("width") && state.containsKey("height")) {
                setSize(Integer.parseInt(state.get("width")), Integer.parseInt(state.get("height")));
            }
            if (state.containsKey("extendedState")) {
                setExtendedState(Integer.parseInt(state.get("extendedState")));
            }
        } catch (NumberFormatException ignored) {}
    }

    /**
     * Возвращает префикс для сохранения состояния главного окна
     */
    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * Создает окно протокола работы
     */
    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    /**
     * Добавляет внутреннее окно на рабочий стол и делает его видимым.
     */
    protected void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    /**
     * Создает строку меню приложения
     */
    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        menuBar.add(createExitMenu());
        return menuBar;
    }

    /**
     * Создает меню выхода из приложения
     */
    private JMenu createExitMenu() {
        JMenu exitMenu = new JMenu("Выход");
        exitMenu.setMnemonic(KeyEvent.VK_X);
        exitMenu.add(createExitMenuItem());
        return exitMenu;
    }

    /**
     * Создает пункт меню для выхода из приложения
     */
    private JMenuItem createExitMenuItem() {
        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitItem.addActionListener(e -> {
            WindowEvent we = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(we);
        });
        return exitItem;
    }

    /**
     * Создает меню выбора режима отображения
     */
    private JMenu createLookAndFeelMenu() {
        JMenu lafMenu = new JMenu("Режим отображения");
        lafMenu.setMnemonic(KeyEvent.VK_V);
        lafMenu.add(createSystemLook());
        lafMenu.add(createCrossPlatformLook());
        return lafMenu;
    }

    /**
     * Создает пункт меню для "Системная схема"
     */
    private JMenuItem createSystemLook() {
        JMenuItem item = new JMenuItem("Системная схема", KeyEvent.VK_S);
        item.addActionListener(e -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        return item;
    }

    /**
     * Создает пункт меню для "Универсальная схема"
     */
    private JMenuItem createCrossPlatformLook() {
        JMenuItem item = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        item.addActionListener(e -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        return item;
    }

    /**
     * Создает меню с тестовыми командами
     */
    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.add(createAddLogMessageItem());
        return testMenu;
    }

    /**
     * Создает пункт меню для добавления тестового сообщения в лог
     */
    private JMenuItem createAddLogMessageItem() {
        JMenuItem item = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        item.addActionListener(e -> Logger.debug("Новая строка"));
        return item;
    }

    /**
     * Устанавливает указанную схему оформления
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}
    }
}