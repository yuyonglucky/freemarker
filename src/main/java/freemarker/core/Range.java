/*
 * Copyright (c) 2003 The Visigoth Software Society. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Visigoth Software Society (http://www.visigoths.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. Neither the name "FreeMarker", "Visigoth", nor any of the names of the 
 *    project contributors may be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact visigoths@visigoths.org.
 *
 * 5. Products derived from this software may not be called "FreeMarker" or "Visigoth"
 *    nor may "FreeMarker" or "Visigoth" appear in their names
 *    without prior written permission of the Visigoth Software Society.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE VISIGOTH SOFTWARE SOCIETY OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Visigoth Software Society. For more
 * information on the Visigoth Software Society, please see
 * http://www.visigoths.org/
 */

package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * A class that represents a Range between two integers.
 */
final class Range extends Expression {

    final Expression lho;
    final Expression rho;

    Range(Expression lho, Expression rho) {
        this.lho = lho;
        this.rho = rho;
    }
    
    boolean hasRho() {
        return rho != null;
    }

    TemplateModel _eval(Environment env) 
        throws TemplateException
    {
        int min = lho.evalToNumber(env).intValue();
        int max = 0;
        if (rho != null) {
            max = rho.evalToNumber(env).intValue();
            return new NumericalRange(min, max);
        }
        return new NumericalRange(min);
    }
    
    // Surely this way we can tell that it won't be a boolean without evaluating the range, but why was this important?
    boolean evalToBoolean(Environment env) throws TemplateException {
        throw new NonBooleanException(this, new NumericalRange(0, 0), env);
    }

    public String getCanonicalForm() {
        String rhs = rho != null ? rho.getCanonicalForm() : "";
        return lho.getCanonicalForm() + ".." + rhs;
    }
    
    String getNodeTypeSymbol() {
        return "..";
    }
    
    boolean isLiteral() {
        boolean rightIsLiteral = rho == null || rho.isLiteral();
        return constantValue != null || (lho.isLiteral() && rightIsLiteral);
    }
    
    protected Expression deepCloneWithIdentifierReplaced_inner(
            String replacedIdentifier, Expression replacement, ReplacemenetState replacementState) {
        return new Range(
                lho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState),
                rho.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
    }
    
    int getParameterCount() {
        return 2;
    }

    Object getParameterValue(int idx) {
        switch (idx) {
        case 0: return lho;
        case 1: return rho;
        default: throw new IndexOutOfBoundsException();
        }
    }

    ParameterRole getParameterRole(int idx) {
        return ParameterRole.forBinaryOperatorOperand(idx);
    }
    
}
