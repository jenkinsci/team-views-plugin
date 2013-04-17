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
package com.sonymobile.jenkins.plugins.teamview.Teams

import com.sonymobile.jenkins.plugins.teamview.PluginImpl
import com.sonymobile.jenkins.plugins.teamview.Messages

def l = namespace(lib.LayoutTagLib);
def teams = PluginImpl.getInstance().getTeams();
l.layout(title: _("Teams")) {
    l.side_panel() {
        l.task(icon: "images/24x24/new-package.png",
               href: rootURL + "/" + my.getUrlName() + "/createTeam",
              title: Messages.Teams_Create());
    }

    l.main_panel() {
        h1(_("Teams"));
        ul{
            for (team in teams.values()) {
                li{
                    a(href: team.getName(),
                    alt: _(team.getName())) {text(_(team.getName()))}
                }
            }
        }
    }
}
