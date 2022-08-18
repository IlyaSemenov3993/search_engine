package ru.skillbox.searcher.process.website.parser;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.skillbox.searcher.config.AppConfig;
import ru.skillbox.searcher.dto.FieldDTO;
import ru.skillbox.searcher.process.website.parser.contentParser.ContentParser;
import helpers.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class FieldContentParserTest {

    @Autowired
    private ContentParser contentParser;

    @Test
    public void parseTest1() throws IOException {
        String sourcePath = "src/test/resources/skillbox.html";
        String content = FileUtils.getFileContent(sourcePath);

        Map<FieldDTO, String> actual = contentParser.parse(content);
        Map<FieldDTO, String> expected = new HashMap<>();

        String titleContent = "Skillbox – образовательная платформа с онлайн-курсами курс.";
        FieldDTO titleFieldDTO = FieldDTO.newBuilder()
                .setName("title")
                .setSelector("title")
                .setWeight(1f)
                .build();
        expected.put(titleFieldDTO, titleContent);

        String h1Content = "Образовательная платформа";
        FieldDTO h1FieldDTO = FieldDTO.newBuilder()
                .setName("h1")
                .setSelector("h1")
                .setWeight(0.9f)
                .build();
        expected.put(h1FieldDTO, h1Content);

        String bodyContent = "Все курсы О Skillbox О Платформе Центр карьеры Отзывы Контакты Вакансии Школа кураторов Вебинары Все вебинары Плейлисты Расписание Медиа Компаниям Войти Программирование Дизайн Маркетинг Управление Игры Кино и Музыка Психология Общее развитие Инженерия Английский язык Другое Актуальные знания от признанных экспертов рынка для новичков и практикующих специалистов. Курса 523 Кураторов 688 Пользователей 458,314 На платформе можно получить знания по актуальным темам и востребованные навыки. Все курсы нацелены на практику: мы следим за актуальностью материала и помогаем с трудоустройством и стажировкой. Получите бесплатный доступ к онлайн‑курсам Электронная почта Получить доступ Нажимая на кнопку «Получить доступ», я соглашаюсь с и правилами акции политикой конфиденциальности Профессии Помогают полностью освоить профессию с нуля, собрать портфолио, подготовить резюме и найти работу. 157 профессий Курсы Позволяют получить конкретный навык или изучить инструмент. 366 курсов Высшее образование Бакалавриат и магистратура совместно с ведущими вузами России. Диплом государственного образца. 5 программ MBA «Лидеры изменений» Программа развития профессиональных компетенций и лидерских качеств от Skillbox и ВШМ СПбГУ. Подробнее События Skillbox Антикризисная стратегия Что происходит, чего ждать? Кто будет востребован? Разбираемся День открытых дверей Посмотрите, как Skillbox живет и работает изнутри Узнать подробнее 4 шага к переменам в карьере и жизни Изучаете материал на платформе в любое удобное время Общаетесь с экспертами и единомышленниками в Telegram Выполняете практические задания, получаете обратную связь и закрепляете знания Готовите проект и дополняете им своё портфолио Отзывы о Skillbox Ирина Черкашина Денис Бобкин Андрей Ершов Екатерина Селищева Михаил Бин Максим Чиликин Евгений Федоринов Сотрудничаем с ведущими компаниями Собираем лучшие вакансии в отрасли, готовим к интервью и рекомендуем вас компаниям-партнёрам. 73432880104 78126025210 74952910742 74954453639 74954453936 74954016931 73432880104 79310093246 79310093248 79310093249 78126025210 73512009018 Поможем в выборе! Если у вас есть вопросы о формате или вы не знаете, что выбрать, оставьте свой номер — мы позвоним, чтобы ответить на все ваши вопросы. Имя Телефон Электронная почта Отправить Нажимая на кнопку, я соглашаюсь на и обработку персональных данных с правилами пользования Платформой 8 (800) 500-05-22 Контактный центр +7 499 444 90 36 Отдел заботы о пользователях Москва, Ленинский проспект, дом 6, строение 20 Все направления Программирование Дизайн Маркетинг Управление Игры Кино и Музыка Психология Общее развитие Инженерия Английский язык Другое О Skillbox О Платформе Центр карьеры Отзывы Контакты Вакансии Школа кураторов Распродажа Вебинары Расписание Медиа Партнёрская программа Корпоративным клиентам Для работодателей Реферальная программа Участник Skolkovo Премии Рунета 2018, 2019, 2020, 2021 Оферта Оплата Правила пользования Платформой Политика конфиденциальности © Skillbox, 2022 Мы для персонализации сервисов и повышения удобства пользования сайтом. Если вы не согласны на их использование, поменяйте настройки браузера. используем файлы cookie Все вебинары Плейлисты";
        FieldDTO bodyFieldDTO = FieldDTO.newBuilder()
                .setName("body")
                .setSelector("body")
                .setWeight(0.8f)
                .build();
        expected.put(bodyFieldDTO, bodyContent);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void parseTest2() throws IOException {
        String sourcePath = "src/test/resources/skillbox.html";
        String content = FileUtils.getFileContentWithLimitLines(sourcePath, 20);

        Map<FieldDTO, String> actual = contentParser.parse(content);
        Map<FieldDTO, String> expected = new HashMap<>();

        String titleContent = "Skillbox – образовательная платформа с онлайн-курсами курс.";
        FieldDTO titleFieldDTO = FieldDTO.newBuilder()
                .setName("title")
                .setSelector("title")
                .setWeight(1f)
                .build();
        expected.put(titleFieldDTO, titleContent);

        FieldDTO h1FieldDTO = FieldDTO.newBuilder()
                .setName("h1")
                .setSelector("h1")
                .setWeight(0.9f)
                .build();
        expected.put(h1FieldDTO, "");

        FieldDTO bodyFieldDTO = FieldDTO.newBuilder()
                .setName("body")
                .setSelector("body")
                .setWeight(0.8f)
                .build();
        expected.put(bodyFieldDTO, "");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void parseNegativeTest() {
        String content = "";
        Map<FieldDTO, String> actual = contentParser.parse(content);
        Map<FieldDTO, String> expected = new HashMap<>();

        FieldDTO titleFieldDTO = FieldDTO.newBuilder()
                .setName("title")
                .setSelector("title")
                .setWeight(1f)
                .build();
        expected.put(titleFieldDTO, "");

        FieldDTO h1FieldDTO = FieldDTO.newBuilder()
                .setName("h1")
                .setSelector("h1")
                .setWeight(0.9f)
                .build();
        expected.put(h1FieldDTO, "");

        FieldDTO bodyFieldDTO = FieldDTO.newBuilder()
                .setName("body")
                .setSelector("body")
                .setWeight(0.8f)
                .build();
        expected.put(bodyFieldDTO, "");

        Assertions.assertEquals(expected, actual);


        Executable executable = () -> contentParser.parse(null);
        assertThrows(NullPointerException.class, executable);
    }

}
