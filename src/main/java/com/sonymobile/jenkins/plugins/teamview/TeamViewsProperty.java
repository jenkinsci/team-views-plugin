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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import hudson.Extension;
import hudson.Util;
import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.model.ItemGroup;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.model.ViewGroupMixIn;
import hudson.security.ACL;
import hudson.security.Permission;
import hudson.util.FormValidation;
import hudson.views.ViewsTabBar;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.acegisecurity.Authentication;
import org.kohsuke.stapler.HttpRedirect;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerFallback;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A class that remembers team views.
 *
 * @author Tomas Westling &lt;tomas.westling&gt;
 */
@XStreamAlias("teamviewsproperty")
public final class TeamViewsProperty extends TeamProperty implements ViewGroup, Action, StaplerFallback {
    private String primaryViewName;
    /**
     * Always hold at least one view.
     */
    private CopyOnWriteArrayList<View> views = new CopyOnWriteArrayList<View>();

    private transient ViewGroupMixIn viewGroupMixIn;

    /**
     * Standard getter for the team.
     *
     * @return the Team.
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Private constructor.
     */
    private TeamViewsProperty() {
        readResolve();
    }

    /**
     * Creates the ViewGroupMixIn and sets the default view.
     *
     * @return a new TeamViewsProperty.
     */
    public Object readResolve() {
        if (views == null) {
            views = new CopyOnWriteArrayList<View>();
        }

        if (views.isEmpty()) {
            // preserve the non-empty invariant
            views.add(new ListView("Default", this));
        }

        viewGroupMixIn = new ViewGroupMixIn(this) {

            @Override
            protected List<View> views() {
                return views;
            }

            @Override
            protected String primaryView() {
                return primaryViewName;
            }

            @Override
            protected void primaryView(String name) {
                primaryViewName = name;
            }
        };

        return this;
    }

    /**
     * Standard getter for the primaryViewName.
     *
     * @return the primary view name.
     */
    public String getPrimaryViewName() {
        return primaryViewName;
    }

    /**
     * Standard setter for the primaryViewName.
     *
     * @param primaryViewName the primary view name.
     */
    public void setPrimaryViewName(String primaryViewName) {
        this.primaryViewName = primaryViewName;
    }

    ///// ViewGroup methods /////
    @Override
    public String getUrl() {
        return team.getUrl() + "views/";
    }

    @Override
    public void save() throws IOException {
        team.save();
    }

    @Override
    public Collection<View> getViews() {
        return viewGroupMixIn.getViews();
    }

    @Override
    public View getView(String name) {
        return viewGroupMixIn.getView(name);
    }

    @Override
    public boolean canDelete(View view) {
        return viewGroupMixIn.canDelete(view);
    }

    @Override
    public void deleteView(View view) throws IOException {
        viewGroupMixIn.deleteView(view);
    }

    @Override
    public void onViewRenamed(View view, String oldName, String newName) {
        viewGroupMixIn.onViewRenamed(view, oldName, newName);
    }

    /**
     * Adds a View to the ViewGroupMixIn.
     *
     * @param view the View to add.
     * @throws IOException if the View can't be added.
     */
    public void addView(View view) throws IOException {
        viewGroupMixIn.addView(view);
    }

    @Override
    public View getPrimaryView() {
        return viewGroupMixIn.getPrimaryView();
    }

    /**
     * Shows the primary view.
     *
     * @return a HttpResponse containing the primary view.
     */
    public HttpResponse doIndex() {
        return new HttpRedirect("view/" + getPrimaryView().getViewName() + "/");
    }

    /**
     * Creates a view and adds it to the ViewGroupMixIn.
     *
     * @param req the StaplerRequest.
     * @param rsp the StaplerResponse.
     * @throws IOException              if the View can't be added.
     * @throws ServletException         if the View can't be created.
     * @throws ParseException           if the View can't be created.
     * @throws Descriptor.FormException if the View can't be created.
     */
    public synchronized void doCreateView(StaplerRequest req, StaplerResponse rsp)
            throws IOException, ServletException, ParseException, Descriptor.FormException {
        addView(View.create(req, rsp, this));
    }

    @Override
    public ACL getACL() {
        return new ACL() {
            public boolean hasPermission(Authentication a, Permission permission) {
                return true;
            }
        };
    }

    @Override
    public void checkPermission(Permission permission) {
        getACL().checkPermission(permission);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return getACL().hasPermission(permission);
    }

    ///// Action methods /////
    @Override
    public String getDisplayName() {
        return Messages.TeamViews_DisplayName();
    }

    @Override
    public String getIconFileName() {
        return "user.png";
    }

    @Override
    public String getUrlName() {
        return "views";
    }

    /**
     * Checks if a private view with the given name exists. An error is returned if exists==true but the view does not
     * exist. An error is also returned if exists==false but the view does exist.
     *
     * @param value  the view name to check.
     * @param exists whether the view is supposed to exist or not.
     * @return a {@link FormValidation} describing whether the view exists or not.
     */
    public FormValidation doViewExistsCheck(@QueryParameter String value, @QueryParameter boolean exists) {
        checkPermission(View.CREATE);
        String view = Util.fixEmpty(value);
        if (view == null) {
            return FormValidation.ok();
        }
        if (exists) {
            if (getView(view) != null) {
                return FormValidation.ok();
            } else {
                return FormValidation.error(Messages.TeamViewsProperty_ViewExistsCheck_NotExist());
            }
        } else {
            if (getView(view) == null) {
                return FormValidation.ok();
            } else {
                return FormValidation.error(Messages.TeamViewsProperty_ViewExistsCheck_AlreadyExists());
            }
        }
    }


    /**
     * Descriptor for the TeamViewsProperty.
     */
    @Extension
    public static class TeamViewsPropertyDescriptor extends TeamPropertyDescriptor {

        @Override
        public String getDisplayName() {
            return Messages.TeamViews_DisplayName();
        }

        /**
         * Returns a new instance of TeamViewsProperty, provided a Team.
         *
         * @param team the provided Team.
         * @return a new TeamViewsProperty.
         */
        public TeamViewsProperty newInstance(Team team) {
            return new TeamViewsProperty();
        }
    }

    @Override
    public TeamProperty reconfigure(StaplerRequest req, JSONObject form) throws Descriptor.FormException {
        req.bindJSON(this, form);
        return this;
    }

    @Override
    public ViewsTabBar getViewsTabBar() {
        return Jenkins.getInstance().getViewsTabBar();
    }

    @Override
    public ItemGroup<? extends TopLevelItem> getItemGroup() {
        return Jenkins.getInstance();
    }

    @Override
    public List<Action> getViewActions() {
        // Jenkins.getInstance().getViewActions() are tempting but they are in a wrong scope
        return Collections.emptyList();
    }

    @Override
    public Object getStaplerFallback() {
        return getPrimaryView();
    }
}
