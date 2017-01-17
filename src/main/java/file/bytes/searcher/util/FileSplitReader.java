package file.bytes.searcher.util;

import file.bytes.searcher.application.FileBytesSearcherApp;
import file.bytes.searcher.dict.Constants;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;


public class FileSplitReader {
    private static final Long SELECTED_MAPPED_PART_SIZE = 10000000L;

    public static Map<Long, Long> resultsValues = new TreeMap<>();

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
            FileBytesSearcherApp.loggerTextArea.appendText(Constants.LOGGER_ENTERED_BYTE_ARRAY + Arrays.toString(regExArray));
            FileBytesSearcherApp.loggerTextArea.appendText(Constants.LOGGER_FILE_SIZE + file.length() + Constants.BYTE);
            FileBytesSearcherApp.loggerTextArea.appendText(Constants.LOGGER_FILE_SPLITS_COUNT + splitStepsList.size());
            FileBytesSearcherApp.loggerTextArea.appendText(Constants.LOGGER_FILE_CHECK_BEGIN);
        });
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

        MappedByteBuffer buffer = null;
        for (Long step : splitStepsList) {
            FileChannel fileChannel = randomAccessFile.getChannel();
            Platform.runLater(() -> FileBytesSearcherApp.loggerTextArea.appendText(Constants.LOGGER_FILE_CHECK_PROCEED + stepsCounter));
            buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, currentPosition, step);
            for (int i = 0; i < buffer.limit(); i++) {
                byte value = buffer.get();
                if (!foundedValueList.isEmpty()) {
                    //Проверка: если колличество елементов во введенном для поиска массиве больше - продолжаем поиск далее
                    if (regExArray.length >= foundedValueList.size()) {
                        if (regExArray.length - 1 >= positionRegExArray && regExArray[positionRegExArray] == value) {
                            foundedValueList.add(value);
                            ++positionRegExArray;
                            if (foundedValueList.size() == regExArray.length) {
                                resultsValues.put(currentPosition - (foundedValueList.size() - 1), currentPosition);
                                positionRegExArray = 0;
                                foundedValueList.clear();
                            }
                        } else {
                            positionRegExArray = 0;
                            foundedValueList.clear();
                            if (regExArray[positionRegExArray] == value) {
                                foundedValueList.add(value);
                                ++positionRegExArray;
                            }
                        }
                    } else {
                        positionRegExArray = 0;
                        foundedValueList.clear();
                    }
                } else {
                    if (regExArray[positionRegExArray] == value) {
                        foundedValueList.add(value);
                        ++positionRegExArray;
                    }
                }
                ++currentPosition;
                ++stepsCounter;
            }
            buffer.rewind();
        }
        if (buffer != null) closeDirectBuffer(buffer);
        randomAccessFile.close();
        cleanAllValues();
        return resultsValues;
    }

    private static void closeDirectBuffer(ByteBuffer cb) {
        if (cb == null || !cb.isDirect()) return;
        try {
            Method cleaner = cb.getClass().getMethod("cleaner");
            cleaner.setAccessible(true);
            Method clean = Class.forName("sun.misc.Cleaner").getMethod("clean");
            clean.setAccessible(true);
            clean.invoke(cleaner.invoke(cb));
        } catch (Exception ex) {
        }
        cb = null;
    }

    private static void cleanAllValues() {
        regExArray = null;
        stepsCounter = 1;
        positionRegExArray = 0;
        foundedValueList.clear();
        splitStepsList.clear();
        currentPosition = 0L;
    }

    private static void cleanCurrentPositioning() {
        positionRegExArray = 0;
        foundedValueList.clear();
    }

}
