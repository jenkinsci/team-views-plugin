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
import java.util.logging.Logger;

/**
 * The main thing.
 *
 * @author Tomas Westling &lt;tomas.westling@sonymobile.com&gt;
 */
public class PluginImpl extends Plugin {
    private static final Logger logger = Logger.getLogger(Team.class.getName());

    private Map<String, Team> teams;

    @Override
    public void start() throws Exception {
        super.start();
        logger.info("Starting");
        teams = new HashMap<String, Team>();
        String[] teamNames = Team.getTeamNames();
        if (teamNames == null) {
            return;
        }
        for (String teamName : teamNames) {
            Team team = new Team(teamName, null);
            teams.put(teamName, team);
        }
        logger.info("Started");
    }

    /**
     * Returns the singleton instance.
     *
     * @return the one.
     */
    public static PluginImpl getInstance() {
        return Hudson.getInstance().getPlugin(PluginImpl.class);
    }


    /**
     * Standard getter.
     *
     * @return the teams.
     */
    public Map<String, Team> getTeams() {
        if (teams == null) {
            teams = new HashMap<String, Team>();
        }
        return teams;
    }

    /**
     * Add a team to the map of teams.
     *
     * @param team the Team to add.
     */
    public void addTeam(Team team) {
        if (teams == null) {
            teams = new HashMap<String, Team>();
        }
        teams.put(team.getName(), team);
    }
}
