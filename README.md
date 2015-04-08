# Quipucamayoc Dashboard

ClojureScript codebase that utilizes Blessed-Contrib to visualize OSC data from the project.

# Core project details

Visit the [Quipucamayoc](http://quipucamayoc.com/) website for further details and latest information.

# Requirements

Current primary build tool: Leiningen.

Clojure: 1.6+ (1.7-alpha6 or higher recommended) <br>
ClojureScript: Latest

## Node/iojs dependencies

Can be installed with `lein`:

If using `nvm`:

```sh
nvm install iojs-v1.6.4
lein npm install
```

Replace `iojs-v1.6.4` with your desired node/iojs version.

If using system node:

``sh
lein npm install
``


# Documentation

Run `lein marg -d doc/ -f index.html` to generate the documentation.

# Running

```sh
lein cljsbuild once core
node run/connect.js sample
```

If using `nvm` just run `nvm install iojs-v1.6.4` replacing the version string with your version of node or iojs.

# License

See `LICENSE` in root of repository.

# Copyright

©2015 [Boris Kourtoukov](http://boris.kourtoukov.com/) & [Ayllu Intiwatana Team](http://quipucamayoc.com/)