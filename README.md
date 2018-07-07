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
To illustrate workflow with this tool we will use ktm-example-artifact repository. // ??

Installing example-artifact:
```
ktm install com.github.ghostbuster91:example-artifact --version 1.0.0
```
As a version `git tag` or sha of git commit can be provided.
For many artifacts(read below for explanation) this can be simplified to:

```
ktm install example-artifact
```

KTM will ask jitpack about all artifacts which contains `example-artifact` in their name.
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

### Requirements for applications
There are only two requirements for application, so it can be installed using ktm.
1. Use gradle as build system
2. Have application plugin applied

An example application can be found [here](https://github.com/ghostbuster91/ktm-example-artifact/)


TODO:
Describe info, details
