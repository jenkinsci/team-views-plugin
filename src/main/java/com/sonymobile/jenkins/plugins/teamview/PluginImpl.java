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

import hudson.Plugin;
import hudson.model.Hudson;

import java.util.HashMap;
import java.util.Map;

/**
 * The main thing.
 *
 * @author Tomas Westling &lt;tomas.westling@sonymobile.com&gt;
 */
public class PluginImpl extends Plugin {
    private Map<String, Team> teams;

    /**
     * Standard getter.
     *
     * @return the teams.
     */
    public Map<String, Team> getTeams() {
        teams = new HashMap<String, Team>();
        teams.put("team1", new Team("team1"));
        teams.put("team2", new Team("team2"));
        return teams;
    }

    @Override
    public void start() throws Exception {
        super.start();
        load();
        if (teams == null) {
            teams = new HashMap<String, Team>();
        }
    }

    /**
     * Returns the singleton instance.
     *
     * @return the one.
     */
    public static PluginImpl getInstance() {
        return Hudson.getInstance().getPlugin(PluginImpl.class);
    }
}
