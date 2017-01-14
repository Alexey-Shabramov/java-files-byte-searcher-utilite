package file.splitter.util;

import file.splitter.application.FileSplitterApp;
import file.splitter.dict.Constants;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;


public class FileSplitReader {
    private static final Long SELECTED_MAPPED_PART_SIZE = 10000000L;

    private static Map<Long, Long> resultsValues = new TreeMap<>();

    private static byte[] regExArray;
    private static int positionRegExArray = 0;

    private static List<Long> splitStepsList = new ArrayList<>();
    private static Long currentPosition = 0L;
    private static Integer stepsCounter = 1;

    private static List<Byte> foundedValueList = new LinkedList<>();

    private FileSplitReader() {
    }

    public static Map<Long, Long> readByteParts(File file, String regExValue) throws IOException {
        regExArray = regExValue.getBytes();
        if (file.length() <= SELECTED_MAPPED_PART_SIZE) {
            splitStepsList.add(file.length());
        } else {
            for (int i = 0; i < file.length() / SELECTED_MAPPED_PART_SIZE; i++) {
                splitStepsList.add(SELECTED_MAPPED_PART_SIZE);
            }
            splitStepsList.add(file.length() - (SELECTED_MAPPED_PART_SIZE * splitStepsList.size()));
        }
        Platform.runLater(() -> {
            FileSplitterApp.loggerTextArea.appendText(Constants.LOGGER_ENTERED_BYTE_ARRAY + Arrays.toString(regExArray));
            FileSplitterApp.loggerTextArea.appendText(Constants.LOGGER_FILE_SIZE + file.length() + Constants.BYTE);
            FileSplitterApp.loggerTextArea.appendText(Constants.LOGGER_FILE_SPLITS_COUNT + splitStepsList.size());
            FileSplitterApp.loggerTextArea.appendText(Constants.LOGGER_FILE_CHECK_BEGIN);
        });
        for (Long step : splitStepsList) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            FileChannel fileChannel = randomAccessFile.getChannel();

            Platform.runLater(() -> FileSplitterApp.loggerTextArea.appendText(Constants.LOGGER_FILE_CHECK_PROCEED + stepsCounter.toString()));

            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, currentPosition, step);
            for (int i = 0; i < buffer.limit(); i++) {
                byte value = buffer.get();
                if (!foundedValueList.isEmpty()) {
                    if (regExArray.length > foundedValueList.size()) {
                        if (regExArray.length - 1 >= positionRegExArray && regExArray[positionRegExArray] == value) {
                            foundedValueList.add(value);
                            ++positionRegExArray;
                            if (foundedValueList.size() == regExArray.length) {
                                resultsValues.put(currentPosition - (foundedValueList.size() - 1), currentPosition);
                                positionRegExArray = 0;
                                foundedValueList.clear();
                            }
                        }
                    } else {
                        positionRegExArray = 0;
                        foundedValueList.clear();
                    }
                } else {
                    if (regExArray[0] == value) {
                        foundedValueList.add(value);
                        ++positionRegExArray;
                    }
                }
                currentPosition++;
                stepsCounter++;
            }
            fileChannel.close();
            randomAccessFile.close();
        }
        stepsCounter = 1;
        cleanAllValues();
        return resultsValues;
    }

    private static void cleanAllValues() {
        regExArray = null;
        positionRegExArray = 0;
        foundedValueList.clear();
        splitStepsList.clear();
        currentPosition = 0L;
    }
}
