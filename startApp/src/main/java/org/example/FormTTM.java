package org.example;

import ru.oogis.searadar.api.convert.SearadarExchangeConverter;
import ru.oogis.searadar.api.message.SearadarStationMessage;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.List;

/**
 * Класс FormTTM создает форму для работы с сообщениями типа TTM.
 * Включает возможность сохранения, загрузки и декодирования сообщений.
 */
public class FormTTM {
    private JFrame frame;
    private JList<String> messageList;
    private DefaultListModel<String> listModel;
    private JTextArea contentArea;
    private JTextArea decodedArea;
    private JButton saveButton;
    private SearadarExchangeConverter converter;

    /**
     * Конструктор для создания формы TTM.
     *
     * @param converter конвертер сообщений SearadarExchangeConverter
     * @param mainFrame главное окно приложения
     */
    public FormTTM(SearadarExchangeConverter converter, JFrame mainFrame) {
        this.converter = converter;
        this.frame = new JFrame("TTM Form");
        this.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frame.setSize(800, 600);
        this.frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel contentLabel = new JLabel("Внесите новую строку:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        this.frame.add(contentLabel, gbc);

        contentArea = new JTextArea(5, 20);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setPreferredSize(new Dimension(400, 100));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        this.frame.add(contentScrollPane, gbc);

        JLabel selectLabel = new JLabel("Выберите строку:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        this.frame.add(selectLabel, gbc);

        listModel = new DefaultListModel<>();
        messageList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(messageList);
        listScrollPane.setPreferredSize(new Dimension(400, 100));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        this.frame.add(listScrollPane, gbc);

        JLabel decodedLabel = new JLabel("Расшифрованная строка:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        this.frame.add(decodedLabel, gbc);

        decodedArea = new JTextArea(10, 20);
        JScrollPane decodedScrollPane = new JScrollPane(decodedArea);
        decodedScrollPane.setPreferredSize(new Dimension(400, 100));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        this.frame.add(decodedScrollPane, gbc);

        saveButton = new JButton("Сохранить");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        this.frame.add(saveButton, gbc);

        saveButton.addActionListener(e -> {
            try {
                saveMessage("МР-231", "TTM", contentArea.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Ошибка при сохранении сообщения: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        messageList.addMouseListener(new MouseAdapter() {
            /**
             * Обрабатывает событие двойного щелчка на элементе списка сообщений.
             *
             * @param e событие мыши
             */
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedMessage = messageList.getSelectedValue();
                    try {
                        decodeMessage(selectedMessage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Ошибка при декодировании сообщения: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JButton backButton = new JButton("Назад");
        backButton.addActionListener(e -> {
            mainFrame.setVisible(true);
            frame.dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        this.frame.add(backButton, gbc);

        try {
            loadMessages("TTM");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Ошибка при загрузке сообщений: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    /**
     * Сохраняет сообщение в базу данных.
     *
     * @param format формат сообщения
     * @param type тип сообщения
     * @param content содержание сообщения
     * @throws SQLException если происходит ошибка при сохранении
     */
    private void saveMessage(String format, String type, String content) throws SQLException {
        String url = "jdbc:postgresql://" + System.getenv("DB_HOST") + ":" + System.getenv("DB_PORT") + "/" + System.getenv("DB_NAME");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        String query = "INSERT INTO messages (format, type, content) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, format);
            stmt.setString(2, type);
            stmt.setString(3, content);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Сообщение успешно сохранено!");
            loadMessages(type);

        } catch (SQLException ex) {
            throw new SQLException("Ошибка при сохранении сообщения: " + ex.getMessage(), ex);
        }
    }

    /**
     * Загружает сообщения из базы данных.
     *
     * @param type тип сообщения
     * @throws SQLException если происходит ошибка при загрузке
     */
    private void loadMessages(String type) throws SQLException {
        String url = "jdbc:postgresql://" + System.getenv("DB_HOST") + ":" + System.getenv("DB_PORT") + "/" + System.getenv("DB_NAME");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        String query = "SELECT content FROM messages WHERE type = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();

            listModel.clear();
            while (rs.next()) {
                listModel.addElement(rs.getString("content"));
            }

        } catch (SQLException ex) {
            throw new SQLException("Ошибка при загрузке сообщений: " + ex.getMessage(), ex);
        }
    }

    /**
     * Декодирует сообщение с использованием SearadarExchangeConverter.
     *
     * @param message сообщение для декодирования
     * @throws Exception если происходит ошибка при декодировании
     */
    private void decodeMessage(String message) throws Exception {
        try {
            CamelContext context = new DefaultCamelContext();
            Exchange exchange = new DefaultExchange(context);
            exchange.getIn().setBody(message);

            System.out.println("Original message: " + message);

            List<SearadarStationMessage> decodedMessages = converter.convert(exchange);

            if (decodedMessages == null || decodedMessages.isEmpty()) {
                throw new Exception("Conversion returned no results or null");
            }

            decodedArea.setText("");
            for (SearadarStationMessage msg : decodedMessages) {
                decodedArea.append(msg.toString() + "\n");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Ошибка при декодировании сообщения: " + ex.getMessage(), ex);
        }
    }
}
