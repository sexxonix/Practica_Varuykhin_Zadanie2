package org.example;

import org.example.searadar.mr231.convert.Mr231Converter;
import org.example.searadar.mr231_3.convert.Mr231_3Converter;
import ru.oogis.searadar.api.convert.SearadarExchangeConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Класс FormOsnovnaya создает графический интерфейс для выбора формата и типа
 * для конвертации с использованием интерфейса SearadarExchangeConverter.
 */
public class FormOsnovnaya {

    /**
     * Главный метод инициализирует и отображает главное окно приложения.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("Converter");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel formatLabel = new JLabel("Формат:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(formatLabel, gbc);

        String[] formats = {"МР-231", "МР-231-3"};
        JComboBox<String> formatComboBox = new JComboBox<>(formats);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(formatComboBox, gbc);

        JLabel typeLabel = new JLabel("Тип:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(typeLabel, gbc);

        String[] typesForMP231 = {"TTM", "RSD", "VHW"};
        String[] typesForMP231_3 = {"TTM", "RSD"};
        JComboBox<String> typeComboBox = new JComboBox<>(typesForMP231);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(typeComboBox, gbc);

        formatComboBox.addActionListener(new ActionListener() {
            /**
             * Обрабатывает событие действия при изменении выбора в комбобоксе форматов.
             *
             * @param e событие действия
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                String selectedFormat = (String) cb.getSelectedItem();
                typeComboBox.removeAllItems();
                if (selectedFormat.equals("МР-231")) {
                    for (String type : typesForMP231) {
                        typeComboBox.addItem(type);
                    }
                } else if (selectedFormat.equals("МР-231-3")) {
                    for (String type : typesForMP231_3) {
                        typeComboBox.addItem(type);
                    }
                }
            }
        });

        JButton submitButton = new JButton("Перейти");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            /**
             * Обрабатывает событие действия при нажатии кнопки "Перейти".
             *
             * @param e событие действия
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFormat = (String) formatComboBox.getSelectedItem();
                String selectedType = (String) typeComboBox.getSelectedItem();
                SearadarExchangeConverter converter = null;
                if (selectedFormat.equals("МР-231")) {
                    converter = new Mr231Converter();
                } else if (selectedFormat.equals("МР-231-3")) {
                    converter = new Mr231_3Converter();
                }
                if (selectedType.equals("TTM")) {
                    new FormTTM(converter, mainFrame);
                    mainFrame.setVisible(false);
                } else if (selectedType.equals("RSD")) {
                    new FormRSD(converter, mainFrame);
                    mainFrame.setVisible(false);
                } else if (selectedType.equals("VHW")) {
                    new FormVHW(converter, mainFrame);
                    mainFrame.setVisible(false);
                }
            }
        });

        mainFrame.add(panel);
        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);
    }
}
