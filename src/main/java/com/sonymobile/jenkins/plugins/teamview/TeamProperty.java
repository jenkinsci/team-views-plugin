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


import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Descriptor;
import hudson.model.ReconfigurableDescribable;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Extensible property of {@link Team}.
 *
 * @author Tomas Westling &lt;tomas.westling@sonymobile.com&gt;
 */
@ExportedBean
public abstract class TeamProperty implements ReconfigurableDescribable<TeamProperty>, ExtensionPoint {

    /** the {@link Team} this property is associated with.*/
    protected transient Team team;

    /**
     * Getter for the {@link TeamPropertyDescriptor}.
     *
     * @return the {@link TeamPropertyDescriptor}.
     */
    public TeamPropertyDescriptor getDescriptor() {
        return (TeamPropertyDescriptor)Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    /**
     * Returns all the registered {@link TeamPropertyDescriptor}s.
     *
     * @return a DescriptorExtensionList containing all the TeamPropertyDescriptors.
     */
    public static DescriptorExtensionList<TeamProperty, TeamPropertyDescriptor> all() {
        return Jenkins.getInstance().<TeamProperty, TeamPropertyDescriptor>getDescriptorList(TeamProperty.class);
    }

    /**
     * Reconfigures a TeamProperty from a submitted form.
     *
     * @param req the StaplerRequest.
     * @param form the submitted form.
     * @return a new TeamProperty instance.
     * @throws Descriptor.FormException if there is a problem in the submitted form.
     */
    public TeamProperty reconfigure(StaplerRequest req, JSONObject form) throws Descriptor.FormException {
        if (form == null) {
            return null;
        }
        return getDescriptor().newInstance(req, form);
    }

    /**
     * Standard setter for the team.
     *
     * @param team the Team.
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Getter for the url name, relative to nearest ancestor, not ending in a '/'.
     *
     * @return the url name for this property.
     */
    public abstract String getUrlName();

    /**
     *  {@link Descriptor} for TeamProperty.
     */
    public abstract static class TeamPropertyDescriptor extends Descriptor<TeamProperty> {

        /**
         * Creates a default instance of TeamProperty to be associated
         * with {@link Team} object that wasn't created from a persisted XML data.
         *
         * @param team the {@link Team} to create an instance for.
         * @return a new TeamProperty instance.
         */
        public abstract TeamProperty newInstance(Team team);
    }
}
