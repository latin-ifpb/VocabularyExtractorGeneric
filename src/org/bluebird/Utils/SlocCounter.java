package org.bluebird.Utils;

import org.antlr.v4.runtime.ParserRuleContext;

public class SlocCounter {

    /**
     * Conta a quantidade de linhas de uma entidade
     *
     * @param ctx Entidade da Parser Tree
     * @return Quantidade de linhas
     */
    public static int lineCount(ParserRuleContext ctx) {
        return ctx.getStop().getLine() - ctx.getStart().getLine();
    }
}
