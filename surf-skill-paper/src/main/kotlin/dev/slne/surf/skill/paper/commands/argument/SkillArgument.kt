package dev.slne.surf.skill.paper.commands.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.skill.api.paper.Skill
import dev.slne.surf.skill.api.paper.manager.SkillManager

class SkillArgument(nodeName: String) :
    CustomArgument<Skill, String>(StringArgument(nodeName), { info ->
        SkillManager.getSkillByName(info.input)
            ?: throw CustomArgumentException.fromAdventureComponent(
                buildText {
                    appendErrorPrefix()
                    error("Die Fähigkeit wurde nicht gefunden.")
                }
            )
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection { _ ->
                SkillManager.skills.map { it.name }
            }
        )
    }
}

inline fun CommandTree.skillArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    SkillArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.skillArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    SkillArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.skillArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(SkillArgument(nodeName).setOptional(optional).apply(block))