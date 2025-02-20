/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.bugfinder.nullpointer;

import pascal.taie.analysis.graph.cfg.Edge;
import pascal.taie.ir.exp.Var;
import pascal.taie.ir.stmt.If;
import pascal.taie.language.type.ReferenceType;

import javax.annotation.CheckForNull;

public class IsNullConditionDecision {

    If conditionStmt;

    Var varTested;

    IsNullValue ifTrueDecision;

    IsNullValue ifFalseDecision;

    public IsNullConditionDecision(If stmt, Var varTested, IsNullValue ifTrueDecision, IsNullValue ifFalseDecision) {
        assert varTested.getType() instanceof ReferenceType;
        assert !(ifTrueDecision == null && ifFalseDecision == null);

        conditionStmt = stmt;
        this.varTested = varTested;
        this.ifTrueDecision = ifTrueDecision;
        this.ifFalseDecision = ifFalseDecision;
    }

    public If getConditionStmt() {
        return conditionStmt;
    }

    public Var getVarTested() {
        return varTested;
    }

    public boolean isEdgeFeasible(Edge.Kind edgeKind) {
        return getDecision(edgeKind) != null;
    }

    public @CheckForNull
    IsNullValue getDecision(Edge.Kind edgeKind) {
        if (edgeKind == Edge.Kind.IF_TRUE) {
            return ifTrueDecision;
        } else if (edgeKind == Edge.Kind.IF_FALSE) {
            return ifFalseDecision;
        } else {
            throw new UnsupportedOperationException("Incorrect edge kind: " + edgeKind);
        }
    }
}
