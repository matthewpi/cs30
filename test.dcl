test: "test"
test2: true

server {
    tps: 128.00
    max_players: 50
    message_of_the_day: "&2&lImmersive &7&l1.7.10-1.8.x &8- &cServer"
}

runtime {
    bind_ip: "0.0.0.0"
    bind_port: 25565
    online_mode: true
}

connection {
    throttle: 4000

    login {
        throttled: "&cConnection throttled, please wait before reconnecting."
        outdated_client: "&cThis server is running Immersive 1.7.10-1.8.x and does not support any lower versions."
        outdated_server: "&cThis server is running Immersive 1.7.10-1.8.x and does not support any higher versions."
    }
}

world {
    day_length: 24000

    chunk {
        x_axis: 16
        y_axis: 256
        z_axis: 16
    }

    naming {
        overworld: "${server.name}"
        nether: "${world.naming.overworld}_nether"
        end: "${world.naming.overworld}_the_end"
    }
}

permission {
    root: "immersive"

    command {
        root: "${permission.root}.command"

        help: "${permission.command.root}.help"
        stop: "${permission.command.root}.stop"
        plugins: "${permission.command.root}.plugins"
        tps: "${permission.command.root}.tps"
        version: "${permission.command.root}.version"
        gamemode: "${permission.command.root}.gamemode"
    }
}

command {
    unknown: "&fUnknown command "%cmd%", type "help" for a list of commands."
    usage: "&cUsage: /%cmd% %args% - %desc%"
    invalid_player: "&cPlayer &f%player% &cis currently not online."
    no_permission: "&cNo permission."

    logging {
        attempted_command: "%sender% attempted command "%cmd%""
        issued_command: "%sender% issued command "%cmd%""
    }

    access {
        player_only: "&cYou must be a player to run this command."
        console_only: "&cYou must be the console to run this command."
    }

    def {
        version {
            invalid_plugin: "&cSorry, %plugin% is not a valid plugin."
        }

        gamemode {
            invalid_gamemode: "&cSorry, %gamemode% is not a valid gamemode."
        }
    }
}
