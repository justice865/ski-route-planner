package edu.kit.kastel.view.command.preference;

import edu.kit.kastel.model.config.Skill;
import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;

/**
 * The Command that is specified to set the skill of the current session.
 * @author uqibg
 */
public class SetSkillCommand extends Command {
    private static final int ONE_ARG = 1;
    private static final int FIRST_ARG_INDEX = 0;

    private static final String ERROR_ARGS = "Error, set skill requires exactly one argument";
    private static final String ERROR_INVALID_SKILL = "Error, invalid skill";

    private final SkiSession session;

    /**
     * Constructor for the SetSkillCommand.
     * @param session the SkiSession to operate on
     */
    public SetSkillCommand(SkiSession session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != ONE_ARG) {
            System.err.println(ERROR_ARGS);
            return;
        }

        Skill skill = parseSkill(args[FIRST_ARG_INDEX]);
        if (skill == null) {
            System.err.println(ERROR_INVALID_SKILL);
            return;
        }
        this.session.setSkill(skill);
    }

    private Skill parseSkill(String value) {
        if (value == null) {
            return null;
        }
        String upper = value.toUpperCase();
        for (Skill skill : Skill.values()) {
            if (skill.name().equals(upper)) {
                return skill;
            }
        }
        return null;
    }
}

