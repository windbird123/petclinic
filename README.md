## Overview
* https://github.com/sjrd/scalajs-sbt-vite-laminar-chartjs-example
* https://github.com/zio/zio-petclinic
* https://github.com/sherpal/FlyIOScalaJVMDemo

## Install
You need to explicitly install the following software:

* sbt, as part of [getting started with Scala](https://docs.scala-lang.org/getting-started/index.html) (or if you prefer, through [its standalone download](https://www.scala-sbt.org/download.html))
* [Node.js](https://nodejs.org/en/)

Other software will be downloaded automatically by the commands below.


## Prepare

```bash
$ npm install
```


## Development

#### frontend
* 1st terminal (scala -> javascript)  
  ```bash
  $ sbt ~fastLinkJS
  ```

* 2nd terminal (vite dev server)
  ```bash
  $ npm run dev
  ```
  
아래 주소로 frontend 확인 가능함 
http://localhost:3000 


#### backend
* 3rd terminal (backend api server)
  ```bash
  $ sbt ~backend/reStart
  ```
아래 주소로 Backend API 를 직접 확인 가능함 (UUID 형식의 petId 로 조회)
http://localhost:8080/pets/550e8400-e29b-41d4-a716-446655440000


## Production (Basic)
#### frontend  
  ```bash
  $ npm run build
  ```

You can then find the built files in the dist/ directory. You will need an HTTP server, such as python3 -m http.server, to open the files, as Vite rewrites <script> tags to prevent cross-origin requests.

#### frontend 와 backend 를 각각 분리해 배포

## Production (Simple) 
One Jar 로 만들어 backend server 에서 html, js 서빙하기   

#### backend Main class 확인 
`build.sbt` 의 아래 항목 체크(or 수정)
```
assembly / mainClass := Some("petclicnic.Main")
```

#### frontend 를 포함해 one-jar (app.jar) 로 만들기 
```
npm run build

rsync -av --delete dist backend/src/main/resources/dist
# rm -rf backend/src/main/resources/dist 
# mv dist backend/src/main/resources/dist 

sbt backend/assembly  # backend/target/scala-2.13/app.jar
```


#### Dockerfile 을 이용해 docker image 생성
```
docker build --tag demoscalaflyio .
docker run --rm -p 9000:8080 demoscalaflyio
```

