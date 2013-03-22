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
import hudson.model.RootAction;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import java.util.Map;
import hudson.model.Descriptor.FormException;

/**
 * View for the teams.
 *
 * @author Tomas Westling &lt;tomas.westling&gt;
 */
@Extension
public class Teams implements RootAction {
    /** the URL name for the Teams page.*/
    public static final String TEAMS_URL_NAME = "teams";


    @Override
    public String getIconFileName() {
        return "user.png";
    }

    @Override
    public String getDisplayName() {
        return Messages.Teams_DisplayName();
    }

    @Override
    public String getUrlName() {
        return TEAMS_URL_NAME;
    }

    /**
     * Used when redirected to a team.
     * @param token the name of the team.
     * @param req the stapler request.
     * @param resp the stapler response.
     * @return the correct Team.
     */
    public Team getDynamic(String token, StaplerRequest req, StaplerResponse resp) {
        Map<String, Team> teams = PluginImpl.getInstance().getTeams();
        return teams.get(token);
    }

    /**
     * Run when the user saves a team.
     *
     * @param request the StaplerRequest.
     * @param response the StaplerResponse.
     * @throws Exception if anything goes wrong with the form.
     */
    public synchronized void doConfigSubmit(StaplerRequest request, StaplerResponse response)
            throws Exception {
        JSONObject form = request.getSubmittedForm();
        String name = form.getString("name");
        String description = form.getString("description");
        if (name == null || name.isEmpty()) {
            throw new FormException("The team name cannot be empty", "name");
        }
        Team team = PluginImpl.getInstance().getTeams().get(name);
        if (team != null) {
            throw new FormException("A team with name: " + name + " already exists!", "name");
        }
        team = new Team(name, description);
        PluginImpl.getInstance().addTeam(team);
        team.save();
        response.sendRedirect2(".");
    }

    /**
     * Form validation for name. Checks for not empty and that it is unique..
     *
     * @param value the form value.
     * @return {@link hudson.util.FormValidation#ok()} if everything is well.
     */
    public FormValidation doCheckName(@QueryParameter final String value) {
        if (value == null || value.isEmpty()) {
            return FormValidation.error("Please enter a name!");
        }
        Team team = PluginImpl.getInstance().getTeams().get(value);
        if (team != null) {
            return FormValidation.error("A team with name: " + value + " already exists!");
        }
        return FormValidation.ok();
    }
}
