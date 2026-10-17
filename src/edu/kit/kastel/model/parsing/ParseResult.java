package edu.kit.kastel.model.parsing;

import edu.kit.kastel.model.graph.core.SkiArea;

/**
 * The ParseResult record represents the result of parsing a ski area from a file.
 * @param area the ski area that was parsed
 * @param success indicates whether the parsing was successful
 * @param errorMessage the error message if the parsing failed, or null if the parsing was successful
 * @author uqibg
 */
public record ParseResult(SkiArea area, boolean success, String errorMessage) {
}
