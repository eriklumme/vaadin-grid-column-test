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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * A sample view to display different Grid setups.
 */
@Route
public class MainView extends VerticalLayout {

    private static final String BUTTON_TEMPLATE =
            "<vaadin-button on-click='showNotification'>Template-Button-[[item.number]]-%d</vaadin-button>";

    private final IntegerField textColumns;
    private final IntegerField templateButtonColumns;
    private final IntegerField componentButtonColumns;
    private final IntegerField componentComboBoxColumns;

    private final DataProvider<Integer, Void> dataProvider = DataProvider.fromCallbacks(
            query -> IntStream.range(query.getOffset(), query.getOffset() + query.getLimit()).boxed(),
            query -> 10000
    );

    private final List<String> comboBoxItems = Arrays.asList("One", "Two", "Three", "Four");

    private Grid<Integer> grid;

    public MainView() {
        textColumns = new IntegerField("Text columns");
        templateButtonColumns = new IntegerField("Template columns (Button)");
        componentButtonColumns = new IntegerField("Component columns (Button)");
        componentComboBoxColumns = new IntegerField("Component columns (Combo box)");
        Button renderButton = new Button("Render", e -> createGrid());

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setAlignItems(Alignment.BASELINE);
        toolbar.setWidthFull();
        toolbar.addAndExpand(textColumns, templateButtonColumns, componentButtonColumns, componentComboBoxColumns, renderButton);
        add(toolbar);

        setSizeFull();
    }

    private void createGrid() {
        if (grid != null) {
            remove(grid);
        }
        grid = new Grid<>();

        for (int c = 0; c < getValue(textColumns); c++) {
            grid.addColumn(createText(c))
                    .setAutoWidth(true).setHeader("Text-" + c);
        }
        for (int c = 0; c < getValue(templateButtonColumns); c++) {
            grid.addColumn(createButtonRenderer(c))
                    .setAutoWidth(true).setHeader("Template-Button-" + c);
        }
        for (int c = 0; c < getValue(componentButtonColumns); c++) {
            grid.addComponentColumn(createButtonFactory(c))
                    .setAutoWidth(true).setHeader("Component-Button-" + c);
        }
        for (int c = 0; c < getValue(componentComboBoxColumns); c++) {
            grid.addComponentColumn(createComboBoxFactory(c))
                    .setAutoWidth(true).setHeader("Component-Combo-Box" + c);
        }

        grid.setDataProvider(dataProvider);
        addAndExpand(grid);
    }

    private Integer getValue(IntegerField field) {
        return Optional.ofNullable(field.getValue()).orElse(0);
    }

    private ValueProvider<Integer, String> createText(int columnIndex) {
        return i -> String.format("Text-%d-%d", i, columnIndex);
    }

    private TemplateRenderer<Integer> createButtonRenderer(int columnIndex) {
        return TemplateRenderer.<Integer>of(String.format(BUTTON_TEMPLATE, columnIndex))
                .withProperty("number", i -> i)
                .withEventHandler("showNotification", i ->
                        showNotification("template button", i, columnIndex));
    }

    private ValueProvider<Integer, Component> createButtonFactory(int columnIndex) {
        return i -> new Button(String.format("Component-Button-%d-%d", i, columnIndex),
                e -> showNotification("component button", i, columnIndex));
    }

    private ValueProvider<Integer, Component> createComboBoxFactory(int columnIndex) {
        return i -> {
            ComboBox<String> comboBox = new ComboBox<>(String.format("Component-Combo-Box-%d-%d", i, columnIndex));
            comboBox.setItems(comboBoxItems);
            comboBox.addValueChangeListener(e -> Notification.show(
                    String.format("Selected value %s in row %d component combo-box column %d", e.getValue(), i, columnIndex)));
            return comboBox;
        };
    }

    private void showNotification(String type, int rowIndex, int columnIndex) {
        Notification.show(String.format("Clicked row %d %s column %d", rowIndex, type, columnIndex));
    }
}
