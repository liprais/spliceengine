/*
 * Apache Derby is a subproject of the Apache DB project, and is licensed under
 * the Apache License, Version 2.0 (the "License"); you may not use these files
 * except in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Splice Machine, Inc. has modified this file.
 *
 * All Splice Machine modifications are Copyright 2012 - 2016 Splice Machine, Inc.,
 * and are licensed to you under the License; you may not use this file except in
 * compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

package com.splicemachine.db.impl.ast;

import com.splicemachine.db.iapi.sql.compile.Visitable;
import com.splicemachine.db.iapi.sql.compile.Visitor;
import com.splicemachine.db.impl.sql.compile.QueryTreeNode;
import org.sparkproject.guava.base.Predicate;
import org.sparkproject.guava.base.Predicates;
import java.util.LinkedList;
import java.util.List;

/**
 * Collect all nodes designated by predicates. Can specify predicate for each visited node and (optionally) for each
 * visited node's parent.
 *
 * The predicate for parent nodes should handle null if used in a context where the root node might be visited.
 */
public class CollectingVisitor<T> implements Visitor {

    private final Predicate<? super Visitable> nodePred;
    private final Predicate<? super Visitable> parentPred;

    private final List<T> nodeList;

    /**
     * Constructor: predicate on node and parent node
     */
    public CollectingVisitor(Predicate<? super Visitable> nodePred, Predicate<? super Visitable> parentPred) {
        this.nodePred = nodePred;
        this.parentPred = parentPred;
        this.nodeList = new LinkedList<>();
    }

    /**
     * Constructor: predicate on node only
     */
    public CollectingVisitor(Predicate<? super Visitable> nodePred) {
        this(nodePred, Predicates.<Visitable>alwaysTrue());
    }

    @Override
    public boolean visitChildrenFirst(Visitable node) {
        return false;
    }

    @Override
    public boolean stopTraversal() {
        return false;
    }

    @Override
    public Visitable visit(Visitable node, QueryTreeNode parent) {
        if (nodePred.apply(node) && parentPred.apply(parent)) {
            nodeList.add((T) node);
        }
        return node;
    }

    @Override
    public boolean skipChildren(Visitable node) {
        return false;
    }

    public List<T> getCollected() {
        return nodeList;
    }

}