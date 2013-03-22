/*
 * The MIT License
 *
 * Copyright 2013 Sony Mobile Communications AB. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sonymobile.jenkins.plugins.teamview;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.RootAction;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * View for the teams.
 *
 * @author Tomas Westling &lt;tomas.westling&gt;
 */
@Extension
public class Teams implements RootAction {
    @Override
    public String getIconFileName() {
        return "user.png";
    }

    @Override
    public String getDisplayName() {
        return Messages.TeamView_DisplayName();
    }

    @Override
    public String getUrlName() {
        return Messages.TeamView_UrlName();
    }

/**
     * Provides the singleton instance of this class that Jenkins has loaded. Throws an IllegalStateException if for
     * some reason the action can't be found.
     *
     * @return the instance.
     */
    public static Teams getInstance() {
        for (Action action : Hudson.getInstance().getActions()) {
            if (action instanceof Teams) {
                return (Teams)action;
            }
        }
        throw new IllegalStateException("We seem to not have been initialized!");
    }

    /**
     * Used when redirected to a team.
     * @param token the name of the team.
     * @param req the stapler request.
     * @param resp the stapler response.
     * @return the correct Team.
     */
    public Team getDynamic(String token, StaplerRequest req, StaplerResponse resp) {
        return null;
    }

}
