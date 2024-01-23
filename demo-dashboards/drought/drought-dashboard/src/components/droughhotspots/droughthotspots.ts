
import {
  Component,
  OnInit,
  ElementRef
} from '@angular/core';

import Map from "@arcgis/core/Map.js";
import MapView from "@arcgis/core/views/MapView.js";



@Component({
  selector: 'droughthotspots',
  templateUrl: './droughthotspots.html',
  //  styleUrls: ['']
})
export class DroughtHotSpots implements OnInit {
  
  public view: any = null;
  
  public constructor(private myElement: ElementRef) {

  }

  public ngOnInit() {
    const mapid = "map" + new Date().getTime().toString();
    console.log();

    const el = this.myElement.nativeElement.getElementsByTagName("div")[0];
    el.setAttribute('id', mapid);
    
    const myMap = new Map({
      basemap: "streets-vector"
    });
    const view = new MapView({
      map: myMap,
      container: mapid
    });
    view.when((v)=>{
      view.ui.components = []; 
      console.log(v); 
    }); 
  }

}