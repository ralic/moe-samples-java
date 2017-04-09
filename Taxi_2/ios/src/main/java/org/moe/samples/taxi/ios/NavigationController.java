// Copyright (c) 2015, Intel Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
// 1. Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
// 3. Neither the name of the copyright holder nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.moe.samples.taxi.ios;

import apple.uikit.UIColor;
import apple.uikit.UIImage;
import apple.uikit.UINavigationController;
import apple.uikit.enums.UIBarStyle;
import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.Owned;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCClassName;
import org.moe.natj.objc.ann.Selector;

@org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("NavigationController")
@RegisterOnStartup
public class NavigationController extends UINavigationController {

    protected NavigationController(Pointer peer) {
        super(peer);
    }

    @Owned
    @Selector("alloc")
    public static native NavigationController alloc();

    @Selector("init")
    public native NavigationController init();

    @Override
    @Selector("viewDidLoad")
    public void viewDidLoad() {
        // Colours
        UIColor moeBlue = UIColor.alloc().initWithRedGreenBlueAlpha(0.0, 113 / 255.f, 197 / 255.f, 1.0);
        UIColor moeDarkBlue = UIColor.alloc().initWithRedGreenBlueAlpha(0.0, 60 / 255.f, 113 / 255.f, 1.0);
        UIColor moeAccent = UIColor.alloc().initWithRedGreenBlueAlpha(255 / 255.f, 163 / 255.f, 0.0, 1.0);

        navigationBar().setBarStyle(UIBarStyle.Black);
        navigationBar().setBarTintColor(moeBlue);
        navigationBar().setShadowImage(UIImage.alloc().init());
        navigationBar().setTranslucent(false);
        navigationBar().setTintColor(UIColor.whiteColor());

        toolbar().setTintColor(moeAccent);
    }
}
