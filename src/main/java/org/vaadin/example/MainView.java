package org.vaadin.example;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A sample view to display different Grid setups.
 */
@Route
public class MainView extends VerticalLayout {

    private static final String BUTTON_TEMPLATE =
            "<vaadin-button>Template-Button-%d-[[item.number]]</vaadin-button on-click='showNotification%d'>";

    private final IntegerField textColumns;
    private final IntegerField templateButtonColumns;
    private final IntegerField componentButtonColumns;
    private final IntegerField componentComboBoxColumns;

    private final DataProvider<Integer, Void> dataProvider = DataProvider.fromCallbacks(
            query -> IntStream.range(query.getOffset(), query.getOffset() + query.getLimit()).boxed(),
            query -> 10000
    );

    private Grid<Integer> grid;

    public MainView() {
        HorizontalLayout toolbar = new HorizontalLayout();
        textColumns = new IntegerField("Text columns");
        templateButtonColumns = new IntegerField("Template columns (Button)");
        componentButtonColumns = new IntegerField("Component columns (Button)");
        componentComboBoxColumns = new IntegerField("Component columns (Combo box)");
        Button renderButton = new Button("Render", e -> createGrid());
        toolbar.add(textColumns, templateButtonColumns, componentButtonColumns, componentComboBoxColumns, renderButton);
        add(toolbar);
    }

    private void createGrid() {
        if (grid != null) {
            remove(grid);
        }
        grid = new Grid<>();

        for (int c = 0; c < getValue(textColumns); c++) {
            int columnIndex = c;
            grid.addColumn(i -> "Text-" + columnIndex + "-" + i).setAutoWidth(true).setHeader("Text-" + columnIndex);
        }
        for (int c = 0; c < getValue(templateButtonColumns); c++) {
            grid.addColumn(createButtonRenderer(c)).setAutoWidth(true).setHeader("Template-Button-" + c);
        }
        for (int c = 0; c < getValue(componentButtonColumns); c++) {
            grid.addComponentColumn(createButtonFactory(c)).setAutoWidth(true).setHeader("Component-Button-" + c);
        }
        for (int c = 0; c < getValue(componentComboBoxColumns); c++) {
            grid.addComponentColumn(createComboBoxFactory(c)).setAutoWidth(true).setHeader("Component-Combo-Box" + c);
        }

        grid.setDataProvider(dataProvider);
        add(grid);
    }

    private Integer getValue(IntegerField field) {
        return Optional.ofNullable(field.getValue()).orElse(0);
    }

    private TemplateRenderer<Integer> createButtonRenderer(int columnIndex) {
        return TemplateRenderer.<Integer>of(String.format(BUTTON_TEMPLATE, columnIndex, columnIndex))
                .withProperty("number", i -> i);
    }

    private ValueProvider<Integer, Component> createButtonFactory(int columnIndex) {
        return i -> new Button("Component-Button-" + columnIndex + "-" + i, e ->
                Notification.show("Clicked row " + i + " column " + columnIndex));
    }

    private ValueProvider<Integer, Component> createComboBoxFactory(int columnIndex) {
        return i -> {
            ComboBox<String> comboBox = new ComboBox<>("Component-Combo-Box-" + columnIndex + "-" + i);
            comboBox.setItems("One", "Two", "Three", "Four", "Five");
            comboBox.addValueChangeListener(e ->
                    Notification.show("Selected value " + e.getValue() + " in row " + i + " column " + columnIndex));
            return comboBox;
        };
    }
}
