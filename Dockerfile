# Использование образа Maven для сборки Java-приложения с JDK 8
FROM maven:3.6.3-jdk-8 AS build

# Установка рабочей директории для сборочного этапа
WORKDIR /app/build

# Копирование всех файлов проекта в рабочую директорию
COPY . /app

# Выполнение команды Maven для очистки и сборки проекта
RUN mvn clean package

# Использование образа OpenJDK 8 для запуска Java-приложения
FROM openjdk:8-jre

# Обновление списка пакетов и установка необходимых зависимостей
RUN apt-get update && \
    apt-get install -y xvfb libxrender1 libxtst6 libxi6 postgresql-client x11-xkb-utils xclip && \
    apt-get clean

# Установка рабочей директории для финального образа
WORKDIR /app

# Копирование скомпилированного jar-файла из предыдущего этапа сборки
COPY --from=build /app/startApp/target/startApp-1.0-SNAPSHOT-jar-with-dependencies.jar /app/startApp.jar

# Установка переменных окружения для подключения к базе данных
ENV DB_HOST=db
ENV DB_PORT=5432
ENV DB_NAME=MR
ENV DB_USER=postgres
ENV DB_PASSWORD=Oor7iedi

# Установка переменной окружения для дисплея
ENV DISPLAY=host.docker.internal:0

# Команда для запуска приложения с эмуляцией виртуального дисплея Xvfb
CMD Xvfb host.docker.internal:0 -screen 0 1024x768x16 -ac & \
    setxkbmap us,ru -option 'grp:alt_shift_toggle' && \
    java -jar /app/startApp.jar
