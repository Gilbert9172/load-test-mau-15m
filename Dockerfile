FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
ENV JAVA_OPTS="-Xms512m -Xmx512m -XX:+UseG1GC -Xlog:gc*:file=/tmp/gc.log:time,uptime:filecount=3,filesize=10m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
