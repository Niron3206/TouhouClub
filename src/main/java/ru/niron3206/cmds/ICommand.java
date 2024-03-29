package ru.niron3206.cmds;

import java.util.List;

public interface ICommand {
    void handle(CommandContext ctx) ;

    String getName();

    String getHelp();

    Groups getGroup();

    default List<String> getAliases() {
        return List.of();
    }

}
