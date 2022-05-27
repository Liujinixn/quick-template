FROM java:8
MAINTAINER liujinxin <liujinxin@13160081226.com>
ADD quick-web-0.0.1-SNAPSHOT.jar quick-template.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","quick-template.jar"]


# docker build -t quick-template:1.0 .

# docker run -d -p 8082:8082 --name quick-template quick-template:1.0

# docker exec -it 容器ID /bin/bash
