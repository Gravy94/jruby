/*
 * Copyright (c) 2013, 2016 Oracle and/or its affiliates. All rights reserved. This
 * code is released under a tri EPL/GPL/LGPL license. You can use it,
 * redistribute it and/or modify it under the terms of the:
 *
 * Eclipse Public License version 1.0
 * GNU General Public License version 2
 * GNU Lesser General Public License version 2.1
 */
package org.jruby.truffle.language.arguments;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.profiles.BranchProfile;
import com.oracle.truffle.api.profiles.ValueProfile;
import com.oracle.truffle.api.source.SourceSection;
import org.jruby.truffle.RubyContext;
import org.jruby.truffle.language.NotProvided;
import org.jruby.truffle.language.RubyNode;

public class ReadPreArgumentNode extends RubyNode {

    private final int index;

    private final BranchProfile outOfRangeProfile = BranchProfile.create();
    private final MissingArgumentBehaviour missingArgumentBehaviour;

    private final ValueProfile argumentValueProfile = ValueProfile.createEqualityProfile();

    public ReadPreArgumentNode(RubyContext context, SourceSection sourceSection, int index,
                               MissingArgumentBehaviour missingArgumentBehaviour) {
        super(context, sourceSection);
        this.index = index;
        this.missingArgumentBehaviour = missingArgumentBehaviour;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        if (index < RubyArguments.getArgumentsCount(frame)) {
            return argumentValueProfile.profile(RubyArguments.getArgument(frame.getArguments(), index));
        }

        outOfRangeProfile.enter();

        switch (missingArgumentBehaviour) {
            case RUNTIME_ERROR:
                throw new IndexOutOfBoundsException();

            case UNDEFINED:
                return NotProvided.INSTANCE;

            case NIL:
                return nil();

            default:
                throw new UnsupportedOperationException("unknown missing argument behaviour");
        }
    }

}
