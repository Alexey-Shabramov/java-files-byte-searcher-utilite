package file.bytes.searcher.application;


import file.bytes.searcher.dict.Constants;
import file.bytes.searcher.util.AlertGuiUtil;
import file.bytes.searcher.util.FileSplitReader;
import file.bytes.searcher.util.FileUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBytesSearcherApp extends Application {
    public static javafx.scene.control.TextArea loggerTextArea;
    private static File chosenFile;
    private static File selectedResultsDirectory;
    private static List<String> errorList = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        final Label labelSelectedFile = new Label();
        Button btnOpenFileChooser = new Button();
        btnOpenFileChooser.setText(Constants.CHOOSE_FILE);

        final Label emptyLable = new Label();
        final Label emptyLable1 = new Label();
        final Label emptyLable2 = new Label();
        final Label emptyLable3 = new Label();
        final Label emptyLable4 = new Label();

        final Label resultDirectoryLabel = new Label();
        Button resultDirectoryChooser = new Button();
        resultDirectoryChooser.setText(Constants.CHOOSE_RESULT_FOLDER);

        final Label regExLabel = new Label();
        TextField regExTextField = new TextField();
        regExLabel.setText(Constants.REG_EX_VALUE);

        Button btnBeginConvertation = new Button();
        btnBeginConvertation.setText(Constants.BEGIN_CONVERTATION);

        final Label loggerLabel = new Label();
        loggerLabel.setText(Constants.LOGGING_TITLE);
        loggerTextArea = new TextArea();
        loggerTextArea.setMinHeight(300);
        loggerTextArea.setMinWidth(300);

        Button cleanLoggerButton = new Button();
        cleanLoggerButton.setText(Constants.CLEAN_LOGGER);

        cleanLoggerButton.setOnAction(event -> Platform.runLater(() -> loggerTextArea.setText(null)));

        btnOpenFileChooser.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile == null) {
                labelSelectedFile.setText(Constants.ERROR_NO_FILE);
                chosenFile = null;
            } else {
                chosenFile = selectedFile;
                labelSelectedFile.setText(selectedFile.getAbsolutePath());
            }
        });

        resultDirectoryChooser.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory == null) {
                resultDirectoryLabel.setText(Constants.ERROR_RESULT_FOLDER_NOT_SET);
                selectedResultsDirectory = null;
            } else {
                selectedResultsDirectory = selectedDirectory;
                resultDirectoryLabel.setText(selectedDirectory.getAbsolutePath());
            }
        });

        btnBeginConvertation.setOnAction(event -> {
            if (chosenFile == null) {
                errorList.add(Constants.ERROR_NO_FILE);
            }
            if (selectedResultsDirectory == null) {
                errorList.add(Constants.ERROR_RESULT_FOLDER_NOT_SET);
            }
            if (regExTextField.getText() == null || "".equals(regExTextField.getText())) {
                errorList.add(Constants.ERROR_REGEX_FIELD_EMPTY);
            }
            if (!errorList.isEmpty()) {
                AlertGuiUtil.prepareAlertMessage(errorList);
            } else {
                try {
                    new Thread(() -> {
                        try {
                            Map<Long, Long> map = FileSplitReader.readByteParts(chosenFile, regExTextField.getText());
                            if (!map.isEmpty()) {
                                if (map.size() <= 200) {
                                    Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_FOUNDED_VALUES_COUNT + map.size()));
                                    for (Map.Entry entry : map.entrySet()) {
                                        Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_EQUALITY_FOUND_FIRST_INDEX + entry.getKey() + Constants.LOGGER_EQUALITY_FOUND_LAST_INDEX + entry.getValue()));
                                    }
                                } else {
                                    Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_OUTPUT_IS_TO_BIG + map.size()));
                                }
                                FileUtil.saveResultAsFile(map, selectedResultsDirectory, chosenFile);
                                loggerTextArea.appendText(Constants.LOGGER_CHECK_IS_OVER);
                            } else {
                                Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_NO_EQUALITY_FOUND));
                            }
                        } catch (IOException e) {
                            Platform.runLater(() -> loggerTextArea.appendText(Constants.ERROR_HEADER + e));
                            AlertGuiUtil.createAlert(Constants.ERROR_HEADER + e);
                        }
                    }).start();
                } catch (Exception e) {
                    AlertGuiUtil.createAlert(Constants.ERROR_HEADER + e);
                }
            }
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(labelSelectedFile,
                btnOpenFileChooser,
                emptyLable,
                resultDirectoryLabel,
                resultDirectoryChooser,
                emptyLable1,
                regExLabel,
                regExTextField,
                emptyLable2,
                btnBeginConvertation,
                emptyLable3,
                loggerLabel,
                loggerTextArea,
                emptyLable4,
                cleanLoggerButton
        );

        StackPane root = new StackPane();
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 600, 600);

        primaryStage.setTitle(Constants.APPLICATION_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setOnHidden(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }
}
