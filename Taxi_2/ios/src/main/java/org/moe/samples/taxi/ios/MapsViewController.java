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

import apple.NSObject;
import apple.corelocation.CLLocation;
import apple.corelocation.struct.CLLocationCoordinate2D;
import apple.uikit.UIImage;
import apple.uikit.UISearchBar;
import apple.uikit.UIViewController;
import apple.uikit.enums.UISearchBarStyle;
import apple.uikit.protocol.UISearchBarDelegate;
import org.moe.googlemaps.*;
import org.moe.googlemaps.protocol.GMSMapViewDelegate;
import org.moe.natj.general.Pointer;
import org.moe.natj.general.ann.RegisterOnStartup;
import org.moe.natj.objc.ObjCRuntime;
import org.moe.natj.objc.ann.ObjCClassName;
import org.moe.natj.objc.ann.Property;
import org.moe.natj.objc.ann.Selector;
import org.moe.samples.taxi.common.Parameters;

@org.moe.natj.general.ann.Runtime(ObjCRuntime.class)
@ObjCClassName("MapsViewController")
@RegisterOnStartup
public class MapsViewController extends UIViewController implements GMSMapViewDelegate, UISearchBarDelegate, LocationManagerDelegate {

    interface MapsViewControllerCallback {
        void onLocationPicked(CLLocationCoordinate2D coordinate, String address);
    }

    private MapsViewControllerCallback handler = null;

    private boolean located = false;
    private CLLocationCoordinate2D selectedCoordinate;
    private String address = "";

    protected MapsViewController(Pointer peer) {
        super(peer);
    }

    @Selector("alloc")
    public static native MapsViewController alloc();

    @Selector("init")
    public native MapsViewController init();

    @Selector("mapView")
    @Property
    public native GMSMapView getMapView();

    @Selector("searchBar")
    @Property
    public native UISearchBar getSearchBar();

    private GMSMarker marker;

    @Override
    public void viewDidLoad() {
        GMSCameraPosition camera = (GMSCameraPosition) GMSCameraPosition.cameraWithTargetZoom(selectedCoordinate, Parameters.defaultZoom);

        getMapView().setCamera(camera);
        getMapView().settings().setCompassButton(true);

        // Creates a marker with current location
        marker = GMSMarker.alloc().init();
        marker.setTitle("My location");
        marker.setSnippet("Russia");
        marker.setMap(getMapView());

        getSearchBar().setBackgroundImage(UIImage.alloc().init());
        getSearchBar().setSearchBarStyle(UISearchBarStyle.Default);

        getSearchBar().setDelegate(this);
    }

    // UISearchBarDelegate

    @Override
    public boolean searchBarShouldBeginEditing(UISearchBar searchBar) {
        System.out.println("--- searchBarShouldBeginEditing");
        return true;
    }

    @Override
    public void searchBarSearchButtonClicked(UISearchBar searchBar) {
        getSearchBar().resignFirstResponder();

        // Do the search...
        System.out.println("--- searchBar.text: " + getSearchBar().text());
    }

    @Override
    public void viewWillAppear(boolean animated) {
        System.out.println("--- viewWillAppear");
        LocationManager.getSharedManager().setDelegate(this);
        getMapView().setDelegate(this);
    }

    @Selector("doneButtonPressed:")
    public void doneButtonPressed(NSObject sender) {
        handler.onLocationPicked(selectedCoordinate, address);
        navigationController().popViewControllerAnimated(true);
    }

    @Selector("handleLocationButton:")
    public void handleLocationButton(NSObject sender) {
        CLLocation location = LocationManager.getSharedManager().currentLocation();
        if (location != null) {
            CLLocationCoordinate2D coordinate = location.coordinate();
            GMSCameraPosition camera = (GMSCameraPosition) GMSCameraPosition.cameraWithTargetZoom(coordinate, Parameters.defaultZoom);
            getMapView().animateToCameraPosition(camera);
        } else {
            System.out.println(LocationManager.LOCATION_WARNING);
        }
    }

    @Override
    @Selector("mapView:willMove:")
    public void mapViewWillMove(GMSMapView mapView, boolean gesture) {
        System.out.println("--- willMove");
    }

    @Override
    @Selector("mapView:idleAtCameraPosition:")
    public void mapViewIdleAtCameraPosition(GMSMapView mapView, GMSCameraPosition position) {
        // In our case position.target equals to projected center of the map
        selectedCoordinate = position.target();
        // TODO: turn on ActivityIndicator
        GMSGeocoder.geocoder().reverseGeocodeCoordinateCompletionHandler(selectedCoordinate, (response, error) -> {
            if (error != null) {
                handleUnknownAddress();
                return;
            }
            // TODO: turn off ActivityIndicator
            if (response != null) {
                handleGeocoderResponse(response.firstResult());
            }
        });
    }

    private void handleGeocoderResponse(GMSAddress address) {
        String addressText = address.thoroughfare();

        if (addressText == null || addressText.toLowerCase().contains("unnamed")) {
            handleUnknownAddress();
            return;
        }
        this.address = addressText;
        getSearchBar().setText(addressText);
    }

    private void handleUnknownAddress() {
        System.out.println("--- handleUnknownAddress");
        address = "Unknown address";
    }

    @Override
    public void didUpdateLocation(LocationManager manager, CLLocation location) {
        CLLocationCoordinate2D coordinate = location.coordinate();
        if (!located) {
            located = true;
        }
        marker.setPosition(coordinate);
    }

    @Override
    public void didUpdateState(LocationManager manager, LocationManager.State state) {

    }

    @Override
    public void didUpdateTrackingLocation(LocationManager manager, CLLocation location) {
        System.out.println("--- didUpdateTrackingLocation");
    }

    void pickLocation(CLLocationCoordinate2D coordinate, MapsViewControllerCallback handler) {
        selectedCoordinate = coordinate;
        this.handler = handler;
    }
}
