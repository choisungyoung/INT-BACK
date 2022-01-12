# INT-BACK
- **Is Not Working** SpringBoot Rest API Server

- 개발서버
    - ip : 3.17.152.174
    - port : 8080
    - API 문서 주소 : [Is Not Working](http://3.17.152.174:8080/docs/api-docs.html)
    
  
## MySQL 설치

docker run --name mysql-container -e MYSQL_ROOT_PASSWORD=aa12345^  --platform linux/amd64 -d -p 3306:3306 mysql:latest


## DB 및 사용자 생성

**create database** INT_DB default character set utf8;

**create** **user** INT_ADMIN identified **by** 'aa12345^';

**GRANT** **ALL** **privileges** **ON** INT_DB.* **TO** INT_ADMIN;


## Docker Image Build
### 로컬
bootJar 실행

docker buildx build --platform=linux/amd64 -t tjddud117/int-back:1.2.0 . 

docker push tjddud117/int-back:1.2.0


### 서버
docker stop int-back

docker run -d --rm -p 8080:8080 --name int-back tjddud117/int-back:1.2.0
