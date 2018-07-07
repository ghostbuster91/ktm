# Kotlin Tooling Manager

KTM allows you to install command line applications(gradle based) directly from github.

Installation:
```
sh -c "$(wget https://raw.githubusercontent.com/ghostbuster91/ktm/master/install.sh -O -)"
```

## Motivation
If you stripped it off, it is basically another tool to manage binary files. So the question comes, why did I create it?
- Python has pip, JavaScript has npm and yarn (and probably many others), go has go get, even linux distros have their owns,
  so I thought that it would be great if kotlin had something similar.
- I think that cli will stick with us for a while. I see many great cli applications being built using js,
  and I wanted to be able to build my own tools, but in a language I prefer.
- Next thing is that many of mentioned above tools(if not all) require developers to publish artifacts to some other repository.
  In the open source approach, where everything which is needed, to assemble the binary, is already on the internet,
  I perceive this step as an unnecessary burden and redundant work.
  Using this tool, artifacts can be installed directly from github repository.
- Last but not least, I wanted to write some kotlin code.

## Usage
```
Usage: ktm [OPTIONS] COMMAND [ARGS]...

Options:
  --version   Show the version and exit.
  -h, --help  Show this message and exit

Commands:
  install  
  aliases  
  info     
  search   
  details  
  use   
```

### Install command
To illustrate workflow with this tool we will use [ktm-example-artifact](https://github.com/ghostbuster91/ktm-example-artifact)

Installing ktm-example-artifact:
```
ktm install com.github.ghostbuster91:ktm-example-artifact --version 1.0.0
```
As a version `git tag` or sha of git commit can be provided.
For many artifacts(read below for explanation) this can be simplified to:

```
ktm install ktm-example-artifact
```

KTM will ask jitpack about all artifacts which contain `ktm-example-artifact` in their name.
If result will be a single item, ktm will proceed with installation, otherwise it will stop printing matched artifacts.
Version, if not provided, will be automatically picked up by asking jitpack about latest successful built of given artifact.

*Note: jitPack api returns only results for repositories which have at least one `git tag`*
### Use command
If you have many versions of single artifact installed you can easily switch between them using `use` command:

```
ktm use example-artifact --version 2.0.0
```

Again, version and naming resolving works the same way as during installation.

### Aliases command

If you work in a team and someone within it is developing a tool for the rest, you will find your self constantly updating it. In some cases naming resolving may not work for you and typing the fully qualified name of the artifact is tedius.

That's why the aliases were added.

To list all defined aliases:
```
ktm aliases
```

By default there is one alias added for ktm:
```
(ktm, com.github.ghostbuster91:ktm)
```

You can add your own aliases easily:
```
ktm aliases --add yourAliasName yourArtifactFullyQualifiedName
```
### Info command
To fetch information about versions of given artifact simply invoke info command. It is just a facade over jitPack api.
```
ktm info ktm
```
```
Looking for com.github.ghostbuster91:ktm......
8fb07d78d4 --> Error
e19240a0fb --> ok
0.0.1 --> Error
0.0.2 --> ok
92af9f2605 --> ok
0.0.4 --> ok
6624eefc9f --> ok
0.0.5 --> ok
```

### Requirements for applications
There are only two requirements for applications, so they can be installed using ktm.
1. Use gradle as build system
2. Have application plugin applied

An example application can be found [here](https://github.com/ghostbuster91/ktm-example-artifact/)

### KTM internals

Below is an output of `tree ~/.ktm` command just after the installation.
```
.ktm
├── aliases
├── bin
│   └── ktm -> ~/.ktm/modules/com.github.ghostbuster91/ktm/0.0.5/ktm/bin/ktm
└── modules
    └── com.github.ghostbuster91
        └── ktm
            ├── 0.0.5
            │   └── ktm
            │       ├── bin
            │       │   ├── ktm
            │       │   └── ktm.bat
            │       └── lib
            │           ├── adapter-rxjava2-2.3.0.jar
            │           ├── annotations-13.0.jar
            │           ├── clikt-1.2.0.jar
            │           ├── commons-codec-1.2.jar
            │           ├── commons-compress-1.14.jar
            │           ├── commons-httpclient-3.1.jar
            │           ├── commons-logging-1.2.jar
            │           ├── commons-vfs2-2.2.jar
            │           ├── converter-moshi-2.3.0.jar
            │           ├── converter-scalars-2.3.0.jar
            │           ├── jline-2.14.2.jar
            │           ├── kotlin-reflect-1.2.21.jar
            │           ├── kotlin-stdlib-1.2.40.jar
            │           ├── ktm-0.0.5.jar
            │           ├── logging-interceptor-3.9.1.jar
            │           ├── moshi-1.6.0.jar
            │           ├── moshi-kotlin-1.6.0.jar
            │           ├── okhttp-3.9.1.jar
            │           ├── okio-1.14.0.jar
            │           ├── reactive-streams-1.0.1.jar
            │           ├── retrofit-2.3.0.jar
            │           ├── rxjava-2.1.7.jar
            │           └── rxkotlin-2.2.0.jar
            └── ktm-0.0.5
```
All installed artifacts are stores under `~/.ktm/modules` directory. Next ktm creates symlinks for currently used versions of relevant artifacts. These symlinks are stored in `~/.ktm/bin`. 


TODO:
Describe details
