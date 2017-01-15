package file.bytes.searcher.dict;


public class Constants {
    public static final String APPLICATION_TITLE = "Программа разделения файлов";

    public static final String BYTE = " Байт";

    public static final String LOGGING_TITLE = "Текущий статус:";

    public static final String CHOOSE_RESULT_FOLDER = "Выберите папку для сохранения файла с результатами";

    public static final String ERROR_RESULT_FOLDER_NOT_SET = "Папка для сохранения результатов не выбрана.";

    public static final String ERROR_TITLE = "Произошла ошибка!  ";
    public static final String ERROR_HEADER = "Ошибка!";

    public static final String OPERATION_SUCCESSFULL_TITLE = "Операция успешно завершена!";
    public static final String OPERATION_SUCCESSFULL_HEADER = "Успех!";

    public static final String BEGIN_CONVERTATION = "Начать поиск байтов";
    public static final String CLEAN_LOGGER = "Очистить область сообщений";

    public static final String CHOOSE_FILE = "Выбор базового файла";
    public static final String ERROR_NO_FILE = "Файл не выбран или не существует!";

    public static final String REG_EX_VALUE = "Введите значение разделителя (RegEx):";
    public static final String ERROR_REGEX_FIELD_EMPTY = "Отсутствует значение RegEX в поле ввода!";

    public static final String LOGGER_ENTERED_BYTE_ARRAY = "\n Введенный массив байт - ";

    public static final String LOGGER_FILE_SIZE = "\n Общий размер файла - ";
    public static final String LOGGER_FILE_SPLITS_COUNT = "\n Количество промежуточных шагов - ";

    public static final String LOGGER_OUTPUT_IS_TO_BIG = "\n Количество найденных совпадений слишком большое для вывода в консоль. " +
            "\n Проверьте файл с результатами в папке указанной вами ранее. " +
            "\n Количество найденных елементов = ";

    public static final String LOGGER_FOUNDED_VALUES_COUNT = "\n Количество найденных совпадений - ";

    public static final String LOGGER_CHECK_IS_OVER = "\n Проверка завершена.";

    public static final String LOGGER_NO_EQUALITY_FOUND = "\n Совпадений не выявлено.";

    public static final String LOGGER_FILE_CHECK_BEGIN = "\n Начинается анализ выбранного файла.";
    public static final String LOGGER_FILE_CHECK_PROCEED = "\n Проводится проверка части файла #";

    public static final String LOGGER_EQUALITY_FOUND_FIRST_INDEX = "\n Совпадение по ключу - Индекс начала: ";
    public static final String LOGGER_EQUALITY_FOUND_LAST_INDEX = ";  Индекс конца: ";

    public static final String CHECK_DATE = "\n Дата проверки: ";
    public static final String CHECKED_FILE = "  Проверенный файл: ";
    public static final String FIRST_INDEX = "\n Первый индекс - ";
    public static final String INDEX_OF_THE_END = "  Последний индекс - ";
}
