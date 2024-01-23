/**
 * 
 */
import {Component,OnInit,ElementRef} from '@angular/core';
import Map from "@arcgis/core/Map.js";
import MediaLayer from "@arcgis/core/layers/MediaLayer.js";
import ImageElement from "@arcgis/core/layers/support/ImageElement.js";
import Extent from "@arcgis/core/geometry/Extent.js";
import ExtentAndRotationGeoreference from "@arcgis/core/layers/support/ExtentAndRotationGeoreference.js";
import MapView from "@arcgis/core/views/MapView.js";
import {MapState} from 'src/services/MapState';
import {MapResult} from 'src/services/MapResult';
import {Point} from 'src/core/types/Point';


@Component({
  selector: 'mapcomponent',
  templateUrl: './mapcomponent.html',
  styleUrls: ['./mapcomponent.css']
})
export class MapComponent implements OnInit {
  public view: any = null;
  private myMap: Map = null;

  public constructor(private myElement: ElementRef, private mapState: MapState) {

  }

  /**
   * 
   */
  public ngOnInit() {
    this.initMap();
    this.mapState.getMapResult().subscribe(mapstate => {
      this.setMapImage(mapstate);  
    });
  }

  /**
   * 
   */
  private setMapImage(image: MapResult) {
    console.log(image);
    if (image != null) {
      const imageURL = image.imageURL;
      const lowerLeft = image.lowerLeft;
      const srid = image.srid;
      const upperRight = image.upperRight;
      const georeference = new ExtentAndRotationGeoreference({
        extent: new Extent({
          spatialReference: {
            wkid: srid
          },
          xmin: lowerLeft.x,
          ymin: lowerLeft.y,
          xmax: upperRight.x,
          ymax: upperRight.y
        })
      });
      const element = new ImageElement({
        georeference: georeference, 
        image: imageURL
      });
      const layer = new MediaLayer({
        title: "mapimage",
        id: "asfafsda",
        source: [element]
      });
      this.myMap.add(layer);
    } 
  }

  /**
   * 
   */
  private initMap(): void {
    const mapid = "map" + new Date().getTime().toString();
    const el = this.myElement.nativeElement.getElementsByTagName("div")[0];
    el.setAttribute('id', mapid);
    this.myMap = new Map({
      basemap: "streets-vector"
    });
    const view = new MapView({
      map: this.myMap,
      container: mapid
      , zoom: 5
      , center: [-120.43, 37.36]
    });
    this.hideComponents(view);
  }

  /**
   * 
   */
  private hideComponents(view: MapView): void {
    view.when((v) => {
      view.ui.components = [];
    });
  }

}