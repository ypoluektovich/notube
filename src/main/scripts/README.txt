╔══════════════════════════════════════════════════════════════════════════════╗
║      _____    _ _        _    _                       _                      ║
║     |_   _|  | | |      | |  | |                     | |                     ║
║       | |  __| | | ___  | |__| | __ _ _ __ _   _  ___| |__   __ _ _ __       ║
║       | | / _` | |/ _ \ |  __  |/ _` | '__| | | |/ __| '_ \ / _` | '_ \      ║
║      _| || (_| | |  __/ | |  | | (_| | |  | |_| | (__| | | | (_| | | | |     ║
║     |_____\__,_|_|\___| |_|  |_|\__,_|_|   \__,_|\___|_| |_|\__,_|_| |_|     ║
║                                                                              ║
╠══════════════════════════════════════════════════════════════════════════════╣
║                                                                              ║
║                       On behalf of the Idle Haruchan                         ║
║                             Yanus Poluektovich                               ║
║                            with help from hi117                              ║
║                              proudly presents                                ║
║                                                                              ║
║                         _  _    _____     _                                  ║
║                        | \| |__|_   _|  _| |__  ___                          ║
║                        | .` / _ \| || || | '_ \/ -_)                         ║
║                        |_|\_\___/|_| \_,_|_.__/\___|                         ║
║                                                                              ║
║                                                                              ║
║                                    v2.0                                      ║
║                                                                              ║
╠══════════════════════════════════════════════════════════════════════════════╣
║                                                                              ║
║ NoTube is a YouTube video downloader that eliminates the need of browser and ║
║ a Flash player to watch clips on that site. It was inspired by NoYuki's      ║
║ troublesome computers that prevent me from forcing him to listen to          ║
║ Evangelion OSTs and otherwise sharing win.                                   ║
║                                                                              ║
║ Usage: notube.{bat|sh} [<clip id> [<format id>]]                             ║
║ where:     <clip id>     is the YouTube id of the clip to load               ║
║            <format id>   is the numeric id of the needed video format        ║
║                                                                              ║
║ Usage examples:                                                              ║
║      $ notube.sh                                                             ║
║      $ notube.sh OkrXE6bzjFM                                                 ║
║      $ notube.sh OkrXE6bzjFM 18                                              ║
║      $ notube.sh http://www.youtube.com/watch?v=OkrXE6bzjFM 34               ║
║                                                                              ║
╠══════════════════════════════════════════════════════════════════════════════╣
║                                                                              ║
║ NoTube has two modes of operation: graphical (GUI) and console (CLI).        ║
║                                                                              ║
║ In CLI mode, you must specify one or two arguments on the command line.      ║
║ The first argument is treated as clip ID. It may be a pure 11-character ID   ║
║ or a URL of the YouTube page with the target clip. NoTube tries to be        ║
║ flexible in this regard, and will try to parse the ID from the supplied      ║
║ string. When automatic recognition fails, you'll get an error message.       ║
║                                                                              ║
║ If no format id is specified, NoTube loads the clip info and prints a list   ║
║ of available formats with their identifiers. If, however, a format id is     ║
║ present on the command line and is valid, the video will be retrieved and    ║
║ saved in the file <clip_id>.<format_id> in the working directory.            ║
║                                                                              ║
║ In GUI mode, NoTube will attempt to register an icon in your system          ║
║ notification area ('system tray' for you Windows users). If the tray is      ║
║ unavailable, a single retrieval request will be made, and then NoTube will   ║
║ exit no matter the outcome. If, however, NoTube is able to register a tray   ║
║ icon, you can use it to download as many clips as you want without           ║
║ restarting NoTube. A popup menu is available with 'Load' and 'Quit'          ║
║ commands; and the default action of the icon is to prompt you for a          ║
║ download. For me on Ubuntu, the menu can be accessed via a right-click on    ║
║ the icon, and the default action is triggered by a double left-click.        ║
║                                                                              ║
║                              Happy watching!                                 ║
╚══════════════════════════════════════════════════════════════════════════════╝
