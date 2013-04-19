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

import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import hudson.model.MyViewsProperty;
import hudson.model.User;
import hudson.model.View;
import org.jvnet.hudson.test.HudsonTestCase;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Matchers;

import java.util.Collection;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

//CS IGNORE MagicNumber FOR NEXT 100 LINES. REASON: Test data

/**
 * Hudson Test cases for {@link Team}.
 *
 * @author Robert Sandell &lt;robert.sandell@sonymobile.com&gt;
 */
public class TeamTest extends HudsonTestCase {

    /**
     * Tests {@link Team#doImportViewsSubmit(String, org.kohsuke.stapler.StaplerRequest,
     * org.kohsuke.stapler.StaplerResponse)}.
     *
     * @throws Exception if so
     */
    public void testDoImportViewsSubmit() throws Exception {
        FreeStyleProject p = createFreeStyleProject();
        User user = User.get("bob", true);
        MyViewsProperty property = user.getProperty(MyViewsProperty.class);
        ListView view1 = new ListView("view1");
        view1.add(p);
        property.addView(view1);
        ListView view2 = new ListView("view2");
        property.addView(view2);
        user.save();


        PluginImpl.getInstance().addTeam(new Team("Team1", "Description"));
        Team team = PluginImpl.getInstance().getTeams().get("Team1");
        StaplerRequest request = mock(StaplerRequest.class);
        StaplerResponse response = mock(StaplerResponse.class);

        team.doImportViewsSubmit(user.getId(), request, response);

        verify(response).sendRedirect2(Matchers.contains(team.getUrl()));

        TeamViewsProperty views = team.getProperty(TeamViewsProperty.class);
        Collection<View> collection = views.getViews();
        assertEquals(3, collection.size());

        boolean found1 = false;
        boolean found2 = false;
        for (View next : collection) {
            if (next.getViewName().equals("view1")) {
                found1 = true;
                assertNotSame(view1, next);
                assertEquals(1, next.getItems().size());
            } else if (next.getViewName().equals("view2")) {
                found2 = true;
                assertNotSame(view2, next);
            }
        }
        assertTrue(found1);
        assertTrue(found2);
    }
}
