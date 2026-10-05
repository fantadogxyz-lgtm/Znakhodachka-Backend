package com.znahodochka.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Коды бизнес-исключений
 */
@JsonFormat(shape=JsonFormat.Shape.STRING)
public enum BusinessExceptionCode {
    /**
     * ресурс не найден
     * 0
     */
    RESOURCE_NOT_FOUND_EXCEPTION,
    /**
     * невалидные данные
     * 1
     */
    INVALID_DATA,
    /**
     * Такой пользователь уже существует
     * 2
     */
    USER_ALREADY_EXISTS,
    /**
     * Невалидный файл для сохранения
     * 3
     */
    INVALID_FILE,
    /**
     * Неподдерживаемое расширение файла
     * 4
     */
    UNSUPPORTED_FILE_TYPE,
    /**
     * Неподдерживаемое количество файлов
     * 5
     */
    INVALID_ATTACHMENT_COUNT,
    /**
     * Невалидное значение Заголовка
     * 6
     */
    INVALID_TITLE,
    /**
     * Челленж должен иметь только 1 фото
     * 7
     */
    CHALLENGE_MUST_HAVE_ONE_PHOTO,
    /**
     * Челленж должен иметь дату окончания
     * 8
     */
    CHALLENGE_MUST_HAVE_END_DATE,
    /**
     * Челленж не может иметь родителя
     * 9
     */
    CHALLENGE_CANNOT_HAVE_PARENT,
    /**
     * Ответка должна иметь одно фото
     * 10
     */
    RESPONSE_MUST_HAVE_ONE_PHOTO,
    /**
     * Ответка должна иметь родителя (челленж)
     * 11
     */
    RESPONSE_MUST_HAVE_PARENT,
    /**
     * Невалидный родитель
     * 12
     */
    INVALID_PARENT,
    /**
     * Невалидные координаты
     * 13
     */
    INVALID_COORDINATES,
    /**
     * Невалидная категория
     * 14
     */
    INVALID_CATEGORY,
    /**
     * Челленж закончился
     * 15
     */
    CHALLENGE_FINISHED,
    /**
     * Уже лайкнуто
     * 16
     */
    ALREADY_LIKED,
    /**
     * Уже нет лайка
     * 17
     */
    ALREADY_DISLIKED,
    /**
     * невалидный контент для комментария
     * 17
     */
    INVALID_COMMENT_CONTENT,
    NOT_YOUR_PUBLICATION,
    /**
     * Комментарий не принадлежит пользователю
     * 18
     */
    NOT_YOUR_COMMENT


}
