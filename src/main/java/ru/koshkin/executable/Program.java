package ru.koshkin.executable;

import ru.koshkin.Constants;
import ru.koshkin.components.TrafficLight;
import ru.koshkin.controllers.TrafficController;
import ru.koshkin.controllers.TrafficLightMessagesBroker;
import ru.koshkin.enums.LIGHTS;
import ru.koshkin.helpers.TrafficLightsConcurrents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class Program {

    private JButton start = new JButton("Старт"), stop = new JButton("Стоп");
    private JLabel PminLabel = new JLabel("Pmin");
    private JTextField PminField = new JTextField(String.valueOf(Constants.Pmin), 2);
    private JLabel TminLabel = new JLabel("Tmin, s");
    private JTextField TminField = new JTextField(String.valueOf(Constants.Tmin), 2);
    private JLabel TrepeatLabel = new JLabel("Trepeat, s");
    private JTextField TrepeatField = new JTextField(String.valueOf(Constants.Trepeat), 2);
    private JLabel speedSimLabel = new JLabel("Скорость симуляции");
    private JSpinner speedSimLabelSpinner = new JSpinner(new SpinnerNumberModel(Constants.simSpeed, 0.1, 20, 0.25));

    private void prepareConfigPanel(JFrame frame) {
        JPanel panel = new JPanel();
        this.stop.setEnabled(false);


        panel.add(this.PminLabel);
        panel.add(this.PminField);
        panel.add(this.TminLabel);
        panel.add(this.TminField);
        panel.add(this.TrepeatLabel);
        panel.add(this.TrepeatField);
        panel.add(this.start);
        panel.add(this.stop);
        panel.add(this.speedSimLabel);
        panel.add(this.speedSimLabelSpinner);
        frame.getContentPane().add(BorderLayout.PAGE_START, panel);
    }

    private TrafficLight[] prepareSimPanel(JFrame frame) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.gridy = 0;  // нулевая ячейка таблицы по вертикали

        JPanel northPanel = new JPanel(new GridLayout(3, 3, 5, 5));

        TrafficLight n1 = new TrafficLight(LIGHTS.RED, "n1");
        TrafficLight N = new TrafficLight(LIGHTS.RED, "N");
        TrafficLight n2 = new TrafficLight(LIGHTS.RED, "n2");

        northPanel.add(n1.getStatusLabel());
        northPanel.add(N.getStatusLabel());
        northPanel.add(n2.getStatusLabel());
        northPanel.add(n1);
        northPanel.add(N);
        northPanel.add(n2);
        northPanel.add(n1.getLightQuerySizeField());
        northPanel.add(N.getLightQuerySizeField());
        northPanel.add(n2.getLightQuerySizeField());

        JPanel westPanel = new JPanel(new GridLayout(3, 3, 5, 5));

        TrafficLight w1 = new TrafficLight(LIGHTS.RED, "w1");
        TrafficLight W = new TrafficLight(LIGHTS.RED, "W");
        TrafficLight w2 = new TrafficLight(LIGHTS.RED, "w2");

        westPanel.add(w1.getStatusLabel());
        westPanel.add(w1);
        westPanel.add(w1.getLightQuerySizeField());
        westPanel.add(W.getStatusLabel());
        westPanel.add(W);
        westPanel.add(W.getLightQuerySizeField());
        westPanel.add(w2.getStatusLabel());
        westPanel.add(w2);
        westPanel.add(w2.getLightQuerySizeField());


        JPanel eastPanel = new JPanel(new GridLayout(3, 3, 5, 5));

        TrafficLight e1 = new TrafficLight(LIGHTS.RED, "e1");
        TrafficLight E = new TrafficLight(LIGHTS.RED, "E");
        TrafficLight e2 = new TrafficLight(LIGHTS.RED, "e2");

        eastPanel.add(e1.getLightQuerySizeField());
        eastPanel.add(e1);
        eastPanel.add(e1.getStatusLabel());
        eastPanel.add(E.getLightQuerySizeField());
        eastPanel.add(E);
        eastPanel.add(E.getStatusLabel());
        eastPanel.add(e2.getLightQuerySizeField());
        eastPanel.add(e2);
        eastPanel.add(e2.getStatusLabel());

        JPanel southPanel = new JPanel(new GridLayout(3, 3, 5, 5));

        TrafficLight s1 = new TrafficLight(LIGHTS.RED, "s1");
        TrafficLight S = new TrafficLight(LIGHTS.RED, "S");
        TrafficLight s2 = new TrafficLight(LIGHTS.RED, "s2");

        southPanel.add(s1.getLightQuerySizeField());
        southPanel.add(S.getLightQuerySizeField());
        southPanel.add(s2.getLightQuerySizeField());
        southPanel.add(s1);
        southPanel.add(S);
        southPanel.add(s2);
        southPanel.add(s1.getStatusLabel());
        southPanel.add(S.getStatusLabel());
        southPanel.add(s2.getStatusLabel());

        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(northPanel, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(westPanel, constraints);
        constraints.gridx = 2;
        constraints.gridy = 1;
        panel.add(eastPanel, constraints);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(southPanel, constraints);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        TrafficLight[] allLights = new TrafficLight[]{
                n1, N, n2,
                w1, W, w2,
                e1, E, e2,
                s1, S, s2
        };
        return allLights;
    }

    public DefaultListModel prepareLogPanel(JFrame frame) {
        DefaultListModel logModel = new DefaultListModel();
        JPanel panel = new JPanel();
        JList<String> log = new JList<>(logModel);
        JScrollPane scrollPane = new JScrollPane(log,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane);

        // autoscroll
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });

        frame.getContentPane().add(BorderLayout.PAGE_END, panel);
        return logModel;
    }

    public Program() {
        TrafficLightsConcurrents.initialize();
        JFrame frame = new JFrame("Traffic Regulator Sim");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000); // размеры окна
        frame.setLocationRelativeTo(null); // окно - в центре экрана
        frame.setResizable(false);

        prepareConfigPanel(frame);
        TrafficLight[] allLights = prepareSimPanel(frame);
        DefaultListModel log = prepareLogPanel(frame);

        this.start.addActionListener((ActionEvent e) -> {
            this.TminField.setEnabled(false);
            this.PminField.setEnabled(false);
            this.TrepeatField.setEnabled(false);
            Constants.initialize(Integer.valueOf(PminField.getText()), Float.valueOf(TminField.getText()), Float.valueOf(TrepeatField.getText()), Float.valueOf(speedSimLabelSpinner.getValue().toString()));
            for (TrafficLight light : allLights) {
                light.startObservingSituation();
            }
            TrafficController.initialize(allLights);
            TrafficLightMessagesBroker.initialize(allLights, log);
            start.setEnabled(false);
            stop.setEnabled(true);
        });

        this.stop.addActionListener((ActionEvent e) -> {
            TrafficController.terminate();
            for (TrafficLight light : allLights) {
                light.stopObservingSituation();
            }
            start.setEnabled(true);
            stop.setEnabled(false);
        });

        frame.setVisible(true);

    }

    public static void main(String[] args) {
        new Program();
    }
}
