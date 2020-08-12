# BetterPlayerVaults

I felt PlayerVaults/PlayerVaultsX was scummy with its monetization, so I decided
to fix it myself.

## Requirements

Lombok, Maven

## Setup

First, clone the Git repo

Next you need to make a symlink.

### Windows

Open a CMD prompt in administrator mode and cd to this directory. Run the
command:

```
mklink /D core\src\main\java\software\bigbade\playervaults\mysql "..\..\..\..\..\..\..\..\mysql\src\main\java\software\bigbade\playervaults\mysql"
```

and

```
mklink /D core\src\main\java\software\bigbade\playervaults\mongo "..\..\..\..\..\..\..\..\mongo\src\main\java\software\bigbade\playervaults\mongo"
```

### Linux

Run:

```
ln -s ./core\src\main\java\software\bigbade\playervaults\mysql ./mysql\src\main\java\software\bigbade\playervaults
```

and

```
ln -s ./core\src\main\java\software\bigbade\playervaults\mongo ./mongo\src\main\java\software\bigbade\playervaults
```

## Contributing

Contributing is always welcome.

## Branches

master: The main branch with the latest changes. PRs go here.

release: Latest release on spigotmc.org.
