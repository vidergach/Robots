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
    private RobotModel robotModel;
    private StateMap appState;
    List<StateSaveAndRestore> windows = new ArrayList<>();//список окон, которые надо сохранять
    private LocaleManager localeManager;

    /**
     * Конструктор главного окна приложения.
     */
    public MainApplicationFrame() {
        super();
        localeManager = LocaleManager.getInstance();
        stateManager = new StateFileManager("Dergach");
        robotModel = new RobotModel();

        restoreLocale();
        setTitle(localeManager.getString("window.main.title"));
        initComponents();
        restoreState();
    }

    /**
     * Восстанавливает локаль из файла состояния до создания окон
     */
    private void restoreLocale() {
        StateMap savedState = stateManager.load();
        if (savedState.containsPrefix("app")) {
            Map<String, String> appState = savedState.get("app");
            if (appState.containsKey("locale")) {
                localeManager.setLocaleFromString(appState.get("locale"));
            }
        }
        updateAllUITexts();
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

        GameWindow gameWindow = new GameWindow(robotModel);
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);
        windows.add(gameWindow);

        RobotInfoWindow robotInfoWindow = new RobotInfoWindow(robotModel);
        robotInfoWindow.setSize(300, 180);
        robotInfoWindow.setLocation(420, 10);
        addWindow(robotInfoWindow);
        windows.add(robotInfoWindow);

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
        String[] options = {
                localeManager.getString("exit.YES"),
                localeManager.getString("exit.NO")
        };
        int result = JOptionPane.showOptionDialog(
                this,
                localeManager.getString("exit.confirm"),
                localeManager.getString("exit.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

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

        Map<String, String> languageState = new HashMap<>();
        languageState.put("locale", localeManager.getLocaleString());
        appState.put("app", languageState);

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
        } catch (NumberFormatException ignored) {
        }
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
        Logger.debug(localeManager.getString("log.default.message"));
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
        menuBar.add(createTestMenu2());
        menuBar.add(createLanguageMenu());
        return menuBar;
    }

    /**
     * Создает меню выхода из приложения
     */
    private JMenu createExitMenu() {
        JMenu exitMenu = new JMenu(localeManager.getString("exit.title"));
        exitMenu.setMnemonic(KeyEvent.VK_X);
        exitMenu.add(createExitMenuItem());
        return exitMenu;
    }

    /**
     * Создает пункт меню для выхода из приложения
     */
    private JMenuItem createExitMenuItem() {
        JMenuItem exitItem = new JMenuItem(localeManager.getString("exit.title"), KeyEvent.VK_X);
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
        JMenu lafMenu = new JMenu(localeManager.getString("view.title"));
        lafMenu.setMnemonic(KeyEvent.VK_V);
        lafMenu.add(createSystemLook());
        lafMenu.add(createCrossPlatformLook());
        return lafMenu;
    }

    /**
     * Создает пункт меню для "Системная схема"
     */
    private JMenuItem createSystemLook() {
        JMenuItem item = new JMenuItem(localeManager.getString("view.system"), KeyEvent.VK_S);
        item.addActionListener(e -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));
        return item;
    }

    /**
     * Создает пункт меню для "Универсальная схема"
     */
    private JMenuItem createCrossPlatformLook() {
        JMenuItem item = new JMenuItem(localeManager.getString("view.universal"), KeyEvent.VK_S);
        item.addActionListener(e -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));
        return item;
    }

    /**
     * Создает меню с тестовыми командами
     */
    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu(localeManager.getString("tests.title"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.add(createAddLogMessageItem());
        return testMenu;
    }

    /**
     * Создает пункт меню для добавления тестового сообщения в лог
     */
    private JMenuItem createAddLogMessageItem() {
        JMenuItem item = new JMenuItem(localeManager.getString("tests.log.message"));
        item.setMnemonic(KeyEvent.VK_S);
        item.addActionListener(e -> Logger.debug(localeManager.getString("log.default.message")));
        return item;
    }

    /**
     * Устанавливает указанную схему оформления
     */
    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {
        }
    }

    /**
     * Создает меню с тестовым сообщением
     */
    private JMenu createTestMenu2() {
        JMenu testMenu = new JMenu(localeManager.getString("tests.message"));
        testMenu.setMnemonic(KeyEvent.VK_T);
        // обычное сообщение
        JMenuItem logItem = new JMenuItem(localeManager.getString("tests.log.message"), KeyEvent.VK_S);
        logItem.addActionListener(e ->
                Logger.debug(localeManager.getString("tests.title"))
        );
        testMenu.add(logItem);
        return testMenu;
    }

    /**
     * Меню изменения языка ру/англ
     */
    private JMenu createLanguageMenu() {
        JMenu langMenu = new JMenu(localeManager.getString("language.title"));

        JMenuItem ruItem = new JMenuItem(localeManager.getString("language.ru"));
        ruItem.addActionListener(e -> changeLanguage("ru"));

        JMenuItem enItem = new JMenuItem(localeManager.getString("language.en"));
        enItem.addActionListener(e -> changeLanguage("en"));

        langMenu.add(ruItem);
        langMenu.add(enItem);

        return langMenu;
    }

    /**
     * Изменяет язык
     */
    private void changeLanguage(String language) {
        localeManager.setLocaleFromString(language);
        updateAllUITexts();
    }

    /**
     * Обновляет тексты интерфейса главного окна при смене языка
     */
    @Override
    public void updateUITexts() {
        setTitle(localeManager.getString("window.main.title"));
        setJMenuBar(generateMenuBar());//пересоздаем меню с новыми текстами
        revalidate();
        repaint();
    }

    /**
     * Обновляет все текстовые элементы интерфейса в соответствии с текущей локалью
     */

    private void updateAllUITexts() {
        updateUITexts();
        for (StateSaveAndRestore window : windows) {
            window.updateUITexts();
        }
    }
}