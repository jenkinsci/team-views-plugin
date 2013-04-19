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
package com.sonymobile.jenkins.plugins.teamview.Team

import hudson.model.User


def l =  namespace(lib.LayoutTagLib);
def f =  namespace(lib.FormTagLib);
def st = namespace("jelly:stapler")

l.layout(title: my.getName()) {
    st.include(page:"sidepanel")
    l.main_panel() {
        h1(_("Import Views from User"));
        f.form(method: "POST", action: "importViewsSubmit") {
          f.entry(title: _("Username"), help: resURL + "/plugin/team-view/help/import-userName.html") {
            f.editableComboBox(name: "userName", items: User.all.collect {return it.id})
          }
          f.block {
            f.submit(value: _("Import"))
          }
        }
    }
}
