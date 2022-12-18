<<<<<<< HEAD
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

#### first terminal  
```bash
$ sbt ~fastLinkJS
```

#### second terminal
```bash
$ npm run dev
```


## Production
```bash
$ npm run build
```

You can then find the built files in the dist/ directory. You will need an HTTP server, such as python3 -m http.server, to open the files, as Vite rewrites <script> tags to prevent cross-origin requests.

=======
# petclinic
zio petclinic
>>>>>>> 03c3672dc6485b332d50157c5cc136db28f85fdb
